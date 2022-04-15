package com.jeno.application.data.repository;

import com.jeno.application.data.entity.Company;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

}
