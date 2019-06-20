package ru.mediaserver.service.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;
import ru.mediaserver.service.authservice.model.User;
import ru.mediaserver.service.authservice.service.UserService;

import java.security.Principal;

@RestController
@EnableResourceServer
@RequestMapping("/oauth")
public class UserController {
	@Autowired
	private UserService userService;

	@GetMapping("/current")
	public Principal getCurrent(Principal principal) {
		return principal;
	}

	@PostMapping("/register")
	public User register(@RequestBody User user){
		return userService.register(user);
	}
}
