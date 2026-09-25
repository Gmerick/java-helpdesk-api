# DeskFlow — interface de uso

1. Tenha Java 17 instalado ou use o kit de preparação do PC.
2. No pacote Windows, extraia tudo e abra `INICIAR.cmd`.
3. Aguarde a inicialização e abra http://localhost:8081. Se o navegador abrir antes do servidor, atualize a página.
4. Use os formulários. O botão **Como usar** apresenta o fluxo principal.

Para executar a partir do código: `mvn clean verify`, depois `java -jar target/app.jar`.
A interface HTML/CSS/JavaScript é embarcada no JAR. Não exige Node, internet ou servidor front-end em execução. Node é usado apenas nos testes de navegador.

## Dados existentes

Os arquivos continuam na pasta `data` do diretório de execução. Para conservar seus dados, execute o JAR na mesma pasta de antes. Não abra duas instâncias usando o mesmo banco. Em uma atualização, pare a aplicação e substitua apenas o JAR; preserve `data`.

## Experiência de uso

Layout responsivo, navegação por teclado, formulários com rótulos, estados vazios, confirmação de gravação e mensagens de erro. Os indicadores usam o banco real, sem números simulados. Buscas de chamados/leads abrangem a página atual; os filtros abrangem todas as páginas. Valores monetários aparecem em reais. O histórico mantém o registro das operações.

## CI e entrega contínua

O workflow CI/CD roda em pushes para `main`, pull requests e execução manual. Compila e executa testes Java, testa o fluxo real no Chromium e verifica a tela em celular. Somente após aprovação empacota o JAR e o iniciador Windows em um ZIP com SHA-256, disponível em **Actions → execução → Artifacts** por 30 dias.

O workflow de release pode ser executado manualmente com uma versão como `v1.1.0`. Ele repete os gates e publica o pacote em **Releases**, sem sobrescrever uma versão existente. Não há deploy automático em nuvem; a entrega é do programa para execução local.

## Verificação local

```bash
mvn clean verify
npm ci
npx playwright install chromium
npm run test:ui
python scripts/package.py
```

Os testes de interface usam banco H2 em memória e a porta 18081; não alteram os dados da aplicação local. Falhas geram relatório, screenshot e trace do Playwright.

## Limites

Aplicação demonstrativa local, sem login ou controle de acesso por usuário. Use dados fictícios. A interface não adiciona operações de exclusão nem desfaz conversões; respeita as regras de negócio existentes. Revisão e validação realizadas pelo mesmo executor.
