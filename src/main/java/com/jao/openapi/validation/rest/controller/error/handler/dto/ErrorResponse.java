package com.jao.openapi.validation.rest.controller.error.handler.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
	private final LocalDateTime timestamp;
	private String message;
	private String details;

	public ErrorResponse(String message, String details) {
		this.timestamp = LocalDateTime.now();
		this.message = message;
		this.details = details;
	}

	// Getters and setters
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}
}
