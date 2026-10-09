# Validação SQL dos dashboards

Execute na raiz do projeto com Node 22.13 ou superior:

```powershell
node tools/dashboard-validation/validate-sql.cjs
```

Usa `node:sqlite`, sem instalação de pacotes. Parte do schema Room 3 versionado, executa os comandos das migrações Kotlin até 9 e valida as consultas do DashboardDao e as agregações comerciais extraídas do DashboardRepository. Fixtures cobrem registros antigos, ausência de vencimento, custos conhecidos/desconhecidos, compras posteriores à venda, unidades, saldo parcial, duplicidade e integridade referencial.

Não compila Kotlin, não executa repositórios nem substitui KSP, testes Robolectric, validação de schema Room, lint ou teste no dispositivo. Esses devem ser executados pelo Gradle em ambiente Android configurado.
