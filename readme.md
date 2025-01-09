
# Lokal utvikling på tvers av prosjekter
For enkelt å teste endringer i andre gradle prosjekt kan disse inkluderes som "composite builds" i byggesystemet.
Etter endring av oppsett må man oppfriske Gradle prosjektet i IntelliJ.
```properties 
# gradle.properties:
includeBuild.hibernate-orm=../hibernate-orm
includeBuild.hibernate-support=../skif-hibernate-support
```

#### Oppsett av testdatabase
Bruker opprettes via `gradle dbCreateUser`. Hvor tablespacefilen skal legges bestemmes
av property `db_dataroot_dir`.

##### Oppsett av database-skjema
Skjema settes opp via `gradle dbInit`

Dersom man får `ORA-01031` så har man et skjema med et annet oppsett av privilegier (feks fra matrikkelen):
```
Message:
ORA-01031: utilstrekkelige privilegier
ORA-01031: insufficient privileges
```
Se filen [CreateUser.sql](database/src/main/sql/oracle/user/CreateUser.sql) for hvilke privilegier som trengs.

#### Oppsett av TomEE
tomee-subprosjektet bruker Gradles application-plugin for å sette opp en tjener med alle tre testprosjektene. Kan
enten kjøres med `gradle run` eller ved å bygge applikasjonen med `gradle install` og kjøre den på den måten.

Den bygde applikasjonen plukker opp databaseinnstillingene fra gradle.properties under bygging. Hvis man endrer på
gradle.properties, så må man bygge applikasjonen på nytt for å få disse endringene inn.

For å kjøre testene mot TomEE er det bare å legge inn riktig portnummer i gradle.properties og kjøre testene i
JEE-modus.
