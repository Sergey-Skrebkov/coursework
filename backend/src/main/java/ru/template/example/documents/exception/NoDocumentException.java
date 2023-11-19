package ru.template.example.documents.exception;

/**
 * Ошибка если документ не был найден в БД
 */
public class NoDocumentException extends RuntimeException{
    public NoDocumentException(String message){
        super(message);
    }
}
