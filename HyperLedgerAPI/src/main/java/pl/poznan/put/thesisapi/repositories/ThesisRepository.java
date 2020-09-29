package pl.poznan.put.thesisapi.repositories;

import pl.poznan.put.thesisapi.Thesis;
import pl.poznan.put.thesisapi.user.User;

public interface ThesisRepository {

    void save(final Thesis thesis, final User user);
    String getById(final String id, final User user);
    String getAll(final User user);
}
