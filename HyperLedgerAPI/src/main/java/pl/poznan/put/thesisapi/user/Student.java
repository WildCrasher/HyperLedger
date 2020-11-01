package pl.poznan.put.thesisapi.user;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class Student implements User {

    private String name;
    private CertificateReader certificateReader = new CertificateReader();

    public Student(final String name) {
        this.name = name;
    }

    @Override
    public void register() {
        try {
            // Create a CA client for interacting with the CA.
            Properties props = new Properties();
            props.put("pemFile",
                    "../network/organizations/peerOrganizations/students.put.poznan.pl/ca/ca.students.put.poznan.pl-cert.pem");
            props.put("allowAllHostNames", "true");
            HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:8054", props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Create a wallet for managing identities
            Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

            // Check to see if we've already enrolled the user.
            if (wallet.get(this.getFullName()) != null) {
                System.out.println("An identity for the user " + this.name + " already exists in the wallet");
                return;
            }

            StudentsAdmin admin = new StudentsAdmin();

            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest(this.name);
            registrationRequest.setAffiliation("students.department1");
            registrationRequest.setEnrollmentID(this.name);
            String enrollmentSecret = caClient.register(registrationRequest, admin);
            Enrollment enrollment = caClient.enroll(this.name, enrollmentSecret);
            Identity user = Identities.newX509Identity("StudentsMSP", enrollment);
            wallet.put(this.getFullName(), user);
            System.out.println("Successfully enrolled user " + this.name + " and imported it into the wallet");

        } catch (Exception e) {
            System.err.println("Error registering user: " + this.name);
            e.printStackTrace();
        }
    }

    @Override
    public void addToWallet() {
        try {
            // A wallet stores a collection of identities
            Path walletPath = Paths.get(".", "wallet");
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);

            Path credentialPath = Paths.get("..", "network", "organizations", "peerOrganizations",
                    "students.put.poznan.pl", "users", this.name + "@students.put.poznan.pl", "msp");
            System.out.println("credentialPath: " + credentialPath.toString());
            Path certificatePath = credentialPath.resolve(Paths.get("signcerts",
                    this.name + "@students.put.poznan.pl-cert.pem"));
            System.out.println("certificatePem: " + certificatePath.toString());
            Path privateKeyPath = credentialPath.resolve(Paths.get("keystore",
                    "priv_sk"));

            X509Certificate certificate = certificateReader.readX509Certificate(certificatePath);
            PrivateKey privateKey = certificateReader.getPrivateKey(privateKeyPath);

            Identity identity = Identities.newX509Identity("StudentsMSP", certificate, privateKey);


            String identityLabel = this.name + "@students.put.poznan.pl";
            wallet.put(identityLabel, identity);

            System.out.println("Write wallet info into " + walletPath.toString() + " successfully.");

        } catch (IOException | CertificateException | InvalidKeyException e) {
            System.err.println("Error adding to wallet");
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOrganization() {
        return "students";
    }

    @Override
    public String getFullName() {
        return this.name + "@students.put.poznan.pl";
    }

    public void setName(String name) {
        this.name = name;
    }
}
