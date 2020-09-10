#!/bin/bash

function one_line_pem {
    echo "`awk 'NF {sub(/\\n/, ""); printf "%s\\\\\\\n",$0;}' $1`"
}

function json_ccp {
    local PP=$(one_line_pem $5)
    local CP=$(one_line_pem $6)
    sed -e "s/\${ORGName}/$1/" \
        -e "s/\${ORGPath}/$2/" \
        -e "s/\${P0PORT}/$3/" \
        -e "s/\${CAPORT}/$4/" \
        -e "s#\${PEERPEM}#$PP#" \
        -e "s#\${CAPEM}#$CP#" \
        organizations/ccp-template.json
}

function yaml_ccp {
    local PP=$(one_line_pem $5)
    local CP=$(one_line_pem $6)
    sed -e "s/\${ORGName}/$1/" \
        -e "s/\${ORGPath}/$2/" \
        -e "s/\${P0PORT}/$3/" \
        -e "s/\${CAPORT}/$4/" \
        -e "s#\${PEERPEM}#$PP#" \
        -e "s#\${CAPEM}#$CP#" \
        organizations/ccp-template.yaml | sed -e $'s/\\\\n/\\\n          /g'
}

ORGName=Supervisors
ORGPath=supervisors
P0PORT=7051
CAPORT=7054
PEERPEM=organizations/peerOrganizations/supervisors.put.poznan.pl/tlsca/tlsca.supervisors.put.poznan.pl-cert.pem
CAPEM=organizations/peerOrganizations/supervisors.put.poznan.pl/ca/ca.supervisors.put.poznan.pl-cert.pem

echo "$(json_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/supervisors.put.poznan.pl/connection-supervisors.json
echo "$(yaml_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/supervisors.put.poznan.pl/connection-supervisors.yaml

ORGName=Students
ORGPath=students
P0PORT=9051
CAPORT=8054
PEERPEM=organizations/peerOrganizations/students.put.poznan.pl/tlsca/tlsca.students.put.poznan.pl-cert.pem
CAPEM=organizations/peerOrganizations/students.put.poznan.pl/ca/ca.students.put.poznan.pl-cert.pem

echo "$(json_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/students.put.poznan.pl/connection-students.json
echo "$(yaml_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/students.put.poznan.pl/connection-students.yaml
