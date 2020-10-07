package pl.poznan.put.thesisapi.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.thesis.Thesis;
import pl.poznan.put.thesisapi.thesis.ThesisRepository;
import pl.poznan.put.thesisapi.user.Student;
import pl.poznan.put.thesisapi.user.Supervisor;
import pl.poznan.put.thesisapi.user.User;
import pl.poznan.put.thesisapi.user.UserService;

import java.util.Map;

import static pl.poznan.put.thesisapi.security.SecurityConstants.*;

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
    public String addThesis(@RequestBody() Thesis thesis, @RequestHeader Map<String, String> headers) {
        String token = headers.get(HEADER_STRING.toLowerCase());

        String username = userService.getUsernameFromJWTToken(token);

        UserEntity userEntity = userService.getUserEntityByUsername(username);

        User user = new Student(username);
//        this.thesisRepository.save(thesis, user);
        return new Gson().toJson("ok");
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
