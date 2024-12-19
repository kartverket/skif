# Versjonering og utviklingsløp
Utvikling er trunk basert, slik at alle endringer tas ned til hovedbranch etter at de har gjennomgått PR og er blitt tilstrekkelig testet.
Hvert team tar ansvar for sin versjon. Hovedbranch heter "trunk".

Fra en vedlikeholdsversjon til en annen skal det ikke være API-endringer, med mindre det er absolutt nødvendige for den feilen vedlikeholdsversjonen er til for å rette.

CI/CD er satt opp til å bygge alle brancher automatisk når noe sjekkes inn. Versjonsnummeret er det samme som branchnavnet, etterfulgt av `-build<n>`, 
hvor `<n>` er byggnummeret i Jenkins. Disse automatiske byggene er å anse som snapshot-versjoner og slettes automatisk etter en periode.

## Versjonsstrategi
SKIF er versjonert etter malen `<hovedversjon>.<underversjon>.<vedlikeholdsversjon>`.

## Vedlikeholdsversjoner
Dersom hovedbranch representerer gjeldende underversjon så kan releasen versjonsmerkes der. 
Hvis ikke, så cherry-pickes endringer ut på frittstående release-branch.

## Hvordan publisere ny versjon
Versjonsmerke defineres manuelt på release-branch med navn på formen `<hovedversjon>.<underversjon>`.
Publiser release i Jenkins:
 * https://jenkins.matrikkel.no/job/Github%20SKIF/job/skif-release/
 * Klikk på "Scan Repository Now" dersom branchen ikke finnes i listen enda.
 * Start så jobb og sett vedlikeholdsversjon (starter på 0).

### Tagge release på GitHub
 * Definer er ny release https://github.com/kartverket/skif/releases/new
 * Versjonsmerke (tag) skal være på formen `<hovedversjon>.<underversjon>.<vedlikeholdsversjon>`
   * Hovedversjon og underversjon stammer fra branchnavn.
   * Vedlikeholdsversjon stammer fra input til publiseringsjobb.
 * Target skal være commit (hash) til publiseringsjobb.

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

#### Oppsett av TomEE
tomee-subprosjektet bruker Gradles application-plugin for å sette opp en tjener med alle tre testprosjektene. Kan
enten kjøres med `gradle run` eller ved å bygge applikasjonen med `gradle install` og kjøre den på den måten.

Den bygde applikasjonen plukker opp databaseinnstillingene fra gradle.properties under bygging. Hvis man endrer på
gradle.properties, så må man bygge applikasjonen på nytt for å få disse endringene inn.

For å kjøre testene mot TomEE er det bare å legge inn riktig portnummer i gradle.properties og kjøre testene i
JEE-modus.
