package com.gubkina.authorization.controllers;

import com.gubkina.authorization.exeptions.InvalidCredentials;
import com.gubkina.authorization.exeptions.UnauthorizedUser;
import com.gubkina.authorization.models.Authorities;
import com.gubkina.authorization.models.User;
import com.gubkina.authorization.services.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class AuthorizationController {

    AuthorizationService service;

    @GetMapping("/authorize")
    public List<Authorities> getAuthorities(@Valid User user) {
        return service.getAuthorities(user);
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentials ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedUser.class)
    public String handleUnauthorizedUser(UnauthorizedUser ex) {
        return ex.getMessage();
    }

    /**
     * обработчик ситуации с непопаданием в ограничения валидации.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constrainValidationHandler(ConstraintViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getLocalizedMessage());
    }
}