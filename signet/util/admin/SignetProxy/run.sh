#!/bin/sh

# -----------------------------------------------------------------------------
# Script for command line utilility to assign System Administrator proxy
#
# Environment Variable Prequisites
#
#   JAVA_HOME     Must point to your JDK.
# -----------------------------------------------------------------------------

JAVA_HOME=/usr/java

BASEDIR=../
CLASSPATH=$BASEDIR/classes

for file in `ls $BASEDIR/lib/*.jar`;
do
if [ "$CLASSPATH" != "" ]; then
   CLASSPATH=${CLASSPATH}:$file
else
   CLASSPATH=$file
fi
done

echo "Using classpath: "$CLASSPATH

$JAVA_HOME/bin/java -cp $CLASSPATH edu.internet2.middleware.signet.util.SignetProxy $1 $2
