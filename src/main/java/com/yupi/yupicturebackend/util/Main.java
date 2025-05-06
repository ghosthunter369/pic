package com.yupi.yupicturebackend.util;

import com.alibaba.dashscope.app.*;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.List;

public class Main {
    public static void callWithSession()
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey("")
                // 替换为实际的应用 ID
                .appId("")
                .prompt("我是王五，你好")
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);
        param.setSessionId(result.getOutput().getSessionId());
        param.setSessionId("0b6f53f02ff1415993862424e2c72cd8");
        System.out.println(result.getOutput().getSessionId());
        param.setPrompt("我的名字是啥?");
        result = application.call(param);
        List<Message> messages = param.getMessages();
        System.out.println(messages);
        System.out.printf("%s\n, session_id: %s\n",
                result.getOutput().getText(), result.getOutput().getSessionId());
    }

    public static void main(String[] args) {
        try {
            callWithSession();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.out.printf("Exception: %s", e.getMessage());
            System.out.println("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
        }
        System.exit(0);
    }
}