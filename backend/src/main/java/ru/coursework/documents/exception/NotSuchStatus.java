package ru.coursework.documents.exception;

/**
 * Статус не найден в базе данных
 */
public class NotSuchStatus extends RuntimeException{
    public NotSuchStatus(String message){
        super(message);
    }
}
