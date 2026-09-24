# Roteiro de apresentação

## Antes da entrevista

Compile com `mvn clean verify`. Abra Controller, Service, Repository, schema.sql e TicketApiTest. Inicie o JAR e deixe outro terminal pronto com `python scripts/demo.py` ou as requisições do arquivo HTTP na IDE.

## Em 5 minutos

| Tempo | Demonstração |
| --- | --- |
| 0–1 min | Problema: acompanhar chamados e impedir mudanças incoerentes de status |
| 1–2 min | Execute `python scripts/demo.py`: criação, atendimento, resolução e fechamento |
| 2–3 min | Mostre a rejeição de transição e o histórico preservado |
| 3–4 min | Abra `Ticket.Status.canMoveTo` e `TicketService.change` |
| 4–5 min | Mostre teste de rollback e explique os códigos HTTP |

Para 10 minutos, crie um chamado manualmente pelo PowerShell, filtre por OPEN e mostre o JOIN de auditoria em `examples/queries.sql`.

## Fala de apoio — adapte após estudar

“Usei minha familiaridade com suporte como ponto de partida para uma API de chamados. O foco foi aprender a separar contrato HTTP, regras de negócio e SQL. Uma mudança de status só é válida se respeitar o fluxo do chamado e deixar uma justificativa no histórico. Atualização e auditoria são transacionais. Os testes sobem um servidor HTTP real e conferem a resposta e o banco.”

## Perguntas e respostas

**O que Spring Boot faz?** Inicializa a aplicação, configura servidor, JSON, validação, fonte de dados e transações a partir das dependências e configurações.

**Por que Controller, Service e Repository?** O Controller recebe a requisição; o Service decide a regra; o Repository realiza a persistência. Isso evita colocar toda a lógica no endpoint.

**Por que 409 e não 500?** A transição proibida é um conflito de negócio conhecido, não uma falha inesperada do servidor. Dados inválidos recebem 400 e registros ausentes recebem 404.

**Como lida com concorrência?** O UPDATE usa `WHERE id=? AND status=?`. Se outra operação mudar o status antes, a atualização afeta zero linhas e devolve conflito. Não é um histórico de versão completo: uma evolução seria um campo version para detectar também ciclos de status.

**O histórico está seguro?** É consistente com o status no banco, mas não registra identidade autenticada. Autenticação, autorização e auditoria por usuário são evoluções.

**Como testou rollback?** Um teste cria uma constraint que força a falha do INSERT de auditoria e verifica que o status permanece OPEN.

## Exercício de domínio

Adicione filtro por prioridade, preservando o filtro por status. Escreva testes combinando os dois filtros e um teste sem resultados. Apresente o commit da sua alteração.
