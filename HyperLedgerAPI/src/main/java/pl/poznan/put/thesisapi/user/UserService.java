package pl.poznan.put.thesisapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.thesisapi.entities.UserEntity;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> list() {
        return userRepository.findAll();
    }
}
