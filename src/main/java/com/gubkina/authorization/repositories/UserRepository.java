package com.gubkina.authorization.repositories;

import com.gubkina.authorization.models.Authorities;
import com.gubkina.authorization.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserRepository {

    List<User> userRepository = new ArrayList<>();
    {
        userRepository.add(new User("anna", "55555"));
        userRepository.add(new User("stepan","111999"));
        userRepository.add(new User("IlonMask","tesla"));
    }

    public List<Authorities> getUserAuthorities(String login, String password) {
        for (User user : userRepository) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return Arrays.asList(Authorities.values());
            }
        }
        return new ArrayList<>();
    }
}
