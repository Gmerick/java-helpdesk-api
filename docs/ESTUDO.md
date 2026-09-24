# Guia de estudo para a entrevista

Este é um projeto didático de portfólio, criado com apoio de IA. Antes de apresentá-lo como domínio técnico, execute, leia o código e faça uma pequena alteração por conta própria. Não representa um sistema em produção ou resultados medidos em uma empresa.

## Ordem de leitura

1. README: problema, funcionalidades e execução.
2. Modelos: dados, tipos, invariantes e enums.
3. Service: regras de negócio e decisões.
4. Repository: SQL parametrizado e persistência.
5. Entrada (CLI ou Controller): argumentos/JSON e respostas.
6. Testes: observe entradas, saídas esperadas e efeitos no banco.
7. APRESENTACAO: ensaie com o programa em execução.

## Conceitos para explicar com suas palavras

- **Encapsulamento:** separar responsabilidade e proteger regras; campos privados e dependências passadas no construtor.
- **Record:** representação imutável de um conjunto de dados. Não é uma entidade JPA neste projeto.
- **List / Map:** lista de registros versus associação de chaves a valores. Streams processam essas coleções.
- **JDBC:** API Java de acesso a bancos. PreparedStatement separa SQL de parâmetros; JdbcTemplate reduz código repetitivo usando JDBC.
- **Transação:** operações relacionadas confirmam juntas ou são desfeitas em rollback.
- **Constraints:** a aplicação orienta o usuário, mas o banco também precisa proteger seus dados.
- **Injeção de dependência:** receber um colaborador no construtor facilita organização e substituição.
- **Teste de integração:** precisa executar componentes reais do fluxo. Um mock isolado não comprova HTTP nem gravação no banco.
- **Git:** commit é um registro de alterações. Antes de alterar: `git checkout -b estudo/minha-melhoria`; depois: `git diff`, testes, `git add` e `git commit`.

## Exercício obrigatório antes da entrevista

Escolha uma melhoria pequena sugerida no roteiro, escreva um teste que falhe sem ela, implemente, execute `mvn clean verify` e explique o diff. Isso transforma leitura em prática verificável.

## Limites honestos

Não dizer que existe autenticação, implantação na AWS, integração com CRM real ou monitoramento se essas funções não foram implementadas. Explique o que está funcionando e o que seria uma evolução.
