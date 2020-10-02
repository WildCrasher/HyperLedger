package pl.poznan.put.thesisapi.user;

import org.springframework.stereotype.Service;
import pl.poznan.put.thesisapi.entities.UserDto;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;

import java.util.List;

@Service
public class UserService implements IUserService {

    private UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity registerNewUserAccount(UserDto userDto)
            throws UserAlreadyExistException {

        if (usernameExists(userDto.getName())) {
            throw new UserAlreadyExistException(
                    "There is an account with that username: "
                            + userDto.getName());
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getName());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setRole(userDto.getRole());

        User user = userEntity.convertToUser();
        System.out.println();
        user.register();

        return this.userRepository.save(userEntity);
    }

    public List<UserEntity> list() {
        return userRepository.findAll();
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}