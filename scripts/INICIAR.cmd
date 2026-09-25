@echo off
setlocal
cd /d "%~dp0"
set "JAVA_CMD=java"
if exist "%USERPROFILE%\ProjetosJavaJr\ferramentas\jdk-17\bin\java.exe" set "JAVA_CMD=%USERPROFILE%\ProjetosJavaJr\ferramentas\jdk-17\bin\java.exe"
echo DeskFlow: http://localhost:8081
echo Mantenha esta janela aberta. Use Ctrl+C para encerrar.
start "" "http://localhost:8081"
"%JAVA_CMD%" -jar app.jar --server.port=8081 --server.address=127.0.0.1
if errorlevel 1 echo Falha ao iniciar. Confira Java 17 e se a porta 8081 esta livre.
pause
