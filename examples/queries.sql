-- Quantidade por prioridade e status
SELECT priority,status,COUNT(*) AS total FROM tickets GROUP BY priority,status ORDER BY priority,status;
-- Chamados e histórico
SELECT t.id,t.title,h.from_status,h.to_status,h.note,h.created_at
FROM tickets t JOIN ticket_history h ON h.ticket_id=t.id ORDER BY t.id,h.id;
-- Chamados ainda em atendimento
SELECT id,title,created_at FROM tickets WHERE status IN ('OPEN','IN_PROGRESS') ORDER BY created_at;
