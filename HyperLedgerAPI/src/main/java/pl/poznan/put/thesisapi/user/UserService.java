package pl.poznan.put.thesisapi.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.poznan.put.thesisapi.entities.UserDto;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;


import java.util.List;

import static java.util.Collections.emptyList;
import static pl.poznan.put.thesisapi.security.SecurityConstants.SECRET;
import static pl.poznan.put.thesisapi.security.SecurityConstants.TOKEN_PREFIX;

@Service
public class UserService implements IUserService, UserDetailsService {

    private UserRepository userRepository;

    public UserService(
            final UserRepository userRepository
    ) {
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

        pl.poznan.put.thesisapi.user.User user = userEntity.convertToUser();
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

    public UserEntity getUserEntityByUsername(String username) throws UsernameNotFoundException {
        UserEntity applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return applicationUser;
    }

    public String getUsernameFromJWTToken(String token) {
        return JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }

    public List<UserEntity> list() {
        return userRepository.findAll();
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}