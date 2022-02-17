
# Lokal utvikling på tvers av prosjekter
For enkelt å teste endringer i andre gradle prosjekt kan disse inkluderes som "composite builds" i byggesystemet.
Etter endring av oppsett må man oppfriske Gradle prosjektet i IntelliJ.
```properties 
# gradle.properties:
includeBuild.hibernate-orm=../hibernate-orm
includeBuild.hibernate-support=../skif-hibernate-support
```


#### Oppsett av WEBLOGIC_HOME
Konfigurasjon av biblioteker til weblogic gjøres i `gradle.properties`:  
``` properties
Brukes ved deploy
WEBLOGIC_HOME=C:\\wls1213
WEBLOGIC_VERSION=12.1.3
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