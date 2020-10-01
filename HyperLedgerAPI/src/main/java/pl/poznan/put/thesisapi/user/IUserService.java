package pl.poznan.put.thesisapi.user;

import pl.poznan.put.thesisapi.entities.UserDto;
import pl.poznan.put.thesisapi.entities.UserEntity;
import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;

public interface IUserService {
    UserEntity registerNewUserAccount(UserDto userDto)
            throws UserAlreadyExistException;
}
