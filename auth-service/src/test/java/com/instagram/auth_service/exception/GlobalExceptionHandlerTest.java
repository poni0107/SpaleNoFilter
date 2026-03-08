package com.instagram.auth_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("handleValidation")
    class HandleValidation {

        @Test
        @DisplayName("Vraća 400 sa mapom validation grešaka")
        void returns400() {

            BindingResult bindingResult = mock(BindingResult.class);

            when(bindingResult.getFieldErrors()).thenReturn(List.of(
                    new FieldError("registerRequest","email","Email mora biti validan"),
                    new FieldError("registerRequest","password","Lozinka mora imati najmanje 6 karaktera")
            ));

            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(null, bindingResult);

            ResponseEntity<Map<String,Object>> response = handler.handleValidation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            Map<String,Object> body = response.getBody();

            assertThat(body).isNotNull();
            assertThat(body.get("status")).isEqualTo(400);
            assertThat(body.get("message")).isEqualTo("Validation failed!");

            @SuppressWarnings("unchecked")
            Map<String,String> errors = (Map<String,String>) body.get("errors");

            assertThat(errors).containsEntry("email","Email mora biti validan");
            assertThat(errors).containsEntry("password","Lozinka mora imati najmanje 6 karaktera");
        }
    }

    @Nested
    @DisplayName("handleBadCredentials")
    class HandleBadCredentials {

        @Test
        @DisplayName("Vraća 401 Unauthorized")
        void returns401() {

            BadCredentialsException ex =
                    new BadCredentialsException("Bad credentials");

            ResponseEntity<Map<String,Object>> response =
                    handler.handleBadCredentials(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

            Map<String,Object> body = response.getBody();

            assertThat(body).isNotNull();
            assertThat(body.get("status")).isEqualTo(401);
            assertThat(body.get("error")).isEqualTo("Unauthorized");
            assertThat(body.get("message"))
                    .isEqualTo("Подаци које сте унели нису исправни!");
        }
    }

    @Nested
    @DisplayName("handleUserExists")
    class HandleUserExists {

        @Test
        @DisplayName("Vraća 409 Conflict kada korisnik već postoji")
        void returns409() {

            UserAlreadyExistsException ex =
                    new UserAlreadyExistsException("User already exists");

            ResponseEntity<Map<String,Object>> response =
                    handler.handleUserExists(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

            Map<String,Object> body = response.getBody();

            assertThat(body).isNotNull();
            assertThat(body.get("status")).isEqualTo(409);
            assertThat(body.get("error")).isEqualTo("Conflict");
            assertThat(body.get("message"))
                    .isEqualTo("User already exists!");
        }
    }

    @Nested
    @DisplayName("handleNotFound")
    class HandleNotFound {

        @Test
        @DisplayName("Vraća 404 kada resurs ne postoji")
        void returns404() {

            ResourceNotFoundException ex =
                    new ResourceNotFoundException("User not found");

            ResponseEntity<Map<String,Object>> response =
                    handler.handleNotFound(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

            Map<String,Object> body = response.getBody();

            assertThat(body).isNotNull();
            assertThat(body.get("status")).isEqualTo(404);
            assertThat(body.get("error")).isEqualTo("Not Found");
            assertThat(body.get("message"))
                    .isEqualTo("User not found!");
        }
    }

    @Nested
    @DisplayName("handleRuntime")
    class HandleRuntime {

        @Test
        @DisplayName("Vraća 500 za RuntimeException")
        void returns500() {

            RuntimeException ex =
                    new RuntimeException("Unexpected error");

            ResponseEntity<Map<String,Object>> response =
                    handler.handleRuntime(ex);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            Map<String,Object> body = response.getBody();

            assertThat(body).isNotNull();
            assertThat(body.get("status")).isEqualTo(500);
            assertThat(body.get("error"))
                    .isEqualTo("Internal Server Error");
            assertThat(body.get("message"))
                    .isEqualTo("Unexpected error");
        }
    }

    @Nested
    @DisplayName("handleGeneral")
    class HandleGeneral {

        @Test
        @DisplayName("Vraća generičku 500 grešku")
        void returns500() {

            Exception ex = new Exception("Any error");

            ResponseEntity<Map<String,Object>> response =
                    handler.handleGeneral(ex);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            Map<String,Object> body = response.getBody();

            assertThat(body).isNotNull();
            assertThat(body.get("status")).isEqualTo(500);
            assertThat(body.get("error"))
                    .isEqualTo("Internal Server Error");
            assertThat(body.get("message"))
                    .isEqualTo("Дошло је до грешке на серверу!");
        }
    }
}
