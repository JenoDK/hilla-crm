package com.example.application.data.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.PermitAll;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.example.application.data.repository.CompanyRepository;
import com.example.application.data.repository.ContactRepository;
import com.example.application.data.repository.StatusRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

@Endpoint
@PermitAll
public class CrmEndpoint {

	private final CompanyRepository companyRepository;
	private final ContactRepository contactRepository;
	private final StatusRepository statusRepository;

	public CrmEndpoint(
			CompanyRepository companyRepository,
			ContactRepository contactRepository,
			StatusRepository statusRepository) {
		this.companyRepository = companyRepository;
		this.contactRepository = contactRepository;
		this.statusRepository = statusRepository;
	}

	@Nonnull
	public CrmData getCrmData() {
		CrmData crmData = new CrmData();
		crmData.contacts = contactRepository.findAll();
		crmData.companies = companyRepository.findAll();
		crmData.statuses = statusRepository.findAll();
		return crmData;
	}

	@Nonnull
	public Contact saveContact(Contact contact) {
		contact.setCompany(companyRepository.findById(contact.getCompany().getId())
				.orElseThrow(() -> new RuntimeException(
						"Could not find Company with ID " + contact.getCompany().getId())));
		contact.setStatus(statusRepository.findById(contact.getStatus().getId())
				.orElseThrow(() -> new RuntimeException(
						"Could not find Status with ID " + contact.getStatus().getId())));
		return contactRepository.save(contact);
	}

	public void deleteContact(UUID contactId) {
		contactRepository.deleteById(contactId);
	}

	public static class CrmData {
		@Nonnull
		public List<@Nonnull Contact> contacts = Collections.emptyList();
		@Nonnull
		public List<@Nonnull Company> companies = Collections.emptyList();
		@Nonnull
		public List<@Nonnull Status> statuses = Collections.emptyList();
	}
}
