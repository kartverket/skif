# SKIF

SKIF (Statens Kartverk Infrastructure Framework) er Statens kartverk sitt felles egenutviklede rammeverk for eiendomsssystemer; Grunnboken, Matrikkelen, NRL og Stedsnavn.
Det er et rammeverk for Java utvikling på klient og tjener basert på JEE-teknologi og SOAP Web Services.

## Versjonering og utviklingsløp

### Publisering
Pakkene ble tidligere publisert til Nexus. Gamle pakker er migrert til GitHub Packages.

Nye pakker publiseres til [GitHub Packages](https://github.com/orgs/kartverket/packages?repo_name=skif) via [build-push.yml](.github/workflows/build-publish.yml) workflowen.
Ved hver push til `trunk` så vil det bygges og publiseres en ny versjon av pakkene.


### Releasetesting
Tester kjøres automatisk som en del av [build-push.yml](.github/workflows/build-publish.yml) workflowen ved PR og push til `trunk`.


### Versjonering
Pakkene har versjonsnummer som er av formatet `[Major version].[Date].[SHA]`
Alle pakker har samme versjon, og versjonsnummeret oppdateres ved hver publisering.

`[Major version]` oppdateres ved breaking changes og kan endres i [build-push.yml](.github/workflows/build-publish.yml) workflowen.

## Lokal utvikling og dependencies

Skif er avhengig av blant annet SkTools.xjc som hentes fra [GitHub Packages](https://github.com/orgs/kartverket/packages?repo_name=SkTools).
For å kunne kjøre build og testene hentes denne fra GitHub Packages i [settings.gradle](settings.gradle), som krever et Token med lese-tilgang for pakker på GitHub for Karteverket organisasjonen.
Tokenet leses fra systemets miljøvariabler, og må hete `PACKAGES_TOKEN`, `KV_PACKAGES_PAT` eller `GH_PACKAGES_PAT`.
Tokenet `GITHUB_USER` må også settes som er brukernavn.


#### Oppsett av testdatabase
Testene kjører mot en ekte Oracle-database, så man trenger en kjørende Oracle-instans (f.eks. en lokal Docker-container
med Oracle XE) før man kan sette opp skjema og kjøre tester.

Kopier [gradle.template.properties](gradle.template.properties) til `gradle.properties` i rot av repoet og fyll inn
følgende properties (se kommentarene i filen for Matrikkelen-oppsettet som et eksempel):

- `db_jdbc_url` — JDBC-URL til Oracle-instansen, f.eks. `jdbc:oracle:thin:@//localhost:1521/<service-navn>`
- `db_username` / `db_password` — brukeren SKIF skal bruke (opprettes av `dbCreateUser`, se under)
- `admin_db_username` / `admin_db_password` — en admin/SYSTEM-bruker med nok privilegier til å opprette tablespace og ny bruker
- `db_dataroot_dir` — mappen hvor tablespace-datafilen skal legges, **sett fra Oracle-serverens filsystem**, ikke fra klientmaskinen

Bruker og tablespace opprettes via:
```
./gradlew :database:dbCreateUser
```

Dersom man får `ORA-01031` så har man et skjema med et annet oppsett av privilegier (feks fra matrikkelen):
```
Message:
ORA-01031: utilstrekkelige privilegier
ORA-01031: insufficient privileges
```
Se filen [CreateUser.sql](database/src/main/sql/oracle/user/CreateUser.sql) for hvilke privilegier som trengs.

**OBS ved kjøring mot Oracle på Linux:** SQL-malen for tablespace ([CreateTablespace.sql](database/src/main/sql/oracle/tablespace/CreateTablespace.sql))
setter sammen datafilstien med backslash (`\`) mellom `db_dataroot_dir` og filnavnet, altså Windows-stil. På Linux blir
`\` ikke tolket som katalogskille, så hele strengen (f.eks. `SKIF\SKIF.DBF`) blir ett bokstavelig filnavn som Oracle
prøver å opprette i mappen *rett over* den man oppga i `db_dataroot_dir`. Denne mappen eies gjerne ikke av
Oracle-brukeren, noe som gir `ORA-27040: file create error, unable to create file` / `Permission denied`. Løsningen
er å peke `db_dataroot_dir` til en undermappe av den faktiske, skrivbare datafil-mappen i containeren
(f.eks. `/opt/oracle/oradata/XE/skif` i stedet for `/opt/oracle/oradata/XE`), slik at det bokstavelige filnavnet
havner i en mappe Oracle har skrivetilgang til.

##### Oppsett av database-skjema
Skjema settes opp og fylles med testdata via:
```
./gradlew :database:dbInit
```

#### Oppsett av TomEE
tomee-subprosjektet bruker Gradles application-plugin for å sette opp en tjener med alle tre testprosjektene. Kan
enten kjøres med `gradle run` eller ved å bygge applikasjonen med `gradle install` og kjøre den på den måten.

Den bygde applikasjonen plukker opp databaseinnstillingene fra gradle.properties under bygging. Hvis man endrer på
gradle.properties, så må man bygge applikasjonen på nytt for å få disse endringene inn.

For å kjøre testene mot TomEE er det bare å legge inn riktig portnummer i gradle.properties og kjøre testene i
JEE-modus.
