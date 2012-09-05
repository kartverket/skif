@rem
@rem SETTER BRUKER/MASKIN SPESEFIKKE ENVIRONMENT VARIABLE FOR HOVEDPROSJEKTET
@rem

@rem Java, Gradle
set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_30
set GRADLE_HOME=C:\bin\gradle-1.0
rem set GRADLE_HOME=C:\bin\gradle-1.1

@rem Definer lokal nexus repo for offline utvikling (optional)
@rem set PUBLIC_REPO=http://localhost:8081/nexus/content/repositories/statkart/

@rem bestemmer weblogic home
set ORG_GRADLE_PROJECT_WEBLOGIC_HOME=C:\bea_wls10.3.5



@rem echo Definerer maskinnavn [optional]
@rem set ORG_GRADLE_PROJECT_LOCALHOSTNAME=%COMPUTERNAME%.statkart.no

@rem echo Definerer Maven Repository for utvikling [optional]
@rem set ORG_GRADLE_PROJECT_MAVEN_REPO=http://skrivap92.statkart.no:8001/nexus/content/groups/public/

@rem echo Definerer lokalt Nexus Maven Reposotory repo for offline utvikling [optional]
@rem set ORG_GRADLE_PROJECT_MAVEN_REPO=http://localhost:8081/nexus/content/repositories/statkart/

@rem echo Definerer Maven Repository for releases [optional]
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES=http://skrivap92.statkart.no:8001/nexus/content/repositories/releases/
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_USERNAME=admin
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_PASSWORD=admin123

@rem echo Definerer Maven Repository for snapshots [optional]
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS=http://skrivap92.statkart.no:8001/nexus/content/repositories/snapshots/
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS_USERNAME=admin
@rem set ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS_PASSWORD=admin123


@rem Setup Perforce windows integrasjon (optional)
@rem p4 set P4CLIENT=%USERNAME%-%COMPUTERNAME%
@rem p4 set P4PORT=skrivap42:1666
@rem p4 set P4USER=%USERNAME%

