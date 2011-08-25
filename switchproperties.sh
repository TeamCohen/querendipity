#!/bin/bash
if [ -z "$2" ]; then
    echo "Usage: $0 current desired
$0 will copy *.properties to *.properties.current. If *.properties.desired exists, $0 will copy *.properties.desired to *.properties."
    exit
fi

echo "From: $1 To: $2"
PROPS="build.properties nies.properties ghirl.properties log4j.properties"
for prop in $PROPS;
do
    echo $prop
    cp $prop $prop.$1
    if [ -f $prop.$2 ]; then
	cp $prop.$2 $prop
    fi
done
touch currentproperties:$2