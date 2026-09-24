package io.github.gmerick.helpdesk;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  public record CreateTicket(
      @NotBlank @Size(max = 120) String title,
      @NotBlank @Size(max = 2000) String description,
      @NotNull Ticket.Priority priority) {}

  public record ChangeStatus(
      @NotNull Ticket.Status status, @NotBlank @Size(max = 500) String note) {}

  private final TicketService service;

  public TicketController(TicketService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<Ticket> create(@Valid @RequestBody CreateTicket request) {
    Ticket ticket = service.create(request);
    return ResponseEntity.created(URI.create("/api/tickets/" + ticket.id())).body(ticket);
  }

  @GetMapping
  List<Ticket> list(
      @RequestParam(required = false) Ticket.Status status,
      @RequestParam(defaultValue = "20") int limit,
      @RequestParam(defaultValue = "0") int offset) {
    return service.list(status, limit, offset);
  }

  @GetMapping("/{id}")
  Ticket get(@PathVariable long id) {
    return service.get(id);
  }

  @PatchMapping("/{id}/status")
  Ticket change(@PathVariable long id, @Valid @RequestBody ChangeStatus request) {
    return service.change(id, request);
  }

  @GetMapping("/{id}/history")
  List<Map<String, Object>> history(@PathVariable long id) {
    return service.history(id);
  }

  @GetMapping("/summary")
  Map<String, Long> summary() {
    return service.summary();
  }
}
