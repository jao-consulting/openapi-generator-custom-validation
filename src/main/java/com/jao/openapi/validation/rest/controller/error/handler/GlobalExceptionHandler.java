package com.jao.openapi.validation.rest.controller.error.handler;

import com.jao.openapi.validation.rest.controller.error.handler.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		String fieldErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList().toString();

		String objectErrors = ex.getBindingResult().getAllErrors().stream().
				map(objectError -> objectError.getObjectName() + ": " + objectError.getDefaultMessage())
				.toList().toString();
		return new ErrorResponse(
				"Validation Failed",
				fieldErrors.concat(objectErrors)
		);
	}

}
