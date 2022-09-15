package com.hundsun.jrescloud.agent.interceptor.jres;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

public class TraceProviderInterceptor {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @RuntimeType
    public Object intercept(@AllArguments Object[] args, @SuperCall Callable<?> callable, @Origin Method method) throws Exception {
        System.out.println("=========trace execute begin at " + formatter.format(LocalDateTime.now()) + "=========");
        long startNano = System.nanoTime();
        Object result = callable.call();
        System.out.println("=========trace execute end at " + formatter.format(LocalDateTime.now()) + ", spend " + (System.nanoTime() - startNano) / 1000L + "us=========");
        return result;
    }
}
