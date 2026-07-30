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


@startuml
participant Client
participant Server
participant CA as "CA / Truststore"

Client -> Server: ClientHello\n(TLS versions, ciphers, ClientRandom)

Server -> Client: ServerHello\n(Chosen cipher, ServerRandom)
Server -> Client: Certificate Chain\n(Leaf + Intermediate)

Client -> CA: Verify certificate chain
CA --> Client: Root CA trusted

note right of Client
- Verify signatures
- Check SAN / hostname
- Check expiry
- Check EKU=serverAuth
  end note

Client -> Server: (implicit challenge)

Server -> Client: Signature\n(Signed handshake hash\nusing server PRIVATE key)

Client -> Client: Verify signature\n(using PUBLIC key from cert)

note right of Client
Only private-key holder
can create valid signature
end note

Client -> Server: Encrypted traffic starts
Server -> Client: Encrypted traffic starts

@enduml





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




