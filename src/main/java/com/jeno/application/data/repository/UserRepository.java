package com.jeno.application.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeno.application.data.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByProviderId(String providerId);

	Optional<User> findByName(String name);

	Optional<User> findByEmail(String email);

	Optional<User> findByNameOrEmail(String name, String email);

}
