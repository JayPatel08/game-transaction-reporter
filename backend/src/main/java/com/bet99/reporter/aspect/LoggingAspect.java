package com.bet99.reporter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final long SLOW_QUERY_THRESHOLD_MS = 300;

    @Pointcut("execution(* com.bet99.reporter.controller..*.*(..)) || execution(* com.bet99.reporter.service..*.*(..))")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logExecutionTimeAndAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("==> Entering [{}.{}] with arguments: {}", className, methodName, Arrays.toString(args));
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > SLOW_QUERY_THRESHOLD_MS) {
                log.warn("<== SLOW EXECUTION [{}.{}] took {} ms (Threshold: {} ms)", className, methodName, duration, SLOW_QUERY_THRESHOLD_MS);
            } else {
                log.info("<== Exited [{}.{}] in {} ms", className, methodName, duration);
            }

            return result;
        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("XXX Exception in [{}.{}] after {} ms. Cause: {}", className, methodName, duration, throwable.getMessage(), throwable);
            throw throwable;
        }
    }
}
