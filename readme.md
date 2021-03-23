## Oppsett
### Oppsett av utviklingsmiljø
Prosjektet integreres i IntelliJ og åpnes som et gradle prosjekt der.

NB: "Create separate module per source set" fungerer IKKE!

Ved første gangs import av prosjektet må man stille inn encoding tilsvarende:
 * Project Encoding: **windows-1252**
 * Encoding for property files: **ISO-8859-1**
 * Create UTF-8 files: **with NO BOM** 
 


#### Oppsett av WEBLOGIC_HOME
Konfigurasjon av biblioteker til weblogic gjøres i `gradle-user.properties`:  
``` properties
WEBLOGIC_HOME=C:\\wls1213
WEBLOGIC_VERSION=12.1.3
``` 

Dette kreves kun for å bruke deploy-taskene. 


#### Oppsett av testdatabase
Bruker opprettes via `gradlew dbCreateUser`. Hvor tablespacefilen skal legges bestemmes
av property `db_dataroot_dir`.

Skjema settes opp via `gradlew dbInit`

Dersom man får feilmeldingen: 
``` 
CREATE OR REPLACE PACKAGE SNAPSHOT_TIME
....
END SNAPSHOT_TIME;

Message:
ORA-01031: utilstrekkelige privilegier
``` 
så mangler man privilegier. Gjeldende workaround for dette er å kjøre `gradlew saGrant`

## Utvikling
### Avhengighetsstyring
Hvilke versjoner av avhengighetene som skal brukes styres med Gradles konsept om _platform_. Det er
fire plattformer definert i bygget. Tre innkommende, og én utgående. Det er primært de innkommende
man vil måtte forholde seg til, med mindre man gjør omfattende endringer i SKIF.

Øvrige steder vil man angi avhengigheter uten versjonsnummer. Hvis Gradle ikke finner versjon, så
mangler avhengigheten i en plattform, eller så mangler man en plattformavhengihet.

#### skif-service-platform
Dette er for innkommende avhengigheter som gjelder for service-rammeverket. Disse kreves for å
bygge SKIF, så vel som å bruke SKIF.

#### skif-store-platform
Dette er for innkommende avhengigheter som gjelder for store-rammeverket. Disse kreves for å
bygge SKIF, så vel som å bruke SKIF. Trekker inn skif-service-platform, så man trenger ikke
gjenta avhengigheter derfra.

#### skif-internal-platform
Dette er for ting som kun trengs for testing, enten det er test-prosjektene, eller direkte testing.
Denne publiseres ikke, så det er veldig viktig at ting som publiseres ikke bruker denne
plattformen.

#### skif-platform
Dette er plattformen de som bruker SKIF skal bruke. Det skal ikke være noen andre avhengigheter
angitt her enn til selve SKIF. Resten kommer transitivt fra skif-service-platform og
skif-store-platform.

#### libraries.gradle
Denne brukes for å stille inn noen ting som er felles for plattformer som ikke avhenger av
hverandre, samt for å konfigurere ting som ikke kan styres med plattformer. Dette inkluderer
substitution rules og avhengigheten til installert WebLogic.

I tillegg defineres det hvilken OJDBC avhengighet man skal bruke: release eller debug. Versjon
kommer fra plattform.
