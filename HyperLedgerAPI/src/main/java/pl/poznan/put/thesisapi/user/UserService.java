package pl.poznan.put.thesisapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;

@Service
public class UserService implements IUserService {

    private UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerNewUserAccount(UserDto userDto)
            throws UserAlreadyExistException {

        if (usernameExists(userDto.getName())) {
            throw new UserAlreadyExistException(
                    "There is an account with that username: "
                            + userDto.getName());
        }
        User user = new User();


        return this.userRepository.save(user);
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}