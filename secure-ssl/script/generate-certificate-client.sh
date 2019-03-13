#!/bin/bash
CA="$1"
ALIAS="$2"
CN="$3"
OU="$4"

C=NN
ST=NN
L=NN
O=NN

PASS="abcdefgh"
VALIDITY=10000

f_show_help(){
    echo
    echo "[HELP] : How to use this script : "
    echo
    echo "          ./generate-certificate-client.sh <certificate authority> <project name> <Common name> <Organisation unit>"
    echo "                                           These four parameters are mandatory."
    echo
    echo "Beware, it won't generate anything if <project name> folder exists."
    echo
    exit 0
}

# To isolate every client certificate into its own folder
f_gen_folder(){
    if [ ! -d "generated" ]; then
        mkdir generated
    fi
    if [ -d "generated/$ALIAS" ]; then
        echo "A certificate with the alias $ALIAS already exist, please re-use it or use another ALIAS"
        exit 0
    fi
    mkdir generated/$ALIAS
}

# Generate the .pem and .key and sign them with ca
f_gen_pem_and_key(){
    echo "Generating pem / key"
    openssl genrsa -aes256 -passout "pass:$PASS" -out generated/$ALIAS/$ALIAS.client.key 2048 
    openssl req -passin "pass:$PASS" -days $VALIDITY -passout "pass:$PASS" -key generated/$ALIAS/$ALIAS.client.key -new -out generated/$ALIAS/$ALIAS.client.req \
        <<EOF
$C
$ST
$L
$O
$OU
$CN
.
$PASS
.
EOF

    echo "Signing the generated key"
    openssl x509 -req -passin "pass:$PASS" -days $VALIDITY -in generated/$ALIAS/$ALIAS.client.req -CA $CA -CAkey $CA-key -CAserial $CA.srl -out generated/$ALIAS/$ALIAS.client.pem
}

# Generate the jks files (keystore and truststore) and sign it with ca
f_gen_jks(){
    echo "Generating jks keystore and truststore..."
    keytool -storepass $PASS -keypass $PASS -keystore generated/$ALIAS/$ALIAS.client.truststore.jks -alias CARoot -import -file $CA -noprompt

    keytool -storepass $PASS -keypass $PASS -keystore generated/$ALIAS/$ALIAS.client.keystore.jks -alias $ALIAS -validity $VALIDITY -genkeypair -keyalg RSA -dname "CN=$CN,OU=$OU,O=$O,L=$L,ST=$ST,C=$C" -noprompt
    
    keytool -storepass $PASS -keystore generated/$ALIAS/$ALIAS.client.keystore.jks -alias $ALIAS -certreq -file generated/$ALIAS/$ALIAS.cert-file
    
    openssl x509 -req -CA $CA -CAkey $CA-key -in generated/$ALIAS/$ALIAS.cert-file -out generated/$ALIAS/$ALIAS.cert-signed -days $VALIDITY -CAcreateserial -passin pass:$PASS
    
    keytool -storepass $PASS -keypass $PASS -keystore generated/$ALIAS/$ALIAS.client.keystore.jks -alias CARoot -import -file $CA -noprompt

    keytool -storepass $PASS -keypass $PASS -keystore generated/$ALIAS/$ALIAS.client.keystore.jks -alias $ALIAS -import -file generated/$ALIAS/$ALIAS.cert-signed
    echo "Generated jks keystore and truststore"
}

f_clean(){
    rm generated/$ALIAS/*cert-signed
    rm generated/$ALIAS/*.req
    rm generated/$ALIAS/*cert-file
    cp $CA generated/$ALIAS
}

echo 
echo "Launching ..."

if [ "$#" -ne 4 ] ; then 
        echo "param nb $#"
        f_show_help
fi

f_gen_folder
f_gen_pem_and_key
f_gen_jks
f_clean

echo "New certificates can be found in generated/$ALIAS"

