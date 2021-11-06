
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


Dersom weblogic home er satt feil eller mangler gir `gradlew assemble` feilmeldingen
``` 
:skif-service-common:compileJava
C:\dev\skif\skif-service-common\src\main\java\no\statkart\skif\service\ws\JaxWsServicePool.java:3: error: package com.sun.xml.ws.developer does not exist
import com.sun.xml.ws.developer.JAXWSProperties;
``` 


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

### Test i JEE Mode mot Weblogic og Spring Server

Alle SKIF test applikasjoner kan deployes samlet mot kjørende Weblogic Server ved kommando:

```
./gradlew deploy
```

Der etter kjøres alle tester mot Weblogic med kommando:
```
./gradlew cleanTest test -Pservice_mode=JEE -Pwls_sport=7002
```

To av SKIFs test applikasjoner er også implementert på Spring Boot, 'skif-service-test-spring' og 
'store-service-test-spring'. Disse må kjøres hver for seg da de begge kjører på port 8080. 

For å test 'skif-service-test-spring' start applikasjonen 'SkifSpringServiceTestServerApplication' med ssl profil. Kjør
deretter testene mot denne modulen:

```
./gradlew cleanTest :skif-service-test:skif-service-test-test:test -Pservice_mode=JEE -Pwls_sport=8080
```

For å test 'skif-store-test-spring' start applikasjonen 'SkifSpringStoreTestServerApplication' med ssl profil. Kjør
deretter testene mot denne modulen:

```
./gradlew cleanTest :skif-store-test:skif-store-test-test:test -Pservice_mode=JEE -Pwls_sport=8080
```
