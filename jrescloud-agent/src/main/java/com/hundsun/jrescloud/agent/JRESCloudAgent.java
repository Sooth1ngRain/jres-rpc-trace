package com.hundsun.jrescloud.agent;

import com.hundsun.jrescloud.agent.interceptor.jres.JresInvokeTimeRecordInterceptor;
import com.hundsun.jrescloud.agent.interceptor.jres.TraceProviderInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class JRESCloudAgent {

    private static Properties AGENT_SETTINGS;
    private static final String DEFAULT_CONFIG_FILE_NAME = "/config/agent.config";

    private static final Logger logger = LoggerFactory.getLogger(JresInvokeTimeRecordInterceptor.class);


    public static void premain(String agentArgs, Instrumentation inst) {
        initializeConfig(agentArgs);
        agentBuilder("invoke", "com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker", inst);
    }

    private static void agentBuilder(String methodName, String className, Instrumentation inst) {
        AgentBuilder.Transformer transformer =
                (builder, typeDescription, classLoader, module) ->
                        builder.method(ElementMatchers.named(methodName)).intercept(MethodDelegation.to(JresInvokeTimeRecordInterceptor.class));
        new AgentBuilder.Default().type(ElementMatchers.named(className)).transform(transformer).installOn(inst);
    }

    private static void initializeConfig(String agentArgs) {
        AGENT_SETTINGS = new Properties();
        try (final InputStreamReader configFileStream = loadConfig()) {
            AGENT_SETTINGS.load(configFileStream);

        } catch (Exception e) {
            logger.error("Failed to read the config file, skywalking is going to run in default config.", e);
        }
    }

    private static InputStreamReader loadConfig() throws AgentPackageNotFoundException, ConfigNotFoundException {
        File configFile = new File(
                AgentPackagePath.getPath(), DEFAULT_CONFIG_FILE_NAME);
        if (configFile.exists() && configFile.isFile()) {
            try {
                logger.info("Config file found in {}.", configFile);

                return new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                throw new ConfigNotFoundException("Failed to load agent.config", e);
            }
        }
        throw new ConfigNotFoundException("Failed to load agent.config.");
    }
}
