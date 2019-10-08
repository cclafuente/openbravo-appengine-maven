
-- probar version:
mvn clean compile exec:java -Dexec.mainClass=com.openbravo.pos.forms.StartPOS

-- probar sin ejecutar
mvn exec:java -Dexec.mainClass=com.openbravo.pos.forms.StartPOS


--crear ejecutable:
Instalar RXTRXcomm.jar

Desde este directorio:
mvn install:install-file -Dfile=RXTXcomm.jar -DgroupId=org.rxtx -DartifactId=rxtxcomm -Dversion=2.2pre2 -Dpackaging=jar

Resto de dependencias, desde el directorio lib:
mvn install:install-file -Dfile=jpos1121.jar -DgroupId=org.jpos -DartifactId=jpos -Dversion=1.121 -Dpackaging=jar

mvn install:install-file -Dfile=substance.jar -DgroupId=org.jvnet -DartifactId=substance -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=substance-swingx.jar -DgroupId=org.jvnet -DartifactId=substance-swingx -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=bsh-core-2.0b4.jar -DgroupId=bsh -DartifactId=bsh-core -Dversion=2.0b4 -Dpackaging=jar

mvn install:install-file -Dfile=barcode4j-light.jar -DgroupId=barcode4j -DartifactId=barcode4j-light -Dversion=1.0 -Dpackaging=jar


