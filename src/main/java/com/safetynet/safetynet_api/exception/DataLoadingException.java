package com.safetynet.safetynet_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class DataLoadingException extends RuntimeException {
    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    //Création d'un gestionnaire global d'erreurs
    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(DataLoadingException.class)
        public ResponseEntity<String> handleDataLoadingException(DataLoadingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : impossible de charger les données.");
        }
    }
}

