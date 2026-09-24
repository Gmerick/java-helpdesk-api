package io.github.gmerick.helpdesk;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.datasource.url=jdbc:h2:mem:helpdesk_test;DB_CLOSE_DELAY=-1"})
class TicketApiTest extends HttpTestSupport {
  @Autowired JdbcTemplate jdbc;

  @BeforeEach
  void clean() {
    jdbc.update("DELETE FROM ticket_history");
    jdbc.update("DELETE FROM tickets");
  }

  Result create() throws Exception {
    return call(
        "POST",
        "/api/tickets",
        "{\"title\":\" VPN indisponível \",\"description\":\"Rede de"
            + " laboratório\",\"priority\":\"HIGH\"}");
  }

  Result change(long id, String status) throws Exception {
    return call(
        "PATCH",
        "/api/tickets/" + id + "/status",
        "{\"status\":\"" + status + "\",\"note\":\"Diagnóstico fictício\"}");
  }

  @Test
  void completeLifecycleIsPersistedWithAudit() throws Exception {
    var created = create();
    assertEquals(201, created.status());
    long id = created.body().get("id").asLong();
    assertEquals("/api/tickets/" + id, created.headers().firstValue("Location").orElseThrow());
    assertEquals("VPN indisponível", created.body().get("title").asText());
    for (String status : new String[] {"IN_PROGRESS", "RESOLVED", "CLOSED"})
      assertEquals(200, change(id, status).status());
    assertEquals("CLOSED", call("GET", "/api/tickets/" + id, null).body().get("status").asText());
    assertEquals(4, call("GET", "/api/tickets/" + id + "/history", null).body().size());
    assertEquals(
        "CLOSED", jdbc.queryForObject("SELECT status FROM tickets WHERE id=?", String.class, id));
  }

  @Test
  void invalidTransitionDoesNotWriteAudit() throws Exception {
    long id = create().body().get("id").asLong();
    assertEquals(409, change(id, "CLOSED").status());
    assertEquals(1, call("GET", "/api/tickets/" + id + "/history", null).body().size());
    assertEquals(
        "OPEN", jdbc.queryForObject("SELECT status FROM tickets WHERE id=?", String.class, id));
  }

  @Test
  void resolvedCanReopenButClosedCannot() throws Exception {
    long id = create().body().get("id").asLong();
    change(id, "IN_PROGRESS");
    change(id, "RESOLVED");
    assertEquals(200, change(id, "IN_PROGRESS").status());
    change(id, "RESOLVED");
    change(id, "CLOSED");
    assertEquals(409, change(id, "IN_PROGRESS").status());
  }

  @Test
  void validatesRequiredFields() throws Exception {
    var result =
        call("POST", "/api/tickets", "{\"title\":\" \",\"description\":\"x\",\"priority\":null}");
    assertEquals(400, result.status());
    assertTrue(result.body().get("errors").has("title"));
    assertTrue(result.body().get("errors").has("priority"));
    assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM tickets", Integer.class));
  }

  @Test
  void malformedJsonEnumsAndIdsReturn400() throws Exception {
    assertEquals(400, call("POST", "/api/tickets", "{").status());
    assertEquals(400, call("GET", "/api/tickets?status=INVALID", null).status());
    assertEquals(400, call("GET", "/api/tickets/abc", null).status());
  }

  @Test
  void missingRecordsReturn404() throws Exception {
    assertEquals(404, call("GET", "/api/tickets/999999", null).status());
    assertEquals(404, call("GET", "/api/tickets/999999/history", null).status());
    assertEquals(404, change(999999, "IN_PROGRESS").status());
  }

  @Test
  void paginationAndFilterWork() throws Exception {
    long id = create().body().get("id").asLong();
    create();
    change(id, "IN_PROGRESS");
    assertEquals(1, call("GET", "/api/tickets?status=OPEN", null).body().size());
    assertEquals(1, call("GET", "/api/tickets?limit=1&offset=1", null).body().size());
    assertEquals(400, call("GET", "/api/tickets?limit=101", null).status());
    assertEquals(400, call("GET", "/api/tickets?offset=-1", null).status());
  }

  @Test
  void summaryIncludesZeroStatuses() throws Exception {
    create();
    var summary = call("GET", "/api/tickets/summary", null);
    assertEquals(1, summary.body().get("OPEN").asLong());
    assertEquals(0, summary.body().get("CLOSED").asLong());
  }

  @Test
  void failedAuditRollsBackStatusChange() throws Exception {
    long id = create().body().get("id").asLong();
    jdbc.execute("ALTER TABLE ticket_history ADD CONSTRAINT short_note CHECK(LENGTH(note)<20)");
    try {
      assertEquals(
          409,
          call(
                  "PATCH",
                  "/api/tickets/" + id + "/status",
                  "{\"status\":\"IN_PROGRESS\",\"note\":\"Uma nota de auditoria longa para provocar"
                      + " falha\"}")
              .status());
      assertEquals(
          "OPEN", jdbc.queryForObject("SELECT status FROM tickets WHERE id=?", String.class, id));
      assertEquals(
          1,
          jdbc.queryForObject(
              "SELECT COUNT(*) FROM ticket_history WHERE ticket_id=?", Integer.class, id));
    } finally {
      jdbc.execute("ALTER TABLE ticket_history DROP CONSTRAINT short_note");
    }
  }

  @Test
  void compareAndSetRejectsStaleState() throws Exception {
    long id = create().body().get("id").asLong();
    assertEquals(200, change(id, "IN_PROGRESS").status());
    assertEquals(409, change(id, "IN_PROGRESS").status());
    assertEquals(2, call("GET", "/api/tickets/" + id + "/history", null).body().size());
  }
}
