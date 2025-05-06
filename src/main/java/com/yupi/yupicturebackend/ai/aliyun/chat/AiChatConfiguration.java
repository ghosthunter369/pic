package com.yupi.yupicturebackend.ai.aliyun.chat;

import com.alibaba.dashscope.app.ApplicationParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiChatConfiguration {
    @Value("${aliYunAi.apiKey}")
    private String apiKey;
    @Value("${aliYunAi.appId}")
    private String appId;
    @Bean
    public ApplicationParam applicationParam() {
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey(apiKey)
                // 替换为实际的应用 ID
                .appId(appId)
                .prompt("你是一个全能型的AI助手")
                .build();
        return param;
    }
}
