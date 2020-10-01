package pl.poznan.put.thesisapi.user;

import pl.poznan.put.thesisapi.exceptions.UserAlreadyExistException;

public interface IUserService {
    User registerNewUserAccount(UserDto userDto)
            throws UserAlreadyExistException;
}
