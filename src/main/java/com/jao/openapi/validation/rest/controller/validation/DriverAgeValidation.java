package com.jao.openapi.validation.rest.controller.validation;

import com.jao.openapi.validation.rest.dto.PersonDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DriverAgeValidation.DriversLicenseAgeValidator.class)
public @interface DriverAgeValidation {
	String message() default "Cannot have driver's license as the person is not an adult";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	class DriversLicenseAgeValidator implements
			ConstraintValidator<DriverAgeValidation, PersonDTO> {

		@Override
		public boolean isValid(PersonDTO personDTO, ConstraintValidatorContext constraintValidatorContext) {

			if (personDTO.getBirthDate() == null || personDTO.getHasDriverLicense() == null) {
				return true;
			}
			int age = Period.between(personDTO.getBirthDate(), LocalDate.now()).getYears();
			return age >= 18 || !personDTO.getHasDriverLicense();
		}
	}
}
