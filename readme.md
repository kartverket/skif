# Versjonering og utviklingsløp

## Publisering
Pakkene ble tidligere publisert til Nexus. Gamle pakker er migrert til GitHub Packages.

Nye pakker publiseres til [GitHub Packages](https://github.com/orgs/kartverket/packages?repo_name=skif) via [build-push.yml](.github/workflows/build-publish.yml) workflowen.
Ved hver push til `trunk` så vil det bygges og publiseres en ny versjon av pakkene.


## Releasetesting
Tester kjøres automatisk som en del av [build-push.yml](.github/workflows/build-publish.yml) workflowen ved PR og push til `trunk`.


## Versjonering
Pakkene har versjonsnummer som er av formatet `[Major version].[Date].[SHA]`
Alle pakker har samme versjon, og versjonsnummeret oppdateres ved hver publisering.

`[Major version]` oppdateres ved breaking changes og kan endres i [build-push.yml](.github/workflows/build-publish.yml) workflowen.

## Lokal utvikling og dependencies

Skif er avhenging av blant annet SkTools.xjc som hentes fra [GitHub Packages](https://github.com/orgs/kartverket/packages?repo_name=SkTools).
For å kunne kjøre build og testene hentes denne fra GitHub Packages i [settings.gradle](settings.gradle), som krever et Token med lese-tilgang for pakker på GitHub for Karteverket organisasjonen.
Tokenet leses fra systemets miljøvariabler, og må hete `PACKAGES_TOKEN`, `KV_PACKAGES_PAT` eller `GH_PACKAGES_PAT`.
Tokenet `GITHUB_USER` må også settes som er brukernavn.


# Lokal utvikling på tvers av prosjekter
For enkelt å teste endringer i andre gradle prosjekt kan disse inkluderes som "composite builds" i byggesystemet.
Etter endring av oppsett må man oppfriske Gradle prosjektet i IntelliJ.
```properties 
# gradle.properties:
includeBuild.hibernate-orm=../hibernate-orm
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
