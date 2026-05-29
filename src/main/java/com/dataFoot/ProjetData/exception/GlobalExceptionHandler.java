package com.dataFoot.ProjetData.exception;

import com.dataFoot.ProjetData.exception.entitexception.ErrorResponse;
import com.dataFoot.ProjetData.exception.entitexception.ExternalApiException;
import com.dataFoot.ProjetData.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.ProjetData.exception.entitexception.TeamNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LeagueNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLeagueNotFound(LeagueNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage(), 404));

    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse>handleTeamsNotFound(TeamNotFoundException ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage(),404));
    }

    public ResponseEntity<ErrorResponse> handleApiException(ExternalApiException ex){
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(ex.getMessage(),502));
    }

}
