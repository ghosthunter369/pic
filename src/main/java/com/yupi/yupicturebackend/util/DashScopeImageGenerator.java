package com.yupi.yupicturebackend.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;

public class DashScopeImageGenerator {

    // 替换为你的 token
    private static final String TOKEN = "";
    private static final String GEN_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image-generation/generation";
    private static final String TASK_URL_TEMPLATE = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    public static void main(String[] args) throws InterruptedException {
        String faceUrl = "https://n.sinaimg.cn/fashion/crawl/343/w503h640/20200601/96e5-iumkapv9736792.jpg";
        String templateUrl = "https://tse3.mm.bing.net/th/id/OIP.0FQSOvu3OYJFe-h_sZKA6wHaNK?cb=iwp1&rs=1&pid=ImgDetMain";

        String taskId = submitTask(faceUrl, templateUrl);
        pollTaskUntilDone(taskId);
    }

    // 发起任务请求
    private static String submitTask(String faceUrl, String templateUrl) {
        JSONObject input = new JSONObject()
                .set("model_index", 1)
                .set("face_image_url", faceUrl)
                .set("template_image_url", templateUrl);

        JSONObject body = new JSONObject()
                .set("model", "wanx-style-cosplay-v1")
                .set("input", input);

        HttpResponse response = HttpRequest.post(GEN_URL)
                .header("Authorization", "Bearer " + TOKEN)
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .body(body.toString())
                .execute();

        JSONObject json = JSONUtil.parseObj(response.body());
        if (!json.containsKey("output") || !json.getJSONObject("output").containsKey("task_id")) {
            throw new RuntimeException("提交任务失败: " + response.body());
        }

        return json.getJSONObject("output").getStr("task_id");
    }

    // 轮询直到成功或失败
    private static void pollTaskUntilDone(String taskId) throws InterruptedException {
        String url = String.format(TASK_URL_TEMPLATE, taskId);
        int maxRetry = 30;
        int interval = 2000; // 2秒

        for (int i = 0; i < maxRetry; i++) {
            HttpResponse response = HttpRequest.get(url)
                    .header("Authorization", "Bearer " + TOKEN)
                    .execute();

            JSONObject json = JSONUtil.parseObj(response.body());
            JSONObject output = json.getJSONObject("output");
            String status = output.getStr("task_status");

            if ("SUCCEEDED".equalsIgnoreCase(status)) {
                String resultUrl = output.getStr("result_url");
                System.out.println("✅ 成功生成图片： " + resultUrl);
                return;
            } else if ("FAILED".equalsIgnoreCase(status)) {
                throw new RuntimeException("❌ 任务失败: " + output.getStr("message"));
            } else {
                System.out.println("⏳ 任务进行中...第" + (i + 1) + "次");
                Thread.sleep(interval);
            }
        }

        throw new RuntimeException("任务超时未完成！");
    }
}
