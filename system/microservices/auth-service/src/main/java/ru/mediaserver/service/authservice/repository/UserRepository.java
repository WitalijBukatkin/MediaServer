package ru.mediaserver.service.authservice.repository;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mediaserver.service.authservice.model.User;


public interface UserRepository extends Repository<User, Long>{
    User getUserByUsername(String name);

    @Transactional
    User save(User user);
}
