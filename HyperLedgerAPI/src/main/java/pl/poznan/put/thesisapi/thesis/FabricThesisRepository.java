package pl.poznan.put.thesisapi.thesis;

import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Repository;
import pl.poznan.put.thesisapi.user.User;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

@Repository
public class FabricThesisRepository implements ThesisRepository {
    @Override
    public String save(final Thesis thesis, final User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            byte[] response = contract.submitTransaction(
                    "issue",
                    user.getName(),
                    thesis.getThesisNumber(),
                    thesis.getIssueDateTime(),
                    thesis.getTopic()
            );

            System.out.println(response);
        } catch (RuntimeException | GatewayException | IOException | TimeoutException | InterruptedException e) {
            if(e.getMessage().contains("cannotPerformAction")) {
                return "cannotPerformAction";
            }
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @Override
    public String assignStudent(String thesisNumber, String student, int priority, User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            byte[] response = contract.submitTransaction(
                    "assignStudent",
                    thesisNumber,
                    student,
                    String.valueOf(priority)
            );

            System.out.println(response);
        } catch (RuntimeException | GatewayException | IOException | TimeoutException | InterruptedException e) {
            if(e.getMessage().contains("cannotPerformAction")) {
                return "cannotPerformAction";
            }
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @Override
    public String chooseStudent(String thesisNumber, String student, User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            byte[] response = contract.submitTransaction(
                    "chooseStudent",
                    thesisNumber,
                    student
            );

            System.out.println(response);
        } catch (RuntimeException | GatewayException | IOException | TimeoutException | InterruptedException e) {
            if(e.getMessage().contains("cannotPerformAction")) {
                return "cannotPerformAction";
            }
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @Override
    public String revokeThesis(String thesisNumber, User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            byte[] response = contract.submitTransaction(
                    "revokeThesis",
                    thesisNumber,
                    user.getName()
            );

            System.out.println(response);
        } catch (RuntimeException | GatewayException | IOException | TimeoutException | InterruptedException e) {
            if(e.getMessage().contains("cannotPerformAction")) {
                return "cannotPerformAction";
            }
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @Override
    public String getById(final String id, final User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            System.out.println("Evaluate query thesis transaction.");
            byte[] response = contract.evaluateTransaction("queryThesis", id);

            return new String(response);

        } catch (GatewayException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getAll(final User user) {
        try {
            Gateway gateway = this.getGateway(user);
            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            System.out.println("Evaluate query all thesis transaction.");
            byte[] response = contract.evaluateTransaction("queryAllThesis");

            return new String(response);
        } catch (GatewayException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    @Override
    public String acceptAssignment(String thesisNumber, User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            byte[] response = contract.submitTransaction(
                    "acceptAssignment",
                    thesisNumber,
                    user.getName()
            );

            System.out.println(response);
        } catch (RuntimeException | GatewayException | IOException | TimeoutException | InterruptedException e) {
            if(e.getMessage().contains("cannotPerformAction")) {
                return "cannotPerformAction";
            }
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @Override
    public String declineAssignment(String thesisNumber, User user) {
        try {
            Gateway gateway = this.getGateway(user);

            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            byte[] response = contract.submitTransaction(
                    "declineAssignment",
                    thesisNumber,
                    user.getName()
            );

            System.out.println(response);
        } catch (RuntimeException | GatewayException | IOException | TimeoutException | InterruptedException e) {
            if(e.getMessage().contains("cannotPerformAction")) {
                return "cannotPerformAction";
            }
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @Override
    public String getStudentTheses(User user) {
        try {
            Gateway gateway = this.getGateway(user);
            Network network = gateway.getNetwork("mychannel");

            Contract contract = network.getContract("thesis");

            System.out.println("Evaluate query student theses transaction.");
            byte[] response = contract.evaluateTransaction("queryStudentTheses", user.getName());

            return new String(response);
        } catch (GatewayException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    private Gateway getGateway(final User user) throws IOException {
        Gateway.Builder builder = Gateway.createBuilder();

        Path walletPath = Paths.get(".", "wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

        Path connectionProfile = Paths.get("..", "organization", user.getOrganization(), "gateway",
                "connection-" + user.getOrganization() + ".yaml");

        builder.identity(wallet, user.getFullName()).networkConfig(connectionProfile).discovery(false);

        return builder.connect();
    }
}
