package io.github.gmerick.helpdesk;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {
  private final TicketRepository repository;

  public TicketService(TicketRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public Ticket create(TicketController.CreateTicket request) {
    long id =
        repository.create(request.title().trim(), request.description().trim(), request.priority());
    repository.audit(id, null, Ticket.Status.OPEN, "Chamado criado");
    return get(id);
  }

  public Ticket get(long id) {
    return repository
        .find(id)
        .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Chamado não encontrado."));
  }

  public List<Ticket> list(Ticket.Status status, int limit, int offset) {
    if (limit < 1 || limit > 100 || offset < 0)
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, "limit deve ser de 1 a 100 e offset não negativo.");
    return repository.list(status, limit, offset);
  }

  @Transactional
  public Ticket change(long id, TicketController.ChangeStatus request) {
    Ticket current = get(id);
    if (!current.status().canMoveTo(request.status()))
      throw new BusinessException(
          HttpStatus.CONFLICT,
          "Transição de " + current.status() + " para " + request.status() + " não permitida.");
    if (!repository.change(id, current.status(), request.status()))
      throw new BusinessException(
          HttpStatus.CONFLICT, "Chamado alterado por outra operação. Consulte novamente.");
    repository.audit(id, current.status().name(), request.status(), request.note().trim());
    return get(id);
  }

  public List<Map<String, Object>> history(long id) {
    get(id);
    return repository.history(id);
  }

  public Map<String, Long> summary() {
    return repository.summary();
  }
}
