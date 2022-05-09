package com.jeno.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme(value = "hilla-crm")
@PWA(name = "hilla-crm", shortName = "CRM", offlineResources = {"images/logo.png"})
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
