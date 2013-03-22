@rem
@rem SETTER BRUKER/MASKIN SPESEFIKKE ENVIRONMENT VARIABLE FOR HOVEDPROSJEKTET
@rem

mode con codepage select=1252

@rem Java, Gradle
set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_30
set GRADLE_HOME=C:\bin\gradle-1.0
rem set GRADLE_HOME=C:\bin\gradle-1.1

@rem Definer lokal nexus repo for offline utvikling (optional)
@rem set PUBLIC_REPO=http://localhost:8081/nexus/content/repositories/statkart/

@rem bestemmer weblogic home
set ORG_GRADLE_PROJECT_WEBLOGIC_HOME=C:\bea_wls10.3.5

@rem bestemmer bruk av debug JDBC driver for Oracle [SKIF-239]
set ORA_DEBUG=false

@rem Setup Perforce windows integrasjon (optional)
@rem p4 set P4CLIENT=%USERNAME%-%COMPUTERNAME%
@rem p4 set P4PORT=skrivap42:1666
@rem p4 set P4USER=%USERNAME%

@rem .NET plattform. MS_NET_HOME skal inneholde csc.exe og MS_SDK_HOME skal inneholde wsdl.exe og svcutil.exe
@rem set MS_NET_HOME=C:\WINDOWS\Microsoft.NET\Framework\v3.5
@rem set MS_SDK_HOME=C:\Program Files\Microsoft SDKs\Windows\v6.0A\bin
@rem set NUNIT_HOME=C:\Program Files\NUnit 2.5.2
