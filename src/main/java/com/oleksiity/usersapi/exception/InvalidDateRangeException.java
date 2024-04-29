package com.oleksiity.usersapi.exception;

public class InvalidDateRangeException extends RuntimeException {


    public InvalidDateRangeException(String message) {
        super(message);
    }

    public InvalidDateRangeException() {
        super("users-api.users.errors.date_range_not_valid");
    }
}
