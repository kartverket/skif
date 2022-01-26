
# Lokal utvikling på tvers av prosjekter
For enkelt å teste endringer i andre gradle prosjekt kan disse inkluderes som "composite builds" i byggesystemet.
Etter endring av oppsett må man oppfriske Gradle prosjektet i IntelliJ.
```properties 
# gradle.properties:
includeBuild.hibernate-orm=../hibernate-orm
includeBuild.hibernate-support=../skif-hibernate-support
```


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