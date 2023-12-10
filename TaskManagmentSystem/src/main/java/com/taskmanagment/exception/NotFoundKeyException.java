package com.taskmanagment.exception;

import java.util.UUID;

public class NotFoundKeyException extends NotFoundException{
    public NotFoundKeyException(UUID key) {
        super(String.format("Key %s does not exist", key));
    }
}
