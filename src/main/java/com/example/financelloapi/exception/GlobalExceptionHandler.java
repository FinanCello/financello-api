package com.example.financelloapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(ex.getMessage());
        pd.setDetail("Usuario duplicado");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(CustomException.class)
    public ProblemDetail handleCustomException(CustomException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Error personalizado");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFoundException(CategoryNotFoundException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Categoría no encontrada");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String firstErrorMessage = fieldErrors.stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Error de validación");

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(firstErrorMessage);
        pd.setDetail("Error de validación");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(UserDoesntExistException.class)
    public ProblemDetail handleUserDoesntExistException(UserDoesntExistException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(ex.getMessage());
        pd.setDetail("Usuario no existe");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(RoleDoesntExistException.class)
    public ProblemDetail handleRoleDoesntExistException(RoleDoesntExistException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(ex.getMessage());
        pd.setDetail("Rol no existe");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(EmptyException.class)
    public ProblemDetail handleEmptyException(EmptyException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(ex.getMessage());
        pd.setDetail("Contenido vacío");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(IncorrectDateException.class)
    public ProblemDetail handleIncorrectDateException(IncorrectDateException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Fecha incorrecta");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(TargetAmountLessThanCurrentAmountException.class)
    public ProblemDetail handleTargetAmountException(TargetAmountLessThanCurrentAmountException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Cantidad objetivo inválida");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(GoalContributionNotFoundException.class)
    public ProblemDetail handleGoalContributionNotFoundException(GoalContributionNotFoundException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle(ex.getMessage());
        pd.setDetail("Contribución a meta no encontrada");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle(ex.getMessage());
        pd.setDetail("Usuario no encontrado");
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Error interno");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Recurso duplicado");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Recurso no encontrado");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getDescription(false));
        return pd;
    }
}