package pl.poznan.put.thesisapi.user;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.poznan.put.thesisapi.entities.UserDto;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;


import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class UserService implements IUserService, UserDetailsService {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
            final UserRepository userRepository,
            final BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }

    public String loginUser(UserDto userDto) {
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        return "jwt";
    }

    public List<UserEntity> list() {
        return userRepository.findAll();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}