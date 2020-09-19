

function createOrg1 {

  echo
	echo "Enroll the CA admin"
  echo
	mkdir -p organizations/peerOrganizations/supervisors.put.poznan.pl/

	export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/
#  rm -rf $FABRIC_CA_CLIENT_HOME/fabric-ca-client-config.yaml
#  rm -rf $FABRIC_CA_CLIENT_HOME/msp

  set -x
  fabric-ca-client enroll -u https://admin:adminpw@localhost:7054 --caname ca-supervisors --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

  echo 'NodeOUs:
  Enable: true
  ClientOUIdentifier:
    Certificate: cacerts/localhost-7054-ca-supervisors.pem
    OrganizationalUnitIdentifier: client
  PeerOUIdentifier:
    Certificate: cacerts/localhost-7054-ca-supervisors.pem
    OrganizationalUnitIdentifier: peer
  AdminOUIdentifier:
    Certificate: cacerts/localhost-7054-ca-supervisors.pem
    OrganizationalUnitIdentifier: admin
  OrdererOUIdentifier:
    Certificate: cacerts/localhost-7054-ca-supervisors.pem
    OrganizationalUnitIdentifier: orderer' > ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/msp/config.yaml

  echo
	echo "Register peer0"
  echo
  set -x
	fabric-ca-client register --caname ca-supervisors --id.name peer0 --id.secret peer0pw --id.type peer --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

  echo
  echo "Register user"
  echo
  set -x
  fabric-ca-client register --caname ca-supervisors --id.name user1 --id.secret user1pw --id.type client --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

  echo
  echo "Register the org admin"
  echo
  set -x
  fabric-ca-client register --caname ca-supervisors --id.name supervisorsadmin --id.secret supervisorsadminpw --id.type admin --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

	mkdir -p organizations/peerOrganizations/supervisors.put.poznan.pl/peers
  mkdir -p organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl

  echo
  echo "## Generate the peer0 msp"
  echo
  set -x
	fabric-ca-client enroll -u https://peer0:peer0pw@localhost:7054 --caname ca-supervisors -M ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/msp --csr.hosts peer0.supervisors.put.poznan.pl --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/msp/config.yaml ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/msp/config.yaml

  echo
  echo "## Generate the peer0-tls certificates"
  echo
  set -x
  fabric-ca-client enroll -u https://peer0:peer0pw@localhost:7054 --caname ca-supervisors -M ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls --enrollment.profile tls --csr.hosts peer0.supervisors.put.poznan.pl --csr.hosts localhost --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x


  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/ca.crt
  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/signcerts/* ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/server.crt
  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/keystore/* ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/server.key

  mkdir ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/msp/tlscacerts
  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/msp/tlscacerts/ca.crt

  mkdir ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/tlsca
  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/tlsca/tlsca.supervisors.put.poznan.pl-cert.pem

  mkdir ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/ca
  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/peers/peer0.supervisors.put.poznan.pl/msp/cacerts/* ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/ca/ca.supervisors.put.poznan.pl-cert.pem

  mkdir -p organizations/peerOrganizations/supervisors.put.poznan.pl/users
  mkdir -p organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl

  echo
  echo "## Generate the user msp"
  echo
  set -x
	fabric-ca-client enroll -u https://user1:user1pw@localhost:7054 --caname ca-supervisors -M ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/msp/config.yaml ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/users/User1@supervisors.put.poznan.pl/msp/config.yaml

  mkdir -p organizations/peerOrganizations/supervisors.put.poznan.pl/users/Admin@supervisors.put.poznan.pl

  echo
  echo "## Generate the org admin msp"
  echo
  set -x
	fabric-ca-client enroll -u https://supervisorsadmin:supervisorsadminpw@localhost:7054 --caname ca-supervisors -M ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/users/Admin@supervisors.put.poznan.pl/msp --tls.certfiles ${PWD}/organizations/fabric-ca/supervisors/tls-cert.pem
  set +x

  cp ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/msp/config.yaml ${PWD}/organizations/peerOrganizations/supervisors.put.poznan.pl/users/Admin@supervisors.put.poznan.pl/msp/config.yaml

}


function createOrg2 {

  echo
	echo "Enroll the CA admin"
  echo
	mkdir -p organizations/peerOrganizations/students.put.poznan.pl/

	export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/peerOrganizations/students.put.poznan.pl/
#  rm -rf $FABRIC_CA_CLIENT_HOME/fabric-ca-client-config.yaml
#  rm -rf $FABRIC_CA_CLIENT_HOME/msp

  set -x
  fabric-ca-client enroll -u https://admin:adminpw@localhost:8054 --caname ca-students --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

  echo 'NodeOUs:
  Enable: true
  ClientOUIdentifier:
    Certificate: cacerts/localhost-8054-ca-students.pem
    OrganizationalUnitIdentifier: client
  PeerOUIdentifier:
    Certificate: cacerts/localhost-8054-ca-students.pem
    OrganizationalUnitIdentifier: peer
  AdminOUIdentifier:
    Certificate: cacerts/localhost-8054-ca-students.pem
    OrganizationalUnitIdentifier: admin
  OrdererOUIdentifier:
    Certificate: cacerts/localhost-8054-ca-students.pem
    OrganizationalUnitIdentifier: orderer' > ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/msp/config.yaml

  echo
	echo "Register peer0"
  echo
  set -x
	fabric-ca-client register --caname ca-students --id.name peer0 --id.secret peer0pw --id.type peer --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

  echo
  echo "Register user"
  echo
  set -x
  fabric-ca-client register --caname ca-students --id.name user1 --id.secret user1pw --id.type client --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

  echo
  echo "Register the org admin"
  echo
  set -x
  fabric-ca-client register --caname ca-students --id.name studentsadmin --id.secret studentsadminpw --id.type admin --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

	mkdir -p organizations/peerOrganizations/students.put.poznan.pl/peers
  mkdir -p organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl

  echo
  echo "## Generate the peer0 msp"
  echo
  set -x
	fabric-ca-client enroll -u https://peer0:peer0pw@localhost:8054 --caname ca-students -M ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/msp --csr.hosts peer0.students.put.poznan.pl --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/msp/config.yaml ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/msp/config.yaml

  echo
  echo "## Generate the peer0-tls certificates"
  echo
  set -x
  fabric-ca-client enroll -u https://peer0:peer0pw@localhost:8054 --caname ca-students -M ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls --enrollment.profile tls --csr.hosts peer0.students.put.poznan.pl --csr.hosts localhost --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x


  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/ca.crt
  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/signcerts/* ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/server.crt
  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/keystore/* ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/server.key

  mkdir ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/msp/tlscacerts
  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/msp/tlscacerts/ca.crt

  mkdir ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/tlsca
  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/tlsca/tlsca.students.put.poznan.pl-cert.pem

  mkdir ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/ca
  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/peers/peer0.students.put.poznan.pl/msp/cacerts/* ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/ca/ca.students.put.poznan.pl-cert.pem

  mkdir -p organizations/peerOrganizations/students.put.poznan.pl/users
  mkdir -p organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl

  echo
  echo "## Generate the user msp"
  echo
  set -x
	fabric-ca-client enroll -u https://user1:user1pw@localhost:8054 --caname ca-students -M ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/msp/config.yaml ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/users/User1@students.put.poznan.pl/msp/config.yaml

  mkdir -p organizations/peerOrganizations/students.put.poznan.pl/users/Admin@students.put.poznan.pl

  echo
  echo "## Generate the org admin msp"
  echo
  set -x
	fabric-ca-client enroll -u https://studentsadmin:studentsadminpw@localhost:8054 --caname ca-students -M ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/users/Admin@students.put.poznan.pl/msp --tls.certfiles ${PWD}/organizations/fabric-ca/students/tls-cert.pem
  set +x

  cp ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/msp/config.yaml ${PWD}/organizations/peerOrganizations/students.put.poznan.pl/users/Admin@students.put.poznan.pl/msp/config.yaml

}

function createOrderer {

  echo
	echo "Enroll the CA admin"
  echo
	mkdir -p organizations/ordererOrganizations/put.poznan.pl

	export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/ordererOrganizations/put.poznan.pl
#  rm -rf $FABRIC_CA_CLIENT_HOME/fabric-ca-client-config.yaml
#  rm -rf $FABRIC_CA_CLIENT_HOME/msp

  set -x
  fabric-ca-client enroll -u https://admin:adminpw@localhost:9054 --caname ca-orderer --tls.certfiles ${PWD}/organizations/fabric-ca/ordererOrg/tls-cert.pem
  set +x

  echo 'NodeOUs:
  Enable: true
  ClientOUIdentifier:
    Certificate: cacerts/localhost-9054-ca-orderer.pem
    OrganizationalUnitIdentifier: client
  PeerOUIdentifier:
    Certificate: cacerts/localhost-9054-ca-orderer.pem
    OrganizationalUnitIdentifier: peer
  AdminOUIdentifier:
    Certificate: cacerts/localhost-9054-ca-orderer.pem
    OrganizationalUnitIdentifier: admin
  OrdererOUIdentifier:
    Certificate: cacerts/localhost-9054-ca-orderer.pem
    OrganizationalUnitIdentifier: orderer' > ${PWD}/organizations/ordererOrganizations/put.poznan.pl/msp/config.yaml


  echo
	echo "Register orderer"
  echo
  set -x
	fabric-ca-client register --caname ca-orderer --id.name orderer --id.secret ordererpw --id.type orderer --tls.certfiles ${PWD}/organizations/fabric-ca/ordererOrg/tls-cert.pem
    set +x

  echo
  echo "Register the orderer admin"
  echo
  set -x
  fabric-ca-client register --caname ca-orderer --id.name ordererAdmin --id.secret ordererAdminpw --id.type admin --tls.certfiles ${PWD}/organizations/fabric-ca/ordererOrg/tls-cert.pem
  set +x

	mkdir -p organizations/ordererOrganizations/put.poznan.pl/orderers
  mkdir -p organizations/ordererOrganizations/put.poznan.pl/orderers/put.poznan.pl

  mkdir -p organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl

  echo
  echo "## Generate the orderer msp"
  echo
  set -x
	fabric-ca-client enroll -u https://orderer:ordererpw@localhost:9054 --caname ca-orderer -M ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/msp --csr.hosts orderer.put.poznan.pl --csr.hosts localhost --tls.certfiles ${PWD}/organizations/fabric-ca/ordererOrg/tls-cert.pem
  set +x

  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/msp/config.yaml ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/msp/config.yaml

  echo
  echo "## Generate the orderer-tls certificates"
  echo
  set -x
  fabric-ca-client enroll -u https://orderer:ordererpw@localhost:9054 --caname ca-orderer -M ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls --enrollment.profile tls --csr.hosts orderer.put.poznan.pl --csr.hosts localhost --tls.certfiles ${PWD}/organizations/fabric-ca/ordererOrg/tls-cert.pem
  set +x

  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/ca.crt
  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/signcerts/* ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/server.crt
  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/keystore/* ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/server.key

  mkdir ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/msp/tlscacerts
  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/msp/tlscacerts/tlsca.put.poznan.pl-cert.pem

  mkdir ${PWD}/organizations/ordererOrganizations/put.poznan.pl/msp/tlscacerts
  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/orderers/orderer.put.poznan.pl/tls/tlscacerts/* ${PWD}/organizations/ordererOrganizations/put.poznan.pl/msp/tlscacerts/tlsca.put.poznan.pl-cert.pem

  mkdir -p organizations/ordererOrganizations/put.poznan.pl/users
  mkdir -p organizations/ordererOrganizations/put.poznan.pl/users/Admin@put.poznan.pl

  echo
  echo "## Generate the admin msp"
  echo
  set -x
	fabric-ca-client enroll -u https://ordererAdmin:ordererAdminpw@localhost:9054 --caname ca-orderer -M ${PWD}/organizations/ordererOrganizations/put.poznan.pl/users/Admin@put.poznan.pl/msp --tls.certfiles ${PWD}/organizations/fabric-ca/ordererOrg/tls-cert.pem
  set +x

  cp ${PWD}/organizations/ordererOrganizations/put.poznan.pl/msp/config.yaml ${PWD}/organizations/ordererOrganizations/put.poznan.pl/users/Admin@put.poznan.pl/msp/config.yaml


}
