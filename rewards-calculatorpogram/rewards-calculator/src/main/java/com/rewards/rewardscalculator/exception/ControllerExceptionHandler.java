package com.rewards.rewardscalculator.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RewardsCalculatorAppError> globalExceptionHandler(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        RewardsCalculatorAppError message = new RewardsCalculatorAppError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RewardsValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        RewardsValidationErrorResponse invalidInput = RewardsValidationErrorResponse.builder().errors(errors)
                .message("Invalid input")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(invalidInput);
    }

    @ExceptionHandler(RewardsApplicationBadRequestGenericError.class)
    public ResponseEntity<RewardsCalculatorAppError> handleRewardsValidationGenericError(RewardsApplicationBadRequestGenericError ex) {
        RewardsCalculatorAppError error = new RewardsCalculatorAppError(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                "Invalid request",
                ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
