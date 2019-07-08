package ru.mediaserver.clients.msmobile.business.auth;

import ru.mediaserver.clients.msmobile.business.auth.model.User;
import ru.mediaserver.clients.msmobile.business.auth.repository.AuthRepository;

import java.io.IOException;

public class Test {
    private AuthRepository repository = new AuthRepository();

    public static void main(String[] args) throws IOException {
        Test test = new Test();

        User user = new User();
        user.setUsername("rock64");
        user.setPassword("1234");

        User register = test.repository.register(user);
        System.out.println(user.getUsername());
    }
}
