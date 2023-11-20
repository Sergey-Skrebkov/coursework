package ru.coursework.documents.exception;

/**
 * Ошибка при попытке повторной отправки документа
 */
public class DocumentApprovedException extends RuntimeException{
    public DocumentApprovedException(String message){
        super(message);
    }
}
