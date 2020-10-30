package pl.poznan.put.thesisapi.user;

import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Set;

public class SupervisorsAdmin implements User {

    private Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
    private X509Identity identity = (X509Identity)wallet.get("Admin@supervisors.put.poznan.pl");

    public SupervisorsAdmin() throws IOException {
        if (wallet.get("Admin@supervisors.put.poznan.pl") == null) {
            this.enroll();
        }
    }

    public void enroll() {
        try {
            // Create a CA client for interacting with the CA.
            Properties props = new Properties();
            props.put("pemFile",
                    "../network/organizations/peerOrganizations/supervisors.put.poznan.pl/ca/ca.supervisors.put.poznan.pl-cert.pem");
            props.put("allowAllHostNames", "true");
            HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Check to see if we've already enrolled the admin user.
            if (wallet.get("Admin@supervisors.put.poznan.pl") != null) {
                System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
                return;
            }

            // Enroll the admin user, and import the new identity into the wallet.
            final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost("localhost");
            enrollmentRequestTLS.setProfile("tls");
            Enrollment enrollment = caClient.enroll("admin", "adminpw", enrollmentRequestTLS);
            Identity user = Identities.newX509Identity("SupervisorsMSP", enrollment);
            wallet.put("Admin@supervisors.put.poznan.pl", user);
            identity = (X509Identity)wallet.get("Admin@supervisors.put.poznan.pl");
            System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
        } catch (Exception e) {
            System.err.println("Error adding to wallet");
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return "supervisors.department1";
    }

    @Override
    public Enrollment getEnrollment() {
        return new Enrollment() {

            @Override
            public PrivateKey getKey() {
                return identity.getPrivateKey();
            }

            @Override
            public String getCert() {
                return Identities.toPemString(identity.getCertificate());
            }
        };
    }

    @Override
    public String getMspId() {
        return "SupervisorsMSP";
    }

    public X509Certificate getCert() {
        return identity.getCertificate();
    }

    public PrivateKey getKey() {
        return identity.getPrivateKey();
    }
}
