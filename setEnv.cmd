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
      set TITLE=SKIF - %%~nxP
   )
)
title %TITLE%


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


if defined ERROR (
  color 04
  echo.
  echo Feil i oppsett!
  goto exit
)

@gradle --version

:exit
