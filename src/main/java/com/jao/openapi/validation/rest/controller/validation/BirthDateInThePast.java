package com.jao.openapi.validation.rest.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.util.Optional;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthDateInThePast.BirthDateValidator.class)
public @interface BirthDateInThePast {
	String message() default "Invalid birth date";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	class BirthDateValidator implements
			ConstraintValidator<BirthDateInThePast, LocalDate> {

		@Override
		public void initialize(BirthDateInThePast contactNumber) {
		}

		@Override
		public boolean isValid(LocalDate birthDate,
				ConstraintValidatorContext cxt) {
			return Optional.ofNullable(birthDate)
					.map(localDate -> localDate.isBefore(LocalDate.now())).orElse(true);
		}

	}

}
