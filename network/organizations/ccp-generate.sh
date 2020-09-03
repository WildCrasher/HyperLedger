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

ORGName=SupervisorsOrg
ORGPath=supervisorsorg
P0PORT=7051
CAPORT=7054
PEERPEM=organizations/peerOrganizations/supervisorsorg.put.poznan.pl/tlsca/tlsca.supervisorsorg.put.poznan.pl-cert.pem
CAPEM=organizations/peerOrganizations/supervisorsorg.put.poznan.pl/ca/ca.supervisorsorg.put.poznan.pl-cert.pem

echo "$(json_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/supervisorsorg.put.poznan.pl/connection-supervisorsorg.json
echo "$(yaml_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/supervisorsorg.put.poznan.pl/connection-supervisorsorg.yaml

ORGName=StudentOrg
ORGPath=studentsorg
P0PORT=9051
CAPORT=8054
PEERPEM=organizations/peerOrganizations/studentsorg.put.poznan.pl/tlsca/tlsca.studentsorg.put.poznan.pl-cert.pem
CAPEM=organizations/peerOrganizations/studentsorg.put.poznan.pl/ca/ca.studentsorg.put.poznan.pl-cert.pem

echo "$(json_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/studentsorg.put.poznan.pl/connection-studentsorg.json
echo "$(yaml_ccp $ORGName $ORGPath $P0PORT $CAPORT $PEERPEM $CAPEM)" > organizations/peerOrganizations/studentsorg.put.poznan.pl/connection-studentsorg.yaml
