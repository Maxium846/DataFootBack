package com.dataFoot.ProjetData.security;

import com.dataFoot.ProjetData.model.User;
import com.dataFoot.ProjetData.repository.UserRepository;
import com.dataFoot.ProjetData.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Register {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    public Register(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User register (@RequestBody User user){

        user.setUserName(user.getUserName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);

    }
    @PostMapping("/login")
    public String login(@RequestBody User user) {

        User userFromDb = userRepository.findByUserName(user.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(user.getPassword(), userFromDb.getPassword())) {

            return jwtService.generateToken(user.getUserName());
        }

        throw new RuntimeException("Bad credentials");
    }
}
