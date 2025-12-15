Create my own Root certificate:


// generate my own keystore of PKCS12 format
keytool -genkeypair \
-alias myrootca \
-keyalg RSA \
-keysize 4096 \
-validity 3650 \
-storetype PKCS12 \
-keystore rootCA.p12 \
-storepass changeit \
-dname "CN=MyInternalRootCA, OU=MyOrg, O=MyCompany, C=UA"

// to see content of keystore
keytool -list -v -keystore rootCA.p12 -storetype PKCS12

// to export certificate:
keytool -exportcert   -alias myrootca   -keystore rootCA.p12   -storetype PKCS12   -storepass changeit   -rfc   -file myrootca.cer

// to export private key:
openssl pkcs12 -in rootCA.p12 \
-nocerts \
-passin pass:changeit \
-passout pass:changeit \
-out myrootca.key.encrypted.pem

// to see private key
openssl rsa -in myrootca.key.encrypted.pem -text -noout

// create intermidiate certificate (not signed yet)

keytool -genkeypair \
-alias intermediateca \
-keyalg RSA \
-keysize 4096 \
-validity 3650 \
-storetype PKCS12 \
-keystore intermediateCA.p12 \
-storepass changeit \
-dname "CN=MyIntermediateCA, OU=MyOrg, O=MyCompany, C=UA"

// to see intermidiate certificate and key object:
keytool -list -v -keystore intermediateCA.p12 -storetype PKCS12

// to see private key of intermidiate:
openssl pkcs12 -in intermediateCA.p12 -nocerts -passin pass:changeit -passout pass:changeit -out myintermidiateca.key.encrypted.pem

// to export certificate of intermidiate:
keytool -certreq \
-alias intermediateca \
-keystore intermediateCA.p12 \
-storepass changeit \
-file intermediate.csr


// sign intermediate certificate with root certificate:
keytool -gencert \
-alias myrootca \
-keystore rootCA.p12 \
-storepass changeit \
-infile intermediate.csr \
-outfile intermediate-signed.cer \
-validity 3650 \
-rfc \
-ext bc=ca:true,pathlen:0 \
-ext ku=keyCertSign,cRLSign

make sure intermidiate certificate is signed as CA:
keytool -printcert -file intermediate-signed.cer

//Import ROOT CA certificate into intermediate CA keystore
keytool -importcert \
-alias myrootca \
-file myrootca.cer \
-keystore intermediateCA.p12 \
-storepass changeit

// Import the signed intermediate certificate
keytool -importcert \
-alias intermediateca \
-file intermediate-signed.cer \
-keystore intermediateCA.p12 \
-storepass changeit


Now this keystore contains:

intermediate private key

intermediate certificate signed by root CA

root CA certificate


Verify the chain!

keytool -list -v -keystore intermediateCA.p12 -storepass changeit



Create server keypair:
keytool -genkeypair \
-alias serverkey \
-keyalg RSA \
-keysize 2048 \
-validity 3650 \
-storetype PKCS12 \
-keystore server.p12 \
-storepass changeit \
-dname "CN=myserver.local, OU=Dev, O=MyCompany, C=UA"

Create CSR (Certificate Server certificate Signing Request):
keytool -certreq   -alias serverkey   -keystore server.p12   -storepass changeit   -file server.csr

Sign server CSR using INTERMEDIATE CA:
keytool -gencert \
-alias intermediateca \
-keystore intermediateCA.p12 \
-storepass changeit \
-infile server.csr \
-outfile server-signed.cer \
-validity 3650 \
-rfc \
-ext ku=digitalSignature,keyEncipherment \
-ext eku=serverAuth \
-ext san=dns:localhost,dns:myserver.local


Import Root CA into the server keystore:
keytool -importcert \
-alias myrootca \
-file myrootca.cer \
-keystore server.p12 \
-storepass changeit

Import Intermediate CA certificate into server keystore:
keytool -importcert \
-alias intermediateca \
-file intermediate-signed.cer \
-keystore server.p12 \
-storepass changeit


Import the signed server certificate into server keystore:
keytool -importcert \
-alias serverkey \
-file server-signed.cer \
-keystore server.p12 \
-storepass changeit

Now your server.p12 contains:
server private key
server certificate (signed by intermediate)
intermediate CA cert
root CA cert

VERIFY THE CHAIN:
keytool -list -v -keystore server.p12 -storepass changeit



to create client truststore:
keytool -importcert \
-alias myrootca \
-file myrootca.cer \
-keystore client-truststore.p12 \
-storetype PKCS12 \
-storepass changeit



