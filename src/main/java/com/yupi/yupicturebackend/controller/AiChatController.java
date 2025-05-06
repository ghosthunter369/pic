package com.yupi.yupicturebackend.controller;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.entity.UserChat;
import com.yupi.yupicturebackend.service.UserChatService;
import com.yupi.yupicturebackend.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/chat")
public class AiChatController {
    @Resource
    private UserChatService userChatService;
    @Resource
    private UserService userService;
    @Resource
    private ApplicationParam applicationParam;
    @GetMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public Flux<String> streamChat(@RequestParam String question, HttpServletRequest request) {
        return Flux.create(sink -> {
            User loginUser = userService.getLoginUser(request);
            UserChat userChat = userChatService.getOne(
                    new QueryWrapper<UserChat>().eq("userId", loginUser.getId()));

            if (userChat == null) {
                userChat = new UserChat();
                userChat.setUserId(loginUser.getId());
                Application application = new Application();
                try {
                    ApplicationResult call = application.call(applicationParam);
                    String sessionId = call.getOutput().getSessionId();
                    userChat.setSessionId(sessionId);
                    applicationParam.setSessionId(sessionId);
                    userChatService.save(userChat);
                } catch (NoApiKeyException | InputRequiredException e) {
                    sink.error(e);
                    return;
                }
            } else {
                applicationParam.setSessionId(userChat.getSessionId());
            }

            applicationParam.setPrompt(question);
            Application application = new Application();

            // 记录上一次的完整输出
            final StringBuilder previousText = new StringBuilder();

            try {
                application.streamCall(applicationParam).blockingForEach(applicationResult -> {
                    String currentText = applicationResult.getOutput().getText();
                    String previous = previousText.toString();

                    if (!currentText.startsWith(previous)) {
                        // 文本突变，可能是模型策略变了，整个替换
                        sink.next(currentText);
                    } else {
                        // 只取新增部分
                        String delta = currentText.substring(previous.length());
                        if (!delta.isEmpty()) {
                            sink.next(delta);
                        }
                    }

                    previousText.setLength(0);
                    previousText.append(currentText);
                });

                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

}
