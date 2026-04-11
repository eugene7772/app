package social.network.dialog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import social.network.dialog.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import social.network.dialog.exception.FindDialogException;
import social.network.dialog.exception.FindMessagesException;
import social.network.dialog.exception.SaveDialogException;
import social.network.dialog.exception.SaveMessageException;

import static social.network.dialog.constants.ErrorConstants.*;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FindDialogException.class)
    public ResponseEntity<ErrorResponse> handle(FindDialogException e) {
        ErrorResponse body = new ErrorResponse(DIALOG_NOT_FOUND, List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(FindMessagesException.class)
    public ResponseEntity<ErrorResponse> handle(FindMessagesException e) {
        ErrorResponse body = new ErrorResponse(MESSAGE_NOT_FOUND, List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(SaveDialogException.class)
    public ResponseEntity<ErrorResponse> handle(SaveDialogException e) {
        ErrorResponse body = new ErrorResponse(SAVE_DIALOG_ERROR, List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(SaveMessageException.class)
    public ResponseEntity<ErrorResponse> handle(SaveMessageException e) {
        ErrorResponse body = new ErrorResponse(SAVE_MESSAGE_ERROR, List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
