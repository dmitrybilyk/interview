# 1️⃣ Generate server keystore
keytool -genkeypair -alias server-cert -keyalg RSA -keysize 2048 \
-keystore server-keystore.jks -validity 3650 \
-storepass changeit -keypass changeit \
-dname "CN=localhost, OU=Dev, O=MyOrg, L=Lviv, ST=UA, C=UA"

# 2️⃣ Export server certificate
keytool -export -alias server-cert -keystore server-keystore.jks \
-rfc -file server-cert.cer -storepass changeit

# 3️⃣ Generate client keystore
keytool -genkeypair -alias client-cert -keyalg RSA -keysize 2048 \
-keystore client-keystore.jks -validity 3650 \
-storepass changeit -keypass changeit \
-dname "CN=client, OU=Dev, O=MyOrg, L=Lviv, ST=UA, C=UA"

# 4️⃣ Export client certificate
keytool -export -alias client-cert -keystore client-keystore.jks \
-rfc -file client-cert.cer -storepass changeit

# 5️⃣ Create truststores (each trusts the other)
keytool -import -alias server-cert -file server-cert.cer \
-keystore client-truststore.jks -storepass changeit -noprompt

keytool -import -alias client-cert -file client-cert.cer \
-keystore server-truststore.jks -storepass changeit -noprompt
