# Validação executada

Data: 24/09/2026. Versão: 1.0.0. Ambiente: Linux x86_64, Amazon Corretto 17.0.20 e Maven 3.9.11. Para reproduzir o build: `mvn --batch-mode --no-transfer-progress clean verify` na raiz deste repositório.

**Resultado local: 10 testes, zero falhas, zero erros e zero testes ignorados; BUILD SUCCESS.**

| Critério | Evidência |
| --- | --- |
| Compilação e empacotamento | `mvn clean verify`, código 0, JAR `target/app.jar` gerado |
| API HTTP e regras | 10 testes com servidor real em porta aleatória e H2 isolado |
| JAR executável | JAR iniciado em JVM separada, fora do contexto de testes |
| Demo documentado | `python scripts/demo.py URL_LOCAL` executado com sucesso |
| Persistência após reinício | Mesmo banco H2 em arquivo, aplicação encerrada e reiniciada, registros idênticos na nova leitura HTTP |
| SQL documentado | `examples/queries.sql` executado no mesmo banco H2 após encerrar o JAR |

## Arquivos de evidência

- [Resumo extraído dos relatórios JUnit](evidence/tests.txt)
- [Saída real da demonstração](evidence/demo.txt)
- [SHA-256 dos arquivos de implementação validados](evidence/source-sha256.txt)
- [Resultado das consultas SQL](evidence/sql.txt)

Os testes usam banco real H2, sem mocks de repository. Nos projetos REST, as requisições são enviadas por Java HttpClient a um servidor HTTP iniciado pelo teste. A demonstração adicional usa o JAR empacotado e banco em arquivo. O manifesto identifica os arquivos de implementação; os documentos e o próprio manifesto não fazem parte dele.

## Revisão e correções

Autorrevisão pelo mesmo executor responsável pela implementação, sem alegação de revisão independente. Foram examinadas validação de entrada, SQL parametrizado, constraints, rollback, estados, exemplos e arquivos destinados à publicação. O inventário teve um conflito inicial de herança de configuração do plugin de empacotamento, corrigido antes do build final. Os 29 testes da coleção de três projetos passaram após a formatação final.

## Limites da verificação

Não executados: Windows/PowerShell nativo, Docker/Compose, AWS e bancos diferentes de H2. Não houve teste de carga, auditoria de segurança ou validação de uso em produção. Os exemplos PowerShell são uma alternativa de uso; o demo Python e o fluxo HTTP foram executados no Linux.

O workflow Java CI está incluído para compilar e testar pushes em main e pull requests. Seu resultado remoto deve ser conferido na aba Actions do commit; os resultados acima são da execução local, não uma simulação de CI remoto.
