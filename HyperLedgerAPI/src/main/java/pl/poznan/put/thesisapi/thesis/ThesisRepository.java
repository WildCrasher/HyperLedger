package pl.poznan.put.thesisapi.thesis;

import pl.poznan.put.thesisapi.user.User;

public interface ThesisRepository {

    void save(final Thesis thesis, final User user);
    String getById(final String id, final User user);
    String getAll(final User user);
}
