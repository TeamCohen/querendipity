#!/bin/bash -x

java -cp .:dist/*:web/WEB-INF/lib/*:/usr0/share/tomcat5/shared/lib/tokyocabinet.jar:/usr0/share/tomcat5/common/lib/servlet-api.jar -Djava.library.path=/usr0/local/lib $@
