#!/bin/bash

fname="$1"
dirname="${fname%.txt}.walk"
mkdir -p $dirname
exec<$fname
while read line
do
    echo $line
    #echo ":"
    java -Xmx1G -cp lib/servlet-api.jar:.:web/WEB-INF/classes:web/WEB-INF/lib/* nies.data.apps.TextWalk -auto -depth 1 -query $line > tmp
    tail -10075 tmp > $dirname/${line:6}
    
done