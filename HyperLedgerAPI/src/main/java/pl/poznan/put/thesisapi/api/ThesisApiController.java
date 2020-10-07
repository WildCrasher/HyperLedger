package pl.poznan.put.thesisapi.api;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.entities.AssignStudentDto;
import pl.poznan.put.thesisapi.thesis.Thesis;
import pl.poznan.put.thesisapi.thesis.ThesisRepository;
import pl.poznan.put.thesisapi.user.Student;
import pl.poznan.put.thesisapi.user.Supervisor;
import pl.poznan.put.thesisapi.user.User;
import pl.poznan.put.thesisapi.user.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController()
@RequestMapping("/api/thesis")
public class ThesisApiController {

    private final ThesisRepository thesisRepository;
    private final UserService userService;

    public ThesisApiController(
            final ThesisRepository thesisRepository,
            final UserService userService
    ) {
        this.thesisRepository = thesisRepository;
        this.userService = userService;
    }

    @PostMapping()
    public String addThesis(@RequestBody() Thesis thesis) {
        User user = new Student("Uzytkownik2");
        this.thesisRepository.save(thesis, user);
        return new Gson().toJson("ok");
    }

    @PostMapping("/assign")
    public ResponseEntity assignStudent(@RequestBody() AssignStudentDto body) throws Exception {
        User user = new Student("Uzytkownik2");
        boolean result = this.thesisRepository.assignStudent(body.getThesisNumber(), body.getStudent(), user);
        if(!result) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(new Gson().toJson("ok"));
    }

    @PostMapping("/approve")
    public ResponseEntity approveThesis(@RequestBody() String thesisNumber) {
        User user = new Student("Uzytkownik2");
        boolean result = this.thesisRepository.approveThesis(thesisNumber, user);
        if(!result) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(new Gson().toJson("ok"));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getThesis(@PathVariable(value = "id") String id) {
        User user = new Student("Uzytkownik2");
        return new Gson().toJson(this.thesisRepository.getById(id, user));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllThesis() {
        User user = new Student("Uzytkownik2");
        return new Gson().toJson(this.thesisRepository.getAll(user));
    }
}
