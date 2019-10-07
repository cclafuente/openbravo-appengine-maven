
-- probar version:
mvn clean compile exec:java -Dexec.mainClass=com.openbravo.pos.forms.StartPOS

--crear ejecutable:
Instalar RXTRXcomm.jar

Desde este directorio:
mvn install:install-file -Dfile=RXTXcomm.jar -DgroupId=org.rxtx -DartifactId=rxtxcomm -Dversion=2.2pre2 -Dpackaging=jar


