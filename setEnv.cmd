@rem
@rem SETTER ENVIRONMENT VARIABLE FOR PROSJEKT
@rem

@echo off
@mode con codepage select=865

rem Setter hovedkatalog for prosjektet. PROJECT_ROOT inneholder '\' til slutt derfor må det stå en '.' til slutt
rem %~dp0 is name of current script under NT
set PROJECT_ROOT=%~dp0.

rem les inn JAVA_HOME, GRADLE_HOME, MAVEN_HOME og andre maskin/bruker spesifike settings
if exist setEnv_personal.cmd call setEnv_personal.cmd


: Window title
if "%TITLE%"=="" (
   for /D %%P in (%PROJECT_ROOT%) do (
      set TITLE=SktoolsKode - %%~nxP
   )
)
title %TITLE%


rem .NET Setup (optional):
@rem echo Setter path til .NET tools. Disse brukes for bygging av .NET test klienter

if "%NUNIT_HOME%"=="" goto END_NUNIT_HOME
:CONFIGURE_NUNIT_HOME
  @echo NUNIT_HOME=%NUNIT_HOME%
  set PATH=%NUNIT_HOME%\bin\net-2.0;%PATH%
:END_NUNIT_HOME

if "%MS_NET_HOME%"=="" goto END_MS_NET_HOME
:CONFIGURE_MS_NET_HOME
  @echo MS_NET_HOME=%MS_NET_HOME%
  set PATH=%MS_NET_HOME%;%PATH%
:END_MS_NET_HOME

if "%MS_SDK_HOME%"=="" goto END_MS_SDK_HOME
:CONFIGURE_MS_SDK_HOME
  @echo MS_SDK_HOME=%MS_SDK_HOME%
  set PATH=%MS_SDK_HOME%;%PATH%
:END_MS_SDK_HOME


rem Java Setup:
@echo JAVA_HOME=%JAVA_HOME%
if not "%JAVA_HOME%"=="" goto CONFIGURE_JAVA_HOME
  @echo   JAVA_HOME environment variable er ikke satt. Har du glem å sette den i setEnv_personal.cmd?
  set ERROR=true
  goto END_JAVA_HOME
:CONFIGURE_JAVA_HOME
  set PATH=%JAVA_HOME%\bin;%PATH%
:END_JAVA_HOME


rem Gradle Setup:
@echo GRADLE_HOME=%GRADLE_HOME%
if not "%GRADLE_HOME%"=="" goto CONFIGURE_GRADLE_HOME
  @echo   GRADLE_HOME environment variable er ikke satt. Har du glem å sette den i setEnv_personal.cmd?
  set ERROR=true
  goto END_GRADLE_HOME
:CONFIGURE_GRADLE_HOME
  set PATH=%GRADLE_HOME%\bin;%PATH%
  set GRADLE_OPTS=-XX:MaxPermSize=256m
:END_GRADLE_HOME


rem Project Setup
if "%ORG_GRADLE_PROJECT_version%"=="" (
   for /D %%P in (%PROJECT_ROOT%) do (
      set ORG_GRADLE_PROJECT_version=%%~nxP-SNAPSHOT
      @echo Setter version til '%%~nxP-SNAPSHOT'
   )
)
if "%ORA_DEBUG%"=="" (
   set ORA_DEBUG=false
)


rem Weglogic Setup
if "%WEBLOGIC_HOME%"=="" (
  @echo   WEBLOGIC_HOME environment variable er ikke satt. Har du glem å sette den i setEnv_personal.cmd?
  set ERROR=true
)
if "%WEBLOGIC_VERSION%"=="" (
  @echo   WEBLOGIC_VERSION environment variable er ikke satt. Har du glem å sette den i setEnv_personal.cmd?
  set ERROR=true
)


rem Maven Repo & Nexus Setup
if "%MAVEN_REPO%"=="" (
   @echo Setter std Maven Repository for utvikling [felles]
   set MAVEN_REPO=http://nexus.statkart.no:8090/nexus/content/groups/public/
)
if "%REPO_UPLOAD_RELEASES%"=="" (
   @echo Setter std Maven Repository for utvikling releases
   set REPO_UPLOAD_RELEASES=http://admin:admin123@nexus.statkart.no:8090/nexus/content/repositories/releases/
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_USERNAME=admin
   set ORG_GRADLE_PROJECT_REPO_UPLOAD_RELEASES_PASSWORD=admin123
)



if defined ERROR (
  color 04
  echo.
  echo Feil i oppsett!
  goto exit
)

@gradle --version

:exit
