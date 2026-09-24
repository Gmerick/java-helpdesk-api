# Escopo e critérios de aceite

Objetivo: criar `java-helpdesk-api` como projeto Java júnior demonstrável em entrevista, com código, testes e documentação. Projeto novo, sem alterações preexistentes. Publicação na conta Gmerick autorizada pelo pedido.

## Critérios

| Critério | Verificação |
| --- | --- |
| Compila em Java 17 e gera JAR executável | `mvn clean verify` e `java -jar target/app.jar` |
| Regras de negócio protegem caminhos válidos e inválidos | Testes em `src/test/java` |
| Dados são gravados em banco relacional | Testes JDBC e demonstração com banco H2 |
| Pode ser apresentado em 5 a 10 minutos | `docs/APRESENTACAO.md` e exemplos executáveis |
| Arquivos publicados correspondem à entrega validada | Conferência do commit e dos arquivos no GitHub |

## Fora do escopo

Uso em produção, dados reais, integração com sistemas empresariais, implantação de recursos pagos e garantia de ausência de bugs. A revisão técnica é uma autorrevisão do mesmo executor que implementou o código.

## Evidências

Resultados executados são registrados em `docs/VALIDACAO.md`. O fluxo Java CI executa o build em push para main e pull requests; consulte a aba Actions para o resultado do commit atual.
