#!/bin/bash
#
# SPDX-License-Identifier: Apache-2.0

function _exit(){
    printf "Exiting:%s\n" "$1"
    exit -1
}

# Exit on first error, print all commands.
set -ev
set -o pipefail

# Where am I?
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export FABRIC_CFG_PATH="${DIR}/../config"

cd "${DIR}/../network/"
#tutaj tez te nazwy jeszcze nie sa mi znane
docker kill cliStudents cliSupervisors logspout || true
./network.sh down
./network.sh up createChannel -ca -s couchdb

# Copy the connection profiles so they are in the correct organizations.
cp "${DIR}/../network/organizations/peerOrganizations/supervisors.put.poznan.pl/connection-supervisors.yaml" "${DIR}/organization/supervisors/gateway/"
cp "${DIR}/../network/organizations/peerOrganizations/students.put.poznan.pl/connection-students.yaml" "${DIR}/organization/students/gateway/"

cp ${DIR}/../network/organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/signcerts/* ${DIR}/../network/organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/signcerts/User1@supervisors.put.poznan.pl-cert.pem
cp ${DIR}/../network/organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/keystore/* ${DIR}/../network/organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/keystore/priv_sk

cp ${DIR}/../network/organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/signcerts/* ${DIR}/../network/organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/signcerts/User1@students.put.poznan.pl-cert.pem
cp ${DIR}/../network/organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/keystore/* ${DIR}/../network/organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/keystore/priv_sk

echo Suggest that you monitor the docker containers by running

echo "./organization/supervisors/configuration/cli/monitordocker.sh network"
