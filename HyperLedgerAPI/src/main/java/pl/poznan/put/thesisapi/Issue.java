/*
SPDX-License-Identifier: Apache-2.0
*/

package pl.poznan.put.thesisapi;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.GatewayException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class Issue {

  private static final String ENVKEY="CONTRACT_NAME";

  public void run() {

    Gateway.Builder builder = Gateway.createBuilder();

    try {
      // A wallet stores a collection of identities
      Path walletPath = Paths.get(".", "wallet");
      Wallet wallet = Wallets.newFileSystemWallet(walletPath);
      System.out.println("Read wallet info from: " + walletPath.toString());

      String userName = "User1@supervisors.put.poznan.pl";

      Path connectionProfile = Paths.get("..", "organization", "supervisors", "gateway", "connection-supervisors.yaml");

      // Set connection options on the gateway builder
      builder.identity(wallet, userName).networkConfig(connectionProfile).discovery(false);

      // Connect to gateway using application specified parameters
      try(Gateway gateway = builder.connect()) {

        System.out.println("Use network channel: mychannel.");
        Network network = gateway.getNetwork("mychannel");

        System.out.println("Use pl.poznan.put.thesis smart contract.");
        Contract contract = network.getContract("thesis");

        System.out.println("Submit thesis issue transaction.");
        byte[] response = contract.submitTransaction("issue", "promotor1", "1", "2020-09-19", "temat");

        System.out.println(response);
      }
    } catch (GatewayException | IOException | TimeoutException | InterruptedException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

}