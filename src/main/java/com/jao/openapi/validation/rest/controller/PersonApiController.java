package com.jao.openapi.validation.rest.controller;

import com.jao.openapi.validation.rest.dto.PersonDTO;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonApiController implements PersonApi {

	@Override
	public PersonDTO createPerson(PersonDTO personDTO) {
		return personDTO;
	}
}
