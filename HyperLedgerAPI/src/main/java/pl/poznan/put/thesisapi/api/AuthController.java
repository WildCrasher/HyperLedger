package pl.poznan.put.thesisapi.api;

import org.springframework.web.bind.annotation.*;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;
import pl.poznan.put.thesisapi.user.UserDto;
import pl.poznan.put.thesisapi.user.UserService;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController()
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    public AuthController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            User registered = userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            return uaeEx.getMessage();
        }
        return "Success";
    }
}
