package pl.poznan.put.thesisapi.user;

public interface User {

    String getName();
    String getFullName();
    String getOrganization();
    void register();
    void addToWallet();
}
