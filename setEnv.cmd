@rem
@rem SETTER ENVIRONMENT VARIABLE FOR PROSJEKT
@rem

@echo off

rem Setter hovedkatalog for prosjektet. PROJECT_ROOT inneholder '\' til slutt derfor må det stå en '.' til slutt
rem %~dp0 is name of current script under NT
set PROJECT_ROOT=%~dp0.

rem les inn JAVA_HOME, ANT_HOME,  MAVEN_HOME og andre maskin/bruker spesifikke settings
if exist setEnv_personal.cmd call setEnv_personal.cmd


rem Sjekk at JAVA_HOME er satt
:checkJava
if not defined JAVA_HOME goto noJavaHome
goto javaHomeOk
:noJavaHome
echo JAVA_HOME environment variable er ikke satt. Har du glem å sette den i setEnv_personal.cmd?
goto exit

rem Hvis vi kommer her er JAVA_HOME satt
:javaHomeOK

rem Setup Java:
@echo JAVA_HOME=%JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=%JAVA_HOME%\lib\tools.jar;

rem Setup Gradle:
@echo GRADLE_HOME=%GRADLE_HOME%
set PATH=%GRADLE_HOME%\bin;%PATH%
set GRADLE_OPTS=-XX:MaxPermSize=256m

rem Setup Groovy (Optional)
if  not "%GROOVY_HOME%"=="" set PATH=%PATH%;%GROOVY_HOME%\bin

@rem Setter path til .NET tools. Disse brukes for bygging av .NET test klienter
REM set PATH=%PATH%;%MS_NET_HOME%;%MS_SDK_HOME%;%NUNIT_HOME%\bin\net-2.0

@REM echo PATH=%PATH%

if "%ORG_GRADLE_PROJECT_version%"=="" (
   for /D %%P in (%PROJECT_ROOT%) do (
      set ORG_GRADLE_PROJECT_version=%%~nxP-SNAPSHOT
      @echo Setter versjon til '%%~nxP-SNAPSHOT'
   )
)

if "%ORG_GRADLE_PROJECT_LOCALHOSTNAME%"=="" (
   @echo Setter std maskinnavn
   set ORG_GRADLE_PROJECT_LOCALHOSTNAME=%COMPUTERNAME%.statkart.no
)

if "%ORG_GRADLE_PROJECT_MAVEN_REPO%"=="" (
   @echo Setter std Maven Repository for utvikling [felles]
   set ORG_GRADLE_PROJECT_MAVEN_REPO=http://nexus.statkart.no:8090/nexus/content/groups/public/
)

if "%ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES%"=="" (
   @echo Setter std Maven Repository for releases
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES=http://nexus.statkart.no:8090/nexus/content/repositories/releases/
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_USERNAME=admin
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_PASSWORD=admin123
)

if "%ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS%"=="" (
   @echo Setter std Maven Repository for snapshots
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS=http://nexus.statkart.no:8090/nexus/content/repositories/snapshots/
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS_USERNAME=admin
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_SNAPSHOTS_PASSWORD=admin123
)

@gradle --version

:exit
