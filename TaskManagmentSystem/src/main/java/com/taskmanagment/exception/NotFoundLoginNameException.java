package com.taskmanagment.exception;

public class NotFoundLoginNameException extends NotFoundException {
    public NotFoundLoginNameException(String login) {
        super(String.format("User with this Login Name: %s NOT FOUND", login));
    }
}
