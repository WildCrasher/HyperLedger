package pl.poznan.put.thesisapi.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.Thesis;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.repositories.ThesisRepository;
import pl.poznan.put.thesisapi.user.Supervisor;
import pl.poznan.put.thesisapi.user.User;
import pl.poznan.put.thesisapi.user.UserService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController()
@RequestMapping("/api/thesis")
public class ThesisApiController {

    private final ThesisRepository thesisRepository;
    private final UserService userService;

    public ThesisApiController(final ThesisRepository thesisRepository, final UserService userService) {
        this.thesisRepository = thesisRepository;
        this.userService = userService;
    }

    @PostMapping()
    public String addThesis(@RequestBody() Thesis thesis) {
        User user = new Supervisor("User1");
        user.addToWallet();
        this.thesisRepository.save(thesis, user);
        return "ok";
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getThesis(@PathVariable(value = "id") String id) {
        User user = new Supervisor("User1");
        user.addToWallet();
        return this.thesisRepository.getById(id, user);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllThesis() {
        User user = new Supervisor("User1");
        user.addToWallet();
        return this.thesisRepository.getAll(user);
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public String testDb() {
//        List<UserEntity> result = userService.list();
//        return result.get(0).getRole();
//    }
}
