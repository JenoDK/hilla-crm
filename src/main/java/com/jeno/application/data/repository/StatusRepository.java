package com.jeno.application.data.repository;

import com.jeno.application.data.entity.Status;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, UUID> {

}
