package ru.template.example.documents.exception;

public class NotSuchStatus extends RuntimeException{
    public NotSuchStatus(String message){
        super(message);
    }
}
