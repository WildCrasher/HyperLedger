package pl.poznan.put.thesisapi.api;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.entities.ChooseStudentDto;
import pl.poznan.put.thesisapi.entities.AssignStudentDto;
import pl.poznan.put.thesisapi.entities.RevokeThesisDto;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.thesis.Thesis;
import pl.poznan.put.thesisapi.thesis.ThesisRepository;
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
        User user = this.getUserFromHeaders(headers);
        String result = this.thesisRepository.save(thesis, user);
        return new Gson().toJson(result);
    }

    @PostMapping("/assign")
    public ResponseEntity assignStudent(@RequestBody() AssignStudentDto body, @RequestHeader Map<String, String> headers) throws Exception {
        User user = this.getUserFromHeaders(headers);
        String result = this.thesisRepository.assignStudent(body.getThesisNumber(), body.getStudent(), body.getPriority(), user);
        if(!result.equals("success")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(new Gson().toJson(result));
    }

    @PostMapping("/choose-student")
    public ResponseEntity chooseStudent(@RequestBody() ChooseStudentDto body, @RequestHeader Map<String, String> headers) {
        User user = this.getUserFromHeaders(headers);
        String result = this.thesisRepository.chooseStudent(body.getThesisNumber(), body.getStudent(), user);
        if(!result.equals("success")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(new Gson().toJson(result));
    }

    @PostMapping("/revoke")
    public ResponseEntity revokeThesis(@RequestBody() RevokeThesisDto body, @RequestHeader Map<String, String> headers) {
        User user = this.getUserFromHeaders(headers);
        String result = this.thesisRepository.revokeThesis(body.getThesisNumber(), user);
        if(!result.equals("success")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(new Gson().toJson(result));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getThesis(@PathVariable(value = "id") String id, @RequestHeader Map<String, String> headers) {
        User user = this.getUserFromHeaders(headers);
        return new Gson().toJson(this.thesisRepository.getById(id, user));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllThesis(@RequestHeader Map<String, String> headers) {
        User user = this.getUserFromHeaders(headers);
        return new Gson().toJson(this.thesisRepository.getAll(user));
    }

    private User getUserFromHeaders(final Map<String, String> headers) {
        String token = headers.get(HEADER_STRING.toLowerCase());
        String username = userService.getUsernameFromJWTToken(token);
        UserEntity userEntity = userService.getUserEntityByUsername(username);
        return userEntity.convertToUser();
    }
}
