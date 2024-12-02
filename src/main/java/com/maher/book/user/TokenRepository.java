package com.maher.book.user;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Integer> {

    Optional<Token> findByToken(String token);
}
