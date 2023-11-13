package ru.template.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Aspect
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Pointcut("within(ru.template.example.documents.controller.*)")
    public void pointcut() {
    }

    @Pointcut("within(ru.template.example.documents.schedule.*)")
    public void schedulePointcut() {
    }

    @After("pointcut()")
    public void logInfoMethodCall(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        logger.log(Level.INFO, "Аргументы метода " + List.of(args));
    }

    @After("schedulePointcut()")
    public void logInfoAboutScheduleMethodsCall(JoinPoint joinPoint) {
        String kind = joinPoint.getKind();
        logger.log(Level.INFO, "Исполняется метод " + kind +
                " в " + Instant.now());
    }
}
