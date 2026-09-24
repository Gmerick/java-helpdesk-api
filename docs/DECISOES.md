# Decisões técnicas

- **Java 17:** baseline suficiente para records, switch expressions, HttpClient e a versão escolhida de Spring Boot.
- **Maven:** build, gerenciamento de dependências e testes com um comando. Versões transitivas gerenciadas pelo parent Spring Boot 4.0.7; o inventário usa apenas esse gerenciamento, sem iniciar Spring.
- **H2:** banco relacional embarcado para facilitar a primeira execução sem Docker. O modo padrão grava em `data/`; os testes usam banco isolado em memória.
- **SQL explícito:** demonstra consultas, constraints e JDBC. Não há JPA ou Hibernate.
- **Sem Lombok:** construtores e tipos permanecem visíveis para estudo.
- **Testes significativos:** verificam invariantes e persistência, não apenas getters. `mvn verify` inclui testes e empacotamento.
- **Esquema idempotente:** `CREATE TABLE IF NOT EXISTS` permite reiniciar sem apagar registros. Não substitui migrações versionadas em produção.
- **Sem exclusão:** o escopo preserva histórico e evita adicionar regras de remoção que não são necessárias para a demonstração.

Referências oficiais:

- [Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)
- [Spring Boot 4.0.7](https://spring.io/blog/2026/06/10/spring-boot-4-0-7-available-now)
- [Spring JDBC](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html)
- [H2](https://h2database.com/html/main.html)
- [Maven](https://maven.apache.org/guides/getting-started/)
