package ru.coursework.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Аспект для ведения логов
 */
@Component
@Aspect
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    /**
     * Точка соединения для контроллеров
     */
    @Pointcut("within(ru.coursework.documents.controller.*)")
    public void controllerPointcut() {
    }

    /**
     * Точка соединения для периодически исполняемых функций
     */
    @Pointcut("within(ru.coursework.documents.schedule.*)")
    public void schedulePointcut() {
    }

    /**
     * Запись в лог после исполнения функции в контроллере
     *
     * @param joinPoint точка наблюдения контроллера
     */
    @After("controllerPointcut()")
    public void logInfoMethodCall(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.log(Level.INFO, "Пришел запрос " + name + "\nАргументы в запросе " + List.of(args));
    }

    /**
     * Запись в лог после исполнения периодической функции
     *
     * @param joinPoint точка наблюдения над функцией в расписании
     */
    @After("schedulePointcut()")
    public void logInfoAboutScheduleMethodsCall(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        logger.log(Level.INFO, "Исполняется метод " + name +
                " в " + Instant.now());
    }
}
