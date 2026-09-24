package io.github.gmerick.helpdesk;

import java.sql.Statement;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository {
  private final JdbcTemplate jdbc;
  private final RowMapper<Ticket> mapper =
      (rs, n) ->
          new Ticket(
              rs.getLong("id"),
              rs.getString("title"),
              rs.getString("description"),
              Ticket.Priority.valueOf(rs.getString("priority")),
              Ticket.Status.valueOf(rs.getString("status")),
              rs.getTimestamp("created_at").toLocalDateTime());

  public TicketRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public long create(String title, String description, Ticket.Priority priority) {
    KeyHolder keys = new GeneratedKeyHolder();
    jdbc.update(
        c -> {
          var p =
              c.prepareStatement(
                  "INSERT INTO tickets(title,description,priority,status) VALUES(?,?,?,'OPEN')",
                  Statement.RETURN_GENERATED_KEYS);
          p.setString(1, title);
          p.setString(2, description);
          p.setString(3, priority.name());
          return p;
        },
        keys);
    return Objects.requireNonNull(keys.getKeys()).get("ID") instanceof Number id
        ? id.longValue()
        : Objects.requireNonNull(keys.getKey()).longValue();
  }

  public Optional<Ticket> find(long id) {
    return jdbc.query("SELECT * FROM tickets WHERE id=?", mapper, id).stream().findFirst();
  }

  public List<Ticket> list(Ticket.Status status, int limit, int offset) {
    if (status == null)
      return jdbc.query(
          "SELECT * FROM tickets ORDER BY id DESC LIMIT ? OFFSET ?", mapper, limit, offset);
    return jdbc.query(
        "SELECT * FROM tickets WHERE status=? ORDER BY id DESC LIMIT ? OFFSET ?",
        mapper,
        status.name(),
        limit,
        offset);
  }

  public boolean change(long id, Ticket.Status expected, Ticket.Status next) {
    return jdbc.update(
            "UPDATE tickets SET status=? WHERE id=? AND status=?", next.name(), id, expected.name())
        == 1;
  }

  public void audit(long id, String from, Ticket.Status to, String note) {
    jdbc.update(
        "INSERT INTO ticket_history(ticket_id,from_status,to_status,note) VALUES(?,?,?,?)",
        id,
        from,
        to.name(),
        note);
  }

  public List<Map<String, Object>> history(long id) {
    return jdbc.query(
        "SELECT * FROM ticket_history WHERE ticket_id=? ORDER BY id",
        (rs, n) -> {
          Map<String, Object> row = new LinkedHashMap<>();
          row.put("id", rs.getLong("id"));
          row.put("from", rs.getString("from_status"));
          row.put("to", rs.getString("to_status"));
          row.put("note", rs.getString("note"));
          row.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime());
          return row;
        },
        id);
  }

  public Map<String, Long> summary() {
    Map<String, Long> counts = new LinkedHashMap<>();
    for (var status : Ticket.Status.values()) counts.put(status.name(), 0L);
    jdbc.query(
        "SELECT status,COUNT(*) AS total FROM tickets GROUP BY status",
        (org.springframework.jdbc.core.RowCallbackHandler)
            rs -> counts.put(rs.getString("status"), rs.getLong("total")));
    return counts;
  }
}
