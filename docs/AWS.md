# AWS: proposta de laboratório

**Status:** arquitetura sugerida, sem recursos criados e sem implantação validada na AWS. Docker/Compose são opções de empacotamento; confira `VALIDACAO.md` para o que foi executado. Este guia demonstra conceitos associados ao AWS Cloud Practitioner Essentials.

## Mapeamento de conceitos

| Conceito | Aplicação no projeto |
| --- | --- |
| EC2 | Executar a JVM ou container da API em uma máquina de laboratório |
| EBS | Persistir o arquivo H2 fora do ciclo de vida do container |
| Security Group | Restringir acesso à porta da aplicação ao IP do laboratório |
| IAM | Conceder à instância somente as permissões realmente necessárias |
| CloudWatch | Evolução para enviar logs e criar alarmes |
| AWS Budgets | Acompanhar despesas antes de manter o laboratório ligado |

## Sequência de implantação de estudo

1. Em uma conta de laboratório, revise custos e configure um orçamento. Nenhuma gratuidade é presumida.
2. Prepare uma EC2 com JDK 17 ou Docker, disco persistente e acesso administrativo restrito.
3. Clone este repositório e execute `mvn clean verify`. Alternativamente, use o Dockerfile para empacotar.
4. Se usar Compose, `docker compose up --build -d` mantém a porta vinculada ao localhost da instância. Use um túnel SSH para a demonstração: `ssh -L 8081:127.0.0.1:8081 usuario@host-do-laboratorio`.
5. Acesse a API pelo localhost da sua máquina e execute os exemplos. Não abra o banco H2 para a internet.
6. Pare o laboratório ao concluir e confira recursos que continuam gerando custo, como discos e endereços reservados.

## Antes de produção

A API não tem autenticação ou autorização e deve usar apenas dados fictícios em laboratório. Para uma implantação pública seriam necessários controles de acesso, HTTPS, gerenciamento de segredos, backup testado, migrações, observabilidade e testes com o banco escolhido.

Migrar para RDS PostgreSQL não é apenas trocar DB_URL: é preciso adicionar o driver PostgreSQL, revisar o esquema e geração de chaves, criar migrações e executar a suíte contra esse banco. Não há suporte PostgreSQL validado nesta versão.

Referências oficiais: [EC2](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html), [Security Groups](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-security-groups.html), [Budgets](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-managing-costs.html).
