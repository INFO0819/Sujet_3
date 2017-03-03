#/bin/bash

rm A1*
rm A2*
rm A3*

rm -R /tmp/ca
mkdir -p /tmp/ca
chemin=$(pwd)
cd /tmp/ca
mkdir -p certs crl newcerts private
chmod 700 private
touch index.txt
echo 1001 > serial

touch /tmp/ca/openssl.cnf
cp $chemin/openssl.cnf /tmp/ca/openssl.cnf

#génération clé CA
openssl genrsa -aes256 -passout pass:foobar -out private/ca.key.pem 4096
chmod 600 private/ca.key.pem

#autosignature
openssl req -config openssl.cnf \
      -key private/ca.key.pem \
      -new -x509 -days 7300 -sha256 -passin pass:"foobar" -extensions v3_ca \
      -out certs/ca.cert.pem -subj "/C=FR/ST=France/L=Reims/O=urca/OU=IT/CN=example.com"

chmod 600 certs/ca.cert.pem

openssl x509 -noout -text -in certs/ca.cert.pem

# # création requête de certificat
# openssl req -config openssl.cnf \
#       -key A2.priv \
#       -new -sha256  -passin pass:"foobar" -subj "/C=FR/ST=France/L=Reims/O=urca/OU=IT/CN=lol.example.com" -out test2.csr.pem
#
# #création certificat
# openssl ca -config openssl.cnf \
#       -extensions server_cert -days 375 -notext -md sha256 -passin pass:"foobar" \
#       -in test2.csr.pem  \
#       -out test2.cert.pem

# chmod 444 test2.cert.pem
#
# # openssl x509 -noout -text \
# #         -in test2.cert.pem
#
# openssl verify -verbose -CAfile /tmp/ca/certs/ca.cert.pem /tmp/ca/test2.cert.pem
#
# openssl x509 -pubkey -noout -in /tmp/ca/test2.cert.pem > /tmp/ca/toto
