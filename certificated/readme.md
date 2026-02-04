Certificate

A signed document that binds:
    - identity (CN / SAN)
    - public key
    - usage rules to a signature of a CA

Private key
    - proves identity
    - NEVER shared
    - stored only in keystores

CA (Certificate Authority)
A certificate with:
    BasicConstraints: CA=true
    KeyUsage: keyCertSign

Root CA
    - Self-signed
    - Trust anchor
    - Lives in truststores

Leaf certificate
    - Used by server/client
    - CA=false
    - Has serverAuth or clientAuth





1. Create Root CA
   (The only cert client must trust)

keytool -genkeypair \
-alias rootA \
-keyalg RSA \
-keysize 4096 \
-validity 3650 \
-storetype PKCS12 \
-keystore rootA.p12 \
-storepass changeit \
-ext bc=ca:true \
-ext ku=keyCertSign,cRLSign \
-dname "CN=RootCA-A, O=MyCompany, C=UA"

2. Export public cert:
keytool -exportcert \
   -alias rootA \
   -keystore rootA.p12 \
   -storepass changeit \
   -rfc \
   -file rootA.cer

3. Create ROOT CA B (subordinate root)
   (Root B is trusted only because Root A signs it)

keytool -genkeypair \
-alias rootB \
-keyalg RSA \
-keysize 4096 \
-validity 3650 \
-storetype PKCS12 \
-keystore rootB.p12 \
-storepass changeit \
-dname "CN=RootCA-B, O=MyCompany, C=UA"

4. Create CSR

keytool -certreq \
-alias rootB \
-keystore rootB.p12 \
-storepass changeit \
-file rootB.csr

5. Sign Root B with Root A

keytool -gencert \
-alias rootA \
-keystore rootA.p12 \
-storepass changeit \
-infile rootB.csr \
-outfile rootB-signed.cer \
-rfc \
-validity 3650 \
-ext bc=ca:true,pathlen:0 \
-ext ku=keyCertSign,cRLSign

6. Import Root A + signed Root B into Root B keystore

keytool -importcert -alias rootA -file rootA.cer -keystore rootB.p12 -storepass changeit
keytool -importcert -alias rootB -file rootB-signed.cer -keystore rootB.p12 -storepass changeit

7. Create SERVER keypair (leaf cert)

keytool -genkeypair \
-alias server \
-keyalg RSA \
-keysize 2048 \
-validity 825 \
-storetype PKCS12 \
-keystore server-new.p12 \
-storepass changeit \
-dname "CN=myserver.local, O=MyCompany, C=UA"

8. Create CSR

keytool -certreq \
-alias server \
-keystore server-new.p12 \
-storepass changeit \
-file server.csr

9. Sign SERVER cert with Root B

keytool -gencert \
-alias rootB \
-keystore rootB.p12 \
-storepass changeit \
-infile server.csr \
-outfile server-signed.cer \
-rfc \
-validity 825 \
-ext ku=digitalSignature,keyEncipherment \
-ext eku=serverAuth \
-ext san=dns:localhost,dns:myserver.local

10. Build SERVER keystore chain

keytool -importcert -alias rootA -file rootA.cer -keystore server-new.p12 -storepass changeit
keytool -importcert -alias rootB -file rootB-signed.cer -keystore server-new.p12 -storepass changeit
keytool -importcert -alias server -file server-signed.cer -keystore server-new.p12 -storepass changeit

11. Verify
    keytool -list -v -keystore server-new.p12 -storepass changeit



12. Create CLIENT truststore

keytool -importcert \
-alias rootA \
-file rootA.cer \
-keystore client-truststore-new.p12 \
-storetype PKCS12 \
-storepass changeit





















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



