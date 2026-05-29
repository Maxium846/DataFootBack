package com.dataFoot.ProjetData.security;

import com.dataFoot.ProjetData.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User>findByUserName(String userName);
}
