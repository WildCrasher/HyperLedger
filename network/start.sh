#!/bin/bash

./network.sh down
./network.sh up createChannel -ca
./network.sh deployCC -l java

cp ./organizations/peerOrganizations/students.put.poznan.pl/connection-students.yaml ../organization/students/gateway/
cp ./organizations/peerOrganizations/supervisors.put.poznan.pl/connection-supervisors.yaml ../organization/supervisors/gateway/

cp ./organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/signcerts/* ./organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/signcerts/User1@students.put.poznan.pl-cert.pem
cp ./organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/keystore/* ./organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/keystore/priv_sk

cp ./organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/signcerts/* ./organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/signcerts/User1@supervisors.put.poznan.pl-cert.pem
cp ./organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/keystore/* ./organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/keystore/priv_sk
