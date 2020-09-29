package pl.poznan.put.thesisapi.user;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Student implements User {

    private String name;
    private CertificateReader certificateReader = new CertificateReader();

    public Student(final String name) {
        this.name = name;
    }

    @Override
    public void register() {

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
