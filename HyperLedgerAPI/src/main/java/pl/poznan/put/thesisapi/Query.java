package pl.poznan.put.thesisapi;

import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public class Query {

    public String run(final String id) {
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

                System.out.println("Use thesis smart contract.");
                Contract contract = network.getContract("thesis");

                System.out.println("Evaluate query thesis transaction.");
                byte[] response = contract.evaluateTransaction("queryThesis", id);

                System.out.println(new String(response));
                return new String(response);
            }
        } catch (GatewayException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public String getAll() {
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

                System.out.println("Use thesis smart contract.");
                Contract contract = network.getContract("thesis");

                System.out.println("Evaluate query all thesis transaction.");
                byte[] response = contract.evaluateTransaction("queryAllThesis");

                return new String(response);
            }
        } catch (GatewayException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
