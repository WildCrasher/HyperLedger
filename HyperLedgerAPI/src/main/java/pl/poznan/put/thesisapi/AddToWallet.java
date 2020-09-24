/*
SPDX-License-Identifier: Apache-2.0
*/

package pl.poznan.put.thesisapi;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identity;

public class AddToWallet {

  private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
    try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
      return Identities.readX509Certificate(certificateReader);
    }
  }

  private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
    try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
      return Identities.readPrivateKey(privateKeyReader);
    }
  }

  public void run() {
    try {
      // A wallet stores a collection of identities
      Path walletPath = Paths.get(".", "wallet");
      Wallet wallet = Wallets.newFileSystemWallet(walletPath);

      Path credentialPath = Paths.get("..", "network", "organizations", "peerOrganizations",
              "supervisors.put.poznan.pl", "users", "User1@supervisors.put.poznan.pl", "msp");
      System.out.println("credentialPath: " + credentialPath.toString());
      Path certificatePath = credentialPath.resolve(Paths.get("signcerts",
              "User1@supervisors.put.poznan.pl-cert.pem"));
      System.out.println("certificatePem: " + certificatePath.toString());
      Path privateKeyPath = credentialPath.resolve(Paths.get("keystore",
              "priv_sk"));

      X509Certificate certificate = readX509Certificate(certificatePath);
      PrivateKey privateKey = getPrivateKey(privateKeyPath);

      Identity identity = Identities.newX509Identity("SupervisorsMSP", certificate, privateKey);


      String identityLabel = "User1@supervisors.put.poznan.pl";
      wallet.put(identityLabel, identity);

      System.out.println("Write wallet info into " + walletPath.toString() + " successfully.");

    } catch (IOException | CertificateException | InvalidKeyException e) {
      System.err.println("Error adding to wallet");
      e.printStackTrace();
    }
  }

}