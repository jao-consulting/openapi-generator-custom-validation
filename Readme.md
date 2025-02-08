# Custom Validation with OpenAPI Generator and Spring Boot 3

## Introduction

[OpenAPI Generator](https://openapi-generator.tech/) is a powerful tool that automates the creation of API client libraries, server stubs, and API documentation from an OpenAPI Specification. When working with Spring Boot, it significantly accelerates development by reducing boilerplate code.

However, handling custom validation remains a common challenge. In this guide, we will walk through the process of adding custom validation to a Spring Boot project generated using OpenAPI Generator. We'll start with basic Jakarta validation annotations and then implement our own custom annotation.

### Prerequisites
- Familiarity with Java and Spring Boot.
- Basic understanding of OpenAPI specifications.
- OpenAPI Generator installed.

_Note:_ This blog does not cover setting up OpenAPI Generator, as there are existing resources on that topic (including one I wrote previously).

---

## Step 1: Implementing Mandatory Fields

Mandatory fields are a fundamental aspect of API validation. OpenAPI Generator makes this easy to enforce through the `required` property in an OpenAPI specification.

### OpenAPI Specification
Below is an example OpenAPI specification defining a `PersonDTO` object with required fields:

```yaml
openapi: "3.0.3"
info:
  description: "This project provides endpoints for creating persons"
  version: "@project.version@"
  title: "Person API"

paths:
  /person:
    post:
      summary: "Create a person with necessary info"
      operationId: "createPerson"
      tags:
        - "Person"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PersonDTO'
      responses:
        "200":
          description: "The Person"
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/PersonDTO"

components:
  schemas:
    PersonDTO:
      type: object
      required: [name, birthDate, has_driver_license]
      properties:
        name:
          type: string
        birthDate:
          type: string
          format: date
        has_driver_license:
          type: boolean
```

### Generated Java Class
The generated `PersonDTO` class will look like this:

```java
public class PersonDTO {
    @NotNull
    private String name;
    
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    
    @NotNull
    private Boolean hasDriverLicense;
}
```

At this point, while `@NotNull` ensures the fields are not `null`, we still need to prevent empty strings.

---

## Step 2: Adding Jakarta Validation

To ensure that the `name` field is not just non-null but also non-empty, we enhance our OpenAPI specification by adding `x-field-extra-annotation`:

```yaml
PersonDTO:
  type: object
  required: [name, birthDate, has_driver_license]
  properties:
    name:
      type: string
      x-field-extra-annotation: '@NotEmpty'
    birthDate:
      type: string
      format: date
    has_driver_license:
      type: boolean
```

### Updated Generated Java Class

```java
public class PersonDTO {
    @NotEmpty
    private String name;
}
```

This ensures `name` is neither `null` nor an empty string.

---

## Step 3: Implementing a Custom Validator

While Jakarta validation covers many cases, we sometimes need business-specific rules. Here, we create a custom validator to ensure `birthDate` is in the past.

### Custom Validator Annotation

```java
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthDateValidator.class)
public @interface BirthDateInThePast {
    String message() default "Birth date must be in the past";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class BirthDateValidator implements ConstraintValidator<BirthDateInThePast, LocalDate> {
    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        return birthDate == null || birthDate.isBefore(LocalDate.now());
    }
}
```

### OpenAPI Specification Update

```yaml
PersonDTO:
  type: object
  required: [name, birthDate, has_driver_license]
  properties:
    birthDate:
      x-field-extra-annotation: '@BirthDateInThePast'
```

### Updating OpenAPI Generator Template
To avoid import errors, modify the OpenAPI Generator template to include:

```java
import com.jao.openapi.validation.rest.controller.validation.*;
```

---

## Step 4: Object-Level Custom Validation

Sometimes, validation rules involve multiple fields. For example, a person under 18 should not have a driver’s license.

### Custom Class-Level Validator

```java
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DriversLicenseAgeValidator.class)
public @interface DriverAgeValidation {
    String message() default "Cannot have a driver's license as the person is not an adult";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class DriversLicenseAgeValidator implements ConstraintValidator<DriverAgeValidation, PersonDTO> {
    @Override
    public boolean isValid(PersonDTO person, ConstraintValidatorContext context) {
        if (person.getBirthDate() == null || person.getHasDriverLicense() == null) {
            return true;
        }
        int age = Period.between(person.getBirthDate(), LocalDate.now()).getYears();
        return age >= 18 || !person.getHasDriverLicense();
    }
}
```

### OpenAPI Specification Update

```yaml
PersonDTO:
  type: object
  x-class-extra-annotation: '@DriverAgeValidation'
```

### Updated Generated Java Class

```java
@DriverAgeValidation
public class PersonDTO {
    @NotEmpty
    private String name;

    @BirthDateInThePast
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private Boolean hasDriverLicense;
}
```

---

## Conclusion

In this blog, we explored different levels of validation in a Spring Boot 3 project generated using OpenAPI Generator:

- **Basic validation**: Using OpenAPI’s `required` property.
- **Field-level validation**: Adding Jakarta annotations via `x-field-extra-annotation`.
- **Custom field validation**: Implementing `@BirthDateInThePast`.
- **Object-level validation**: Enforcing rules across multiple fields with `@DriverAgeValidation`.
- **Modifying OpenAPI Generator templates** to include necessary imports.

By following these patterns, you can enforce robust validation while leveraging the benefits of OpenAPI code generation.

**The full code is available on GitHub.** For further details, check the [OpenAPI Generator documentation](https://openapi-generator.tech/docs/).

Happy coding! 🚀

