@rem
@rem SETTER BRUKER/MASKIN SPESEFIKKE ENVIRONMENT VARIABLE FOR HOVEDPROSJEKTET
@rem

@rem Java, Gradle
set JAVA_HOME=c:\apps\jdk1.6.0_26
set GRADLE_HOME=c:\apps\gradle-1.0-milestone-4

@rem Groovy (optional)
set GROOVY_HOME=c:\apps\groovy-1.7.10

@rem Set Weblogic
set WEBLOGIC_HOME=D:\wls1031oepe

@rem Definer lokal nexus repo for offline utvikling (optional)
set PUBLIC_REPO=http://localhost:8081/nexus/content/repositories/statkart/

@rem .NET plattform. MS_NET_HOME skal inneholde csc.exe og MS_SDK_HOME skal inneholde wsdl.exe og svcutil.exe
set MS_NET_HOME=C:\WINDOWS\Microsoft.NET\Framework\v3.5
set MS_SDK_HOME=C:\Program Files\Microsoft SDKs\Windows\v6.0A\bin
set NUNIT_HOME=C:\Program Files\NUnit 2.5.2

@rem Setup Perforce windows integrasjon
p4 set P4CLIENT=frehen-WSFREHEN1
p4 set P4PORT=skrivap42:1666
p4 set P4USER=frehen

