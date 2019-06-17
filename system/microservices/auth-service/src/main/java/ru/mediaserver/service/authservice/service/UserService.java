package ru.mediaserver.service.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mediaserver.service.authservice.model.User;
import ru.mediaserver.service.authservice.repository.UserRepository;

import java.io.IOException;

@Component
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private Runtime runtime = Runtime.getRuntime();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUserName(username);
    }

    public User register(User user){
        User existing = userRepository.getUserByUserName(user.getUsername());

        if(existing!=null) {
            throw new IllegalArgumentException("user already exists: " + existing.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            runtime.exec("user-conf create "
                    + user.getUsername() + " "
                    + user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userRepository.save(user);
    }
}
