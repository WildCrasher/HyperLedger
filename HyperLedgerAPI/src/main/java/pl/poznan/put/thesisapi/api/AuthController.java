package pl.poznan.put.thesisapi.api;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;
import pl.poznan.put.thesisapi.entities.UserDto;
import pl.poznan.put.thesisapi.user.UserService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController()
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthController(
            final UserService userService,
            final BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            UserEntity registered = userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            return uaeEx.getMessage();
        }
        return new Gson().toJson("success");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String testDb() {
        List<UserEntity> result = userService.list();
        return new Gson().toJson(result);
    }
}
