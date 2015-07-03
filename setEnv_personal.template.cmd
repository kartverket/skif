@rem
@rem SETTER BRUKER/MASKIN SPESEFIKE ENVIRONMENT VARIABLE FOR HOVEDPROSJEKTET
@rem


@rem *******************************************************************************************************************
@rem * Gradle & Project Setup
@rem *******************************************************************************************************************
set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_60
set GRADLE_HOME=C:\bin\gradle-1.6

@rem bestemmer bruk av debug JDBC driver for Oracle [SKIF-239]
set ORA_DEBUG=false

@rem bestemmer versjon for bygget (optional)
@rem set ORG_GRADLE_PROJECT_version=trunk


@rem *******************************************************************************************************************
@rem * Weblogic Setup
@rem *******************************************************************************************************************
set WEBLOGIC_HOME=C:\bea_wls10.3.5
set WEBLOGIC_VERSION=10.3.5
@rem LOCALHOSTNAME bestemmer gyldig hostname for bruk med sertifikater (default til %COMPUTERNAME%.statkart.no)
@rem set LOCALHOSTNAME=%COMPUTERNAME%.statkart.no


@rem *******************************************************************************************************************
@rem * Maven Repo & Nexus Setup  (optional)
@rem *******************************************************************************************************************
@rem Definer lokal nexus repo for offline utvikling (optional)
@rem set MAVEN_REPO=http://localhost:8081/nexus/content/repositories/statkart/

@rem Definerer repo for deploying av bygget applikasjon (nexus)
@rem set REPO_UPLOAD_RELEASES=http://admin:admin123@nexus.statkart.no:8090/nexus/content/repositories/releases/
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_USERNAME=admin
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_PASSWORD=admin123


@rem *******************************************************************************************************************
@rem * Perforce for Windows integrasjon (optional)
@rem *******************************************************************************************************************
@rem p4 set P4CLIENT=%USERNAME%-%COMPUTERNAME%
@rem p4 set P4PORT=perforce.statkart.no:1666
@rem p4 set P4USER=%USERNAME%


@rem *******************************************************************************************************************
@rem * .NET plattform (optional)
@rem *******************************************************************************************************************
@rem set NUNIT_HOME=C:\Program Files\NUnit 2.5.2

@rem echo MS_NET_HOME skal inneholde csc.exe og MS_SDK_HOME skal inneholde wsdl.exe og svcutil.exe
@rem set MS_NET_HOME=C:\WINDOWS\Microsoft.NET\Framework\v3.5

@rem echo MS_SDK_HOME skal inneholde wsdl.exe og svcutil.exe
@rem set MS_SDK_HOME=C:\Program Files\Microsoft SDKs\Windows\v6.0A\bin
