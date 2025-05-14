package com.example.financelloapi.exception;

public class RoleDoesntExistException extends RuntimeException {
    public RoleDoesntExistException(Integer role_id) {
        super("Role with " + role_id + " does not exist");
    }
}
