@echo off
rem Descarga las librerias del proyecto (Spring Boot, JPA, Modulith...) y corre las pruebas.
rem El resultado completo queda en maven-salida.log, en esta misma carpeta.
chcp 65001 >nul
cd /d "%~dp0"
echo Descargando librerias y corriendo las pruebas. La primera vez puede tardar varios minutos...
echo ===== Java ===== > maven-salida.log
where java >> maven-salida.log 2>&1
java -version >> maven-salida.log 2>&1
echo JAVA_HOME=%JAVA_HOME% >> maven-salida.log
echo ===== Maven ===== >> maven-salida.log
call mvnw.cmd -B test >> maven-salida.log 2>&1
echo CODIGO_SALIDA=%ERRORLEVEL% >> maven-salida.log
echo ===== FIN ===== >> maven-salida.log
echo Terminado. El detalle esta en maven-salida.log
timeout /t 20
