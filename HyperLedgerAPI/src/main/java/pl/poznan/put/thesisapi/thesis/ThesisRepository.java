package pl.poznan.put.thesisapi.thesis;

import pl.poznan.put.thesisapi.entities.AssignStudentDto;
import pl.poznan.put.thesisapi.user.Student;
import pl.poznan.put.thesisapi.user.Supervisor;
import pl.poznan.put.thesisapi.user.User;

public interface ThesisRepository {

    void save(final Thesis thesis, final User user);
    boolean assignStudent(final String thesisNumber, final String student, final User user);
    boolean approveThesis(final String thesisNumber, final User user);
    String getById(final String id, final User user);
    String getAll(final User user);

}
