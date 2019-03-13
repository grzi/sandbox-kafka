#!/bin/bash

OUTPUT=$1
CANAME=$2

VALIDITY=10000
PASS="abcdefgh"

f_prepare(){
    if [ ! -d "$OUTPUT" ]; then
        mkdir $OUTPUT
    fi

    if [ -e "$OUTPUT/$CANAME" ]; then
        echo "A Certificate authority already exists, can't override"
        f_show_help
    fi
}

f_show_help(){
    echo
    echo "[HELP] : How to use this script : "
    echo
    echo "          ./generate-certificate-server.sh <Output path> <Certificate Authority desired name>"
    echo "                                        These two parameters are mandatory."
    echo
    echo "You can change the default values of the generated certificate into the script"
    echo "Beware, it won't generate a new CA if <Output path>/<Certificate Authority name> exists"
    echo
    exit 0
}

if [ "$#" -ne 2 ] ; then 
    f_show_help
fi

f_prepare

echo "Generating server keystore"
keytool -keystore $OUTPUT/kafka.server.keystore.jks -alias localhost -validity $VALIDITY -genkeypair -storepass $PASS -keypass $PASS -keyalg RSA -dname "CN=$HOSTNAME,OU=AdminKafka,O=NN,L=NN,ST=NN,C=NN" -ext SAN=DNS:$HOSTNAME

echo "Generating server Certificate authority"
openssl req -new -x509 -keyout $OUTPUT/$CANAME-key -out $OUTPUT/$CANAME -days $VALIDITY \
<<EOF
FR
Ile-de-france
Paris
Something-1
Something-2
$HOSTNAME
.
EOF

echo "Import certificate authority into truststore"
keytool -storepass $PASS -keypass $PASS -keystore $OUTPUT/kafka.server.truststore.jks -alias CARoot -import -file $OUTPUT/$CANAME

echo " Export cert-file from server keystore"
keytool -storepass $PASS -keypass $PASS -keystore $OUTPUT/kafka.server.keystore.jks -alias localhost -certreq -file $OUTPUT/$CANAME-file

echo "Signing cert-file with certificqte authority, to cert-signed"
openssl x509 -req -CA $OUTPUT/$CANAME -CAkey $OUTPUT/$CANAME-key -in $OUTPUT/$CANAME-file -out $OUTPUT/$CANAME-signed -days $VALIDITY -CAcreateserial -passin pass:$PASS 

echo "Importing Certificate authority into the keystore"
keytool -storepass $PASS -keypass $PASS  -keystore $OUTPUT/kafka.server.keystore.jks -alias CARoot -import -file $OUTPUT/$CANAME

echo "Imported the signed cert into the keystore"
keytool -storepass $PASS -keypass $PASS -keystore $OUTPUT/kafka.server.keystore.jks -alias localhost -import -file $OUTPUT/$CANAME-signed