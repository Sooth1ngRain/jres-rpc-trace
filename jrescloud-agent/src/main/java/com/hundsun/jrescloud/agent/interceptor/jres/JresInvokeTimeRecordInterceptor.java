package com.hundsun.jrescloud.agent.interceptor.jres;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.hundsun.jrescloud.rpc.api.IRpcContext;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: chencw
 * @date: 2022-09-09 15:37
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */

public class JresInvokeTimeRecordInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JresInvokeTimeRecordInterceptor.class);

    private static String template = "======${threadName} ${traceId} ${functionId} ${requestId}  begin ===========\n" +
            "${methodName} begin, record time ${beginTime}, execute time from last record ${cost} us\n" +
            "arguments: ${arguments} " +
            "${methodName} end, record time ${endTime}, execute time from last record ${cost} us\n" +
            "======${threadName} ${traceId} ${functionId} end for ${costTime}us=========";


    @RuntimeType
    public static Object intercept(@AllArguments Object[] args, @This Invoker invoker, @SuperCall Callable<Result> callable) throws Exception {
        StopWatch traceSW = new StopWatch();
        StopWatch functionSW = new StopWatch();

        traceSW.start();

        Map<String, String> logParams = new HashMap<>();

        Invocation invocation = (Invocation) args[0];
        IRpcContext rpcContext = com.hundsun.jrescloud.common.util.SpringUtils.getBean(com.hundsun.jrescloud.rpc.api.IRpcContext.class);


        long startNano = System.nanoTime();
        Result result = null;
        Long costTime = null;
        boolean containException = false;
        Exception e = null;

        logParams.put("threadName", Thread.currentThread().getName());
        logParams.put("traceId", getTraceId(invocation));
        logParams.put("functionId", getFunctionId(invocation));
        logParams.put("requestId", getRequestId(invocation));
        logParams.put("methodName", invocation.getMethodName());
        logParams.put("arguments", collectArguments(1000, invocation.getArguments()));
        logParams.put("beginTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

        try {
            traceSW.suspend();
            functionSW.start();
            result = callable.call();

        } catch (Exception ex) {
            containException = true;
            e = ex;
        } finally {
            functionSW.stop();
            logParams.put("endTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        traceSW.resume();
        if(result!=null){
            logParams.put("returnResult", collectArguments(1000, result.getValue()));
            if (result.hasException()) {
                logParams.put("exception", collectArguments(1000, result.getException().getLocalizedMessage()));
            }
        }

        traceSW.stop();
        logParams.put("costTime", String.valueOf(functionSW.getNanoTime()));
        String buildMessage = buildMessage(template, logParams);

        Matcher m = Pattern.compile("\\$\\{([^}]+)}").matcher(buildMessage);
        while(m.find()) {
            String jexlStr = m.group(1);

            JexlEngine jexl = new JexlBuilder().create();
            JexlExpression jexlExpression = jexl.createExpression(jexlStr);

            //创建上下文并添加数据
            JexlContext jc = new MapContext();
            jc.set("rpcContext", rpcContext);
            jc.set("params", invocation.getArguments());

            //现在评估表达式，得到结果，结果为最后一个表达式的运算值
            Object val = jexlExpression.evaluate(jc);
            logParams.put(m.group(1) , JSON.toJSONString(val));
        }

        buildMessage = buildMessage(buildMessage , logParams);

        logger.info(buildMessage);
        logger.info(String.format("trace interceptor cost time %d μs", traceSW.getNanoTime()));
        if (containException) {
            throw e;
        } else {
            return result;
        }
    }

    /**
     * 以json的形式收集参数，如果参数长度超过阈值则将超出的部分省略
     *
     * @param argumentsLengthThreshold 参数长度阈值
     * @param params                   参数
     * @return
     */
    private static String collectArguments(int argumentsLengthThreshold, Object... params) {
        String parameters = JSON.toJSONString(params);
        if (parameters.length() > argumentsLengthThreshold) {
            return StringUtils.left(parameters, argumentsLengthThreshold) + "...";
        } else {
            return parameters;
        }
    }

    private static String buildMessage(String template, Map<String, String> params) {
        StringSubstitutor sub = new StringSubstitutor(params);
        return sub.replace(template);
    }


    private static String getTraceId(Invocation invocation) {
        return invocation.getAttachment("trace.traceId");
    }

    private static String getFunctionId(Invocation invocation) {
        return invocation.getAttachment("invoke.functionId");
    }

    private static String getRequestId(Invocation invocation) {
        return String.valueOf(invocation.getRequestId());
    }

}
