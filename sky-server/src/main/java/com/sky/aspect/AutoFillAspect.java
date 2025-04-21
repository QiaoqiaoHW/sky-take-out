package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        //获取切入点方法
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //获取方法上的注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获取注解上操作类型
        OperationType operationType = autoFill.value();

        //获取切入点方法参数
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        //获取实体对象
        Object entity = args[0];

        //准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();


        //根据操作类型来填充值
        if (operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }catch (Exception ex){
                log.error("public void autoFill(JoinPoint joinPoint) - 反射方法异常", ex.getMessage());
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }catch (Exception ex){
                log.error("public void autoFill(JoinPoint joinPoint) - 反射方法异常", ex.getMessage());
            }

        }
    }
}
