package com.hundsun.jrescloud.agent;

/**
 * @Description:
 * @Author: chencw
 * @date: 2022-09-14 15:06
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */
public class ConfigNotFoundException extends Exception {
    public ConfigNotFoundException(String message) {
        super(message);
    }

    public ConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
