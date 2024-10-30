package com.example.webgrow.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Integer> {

    Optional<Host> findByEmail(String email);
}
