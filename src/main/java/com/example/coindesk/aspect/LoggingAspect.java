package com.example.coindesk.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.example.coindesk.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("START {} with args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.example.coindesk.controller..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("SUCCESS {} return: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.coindesk.controller..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("ERROR {} throw exception: {}", joinPoint.getSignature(), ex.getMessage(), ex);
    }
}
