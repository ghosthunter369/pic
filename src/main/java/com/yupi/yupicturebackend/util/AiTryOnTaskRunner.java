package com.yupi.yupicturebackend.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class AiTryOnTaskRunner {

    private static final String API_KEY = "";
    private static final String GEN_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/image-synthesis/";
    private static final String TASK_QUERY_TEMPLATE = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    public static void main(String[] args) throws InterruptedException {
        String top = "https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/2389646171/p801332.jpeg";
        String bottom = "https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/1389646171/p801326.jpeg";
        String person = "https://p0.itc.cn/q_70/images03/20211220/fbed50c8917a44fd8521ead71b55fdce.jpeg";

        try {
            String taskId = submitTryOnTask(top, bottom, person);
            pollTaskStatus(taskId);
        } catch (Exception e) {
            System.err.println("❌ 错误：" + e.getMessage());
        }
    }

    // 提交试衣任务
    private static String submitTryOnTask(String topUrl, String bottomUrl, String personUrl) {
        JSONObject input = new JSONObject()
                .set("top_garment_url", topUrl)
                .set("bottom_garment_url", bottomUrl)
                .set("person_image_url", personUrl);

        JSONObject parameters = new JSONObject()
                .set("resolution", -1)
                .set("restore_face", true);

        JSONObject payload = new JSONObject()
                .set("model", "aitryon")
                .set("input", input)
                .set("parameters", parameters);

        HttpResponse response = HttpRequest.post(GEN_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .body(payload.toString())
                .execute();

        System.out.println("📤 提交任务响应：" + response.body());

        JSONObject json = JSONUtil.parseObj(response.body());
        JSONObject output = json.getJSONObject("output");
        if (output == null || !output.containsKey("task_id")) {
            throw new RuntimeException("提交失败，无 task_id 返回: " + response.body());
        }

        String taskId = output.getStr("task_id");
        System.out.println("✅ 获取到任务 ID: " + taskId);
        return taskId;
    }

    // 轮询任务状态，最多 30 次，每 2 秒一次
    private static void pollTaskStatus(String taskId) throws InterruptedException {
        String url = String.format(TASK_QUERY_TEMPLATE, taskId);
        int maxRetry = 30;

        for (int i = 1; i <= maxRetry; i++) {
            HttpResponse response = HttpRequest.get(url)
                    .header("Authorization", "Bearer " + API_KEY)
                    .execute();

            JSONObject json = JSONUtil.parseObj(response.body());
            System.out.printf("⏳ 第 %d 次查询任务状态: %s%n", i, response.body());

            JSONObject output = json.getJSONObject("output");
            if (output == null || !output.containsKey("task_status")) {
                throw new RuntimeException("查询失败，未返回任务状态: " + response.body());
            }

            String status = output.getStr("task_status");

            if ("SUCCEEDED".equalsIgnoreCase(status)) {
                String resultImageUrl = output.getStr("image_url");
                System.out.println("✅ 任务成功，生成图片地址: " + resultImageUrl);
                return;
            } else if ("FAILED".equalsIgnoreCase(status)) {
                String code = output.getStr("code");
                String message = output.getStr("message");
                throw new RuntimeException("❌ 任务失败，原因: [" + code + "] " + message);
            }

            Thread.sleep(2000); // 2秒轮询
        }

        throw new RuntimeException("⚠️ 任务超时仍未完成，终止轮询！");
    }
}
