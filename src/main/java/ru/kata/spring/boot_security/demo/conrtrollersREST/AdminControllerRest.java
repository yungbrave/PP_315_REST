package ru.kata.spring.boot_security.demo.conrtrollersREST;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserErrorResponse;
import ru.kata.spring.boot_security.demo.util.UserNotCreatedException;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;
import ru.kata.spring.boot_security.demo.util.UserNotUpdatedException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rest/api/users/admin")
@CrossOrigin(origins = "*")
public class AdminControllerRest {
    private final UserService userService;


    @Autowired
    public AdminControllerRest(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        return new ResponseEntity<>(userService.findOne(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid User user,
                                             BindingResult bindingResult) {
        System.out.println("test");
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
            }
            throw new UserNotCreatedException(errorMessage.toString());
        }
        userService.save(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> edit(@PathVariable("id") long id,
                                           @RequestBody @Valid User toBeUpdatedUser,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
            }
            throw new UserNotUpdatedException(errorMessage.toString());
        }
        userService.update(id, toBeUpdatedUser);
        return new ResponseEntity<>(toBeUpdatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        userService.delete(id);
        return new ResponseEntity<>(String.format("User with id = %d was deleted", id),HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                "User not found",
                System.currentTimeMillis()
        );
        // В HTTP ответе тело ответа (response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // NOT_FOUND - 404
    }


    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        // В HTTP ответе тело ответа (response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // NOT_FOUND - 40*
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotUpdatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        // В HTTP ответе тело ответа (response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // NOT_FOUND - 40*
    }

}
