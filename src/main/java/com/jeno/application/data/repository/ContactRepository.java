package com.jeno.application.data.repository;

import com.jeno.application.data.entity.Contact;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

}
