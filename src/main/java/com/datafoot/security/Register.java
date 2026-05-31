package com.datafoot.security;

import com.datafoot.exception.entitexception.BadCredentialException;
import com.datafoot.exception.entitexception.UserNotFoundException;
import com.datafoot.security.dto.LoginRequest;
import com.datafoot.security.dto.RegisterRequest;
import com.datafoot.security.dto.UserResponse;
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
    public UserResponse register (@RequestBody RegisterRequest request){
        User user = new User();
        user.setUserName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        UserResponse response = new UserResponse();
        response.setId(saved.getId());
        response.setUserName(saved.getUserName());
        return response;

    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest user) {

        User userFromDb = userRepository.findByUserName(user.getUserName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (passwordEncoder.matches(user.getPassword(), userFromDb.getPassword())) {

            return jwtService.generateToken(user.getUserName());
        }

        throw new BadCredentialException("Mot de passe incorrect");
    }
}
