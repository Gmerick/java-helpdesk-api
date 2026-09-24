package io.github.gmerick.helpdesk;

import java.time.LocalDateTime;

public record Ticket(
    long id,
    String title,
    String description,
    Priority priority,
    Status status,
    LocalDateTime createdAt) {
  public enum Priority {
    LOW,
    MEDIUM,
    HIGH
  }

  public enum Status {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    public boolean canMoveTo(Status next) {
      return switch (this) {
        case OPEN -> next == IN_PROGRESS;
        case IN_PROGRESS -> next == RESOLVED;
        case RESOLVED -> next == CLOSED || next == IN_PROGRESS;
        case CLOSED -> false;
      };
    }
  }
}
