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

@Component
@Aspect
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Pointcut("within(ru.coursework.documents.controller.*)")
    public void pointcut() {
    }

    @Pointcut("within(ru.coursework.documents.schedule.*)")
    public void schedulePointcut() {
    }

    @After("pointcut()")
    public void logInfoMethodCall(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.log(Level.INFO, "Пришел запрос"+ name +"\nАргументы метода " + List.of(args));
    }

    @After("schedulePointcut()")
    public void logInfoAboutScheduleMethodsCall(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        logger.log(Level.INFO, "Исполняется метод " + name +
                " в " + Instant.now());
    }
}
