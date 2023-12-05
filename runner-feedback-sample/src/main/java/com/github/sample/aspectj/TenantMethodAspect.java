package com.github.sample.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TenantMethodAspect {


    @Around("execution(* com.github.sample.service.*.*(..))")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        // start stopwatch   相当于是before advice
        System.out.println("aop before");
        Object retVal = pjp.proceed();
        System.out.println("aop after");
        // stop stopwatch    相当于是after advice

        return retVal;

    }
}
