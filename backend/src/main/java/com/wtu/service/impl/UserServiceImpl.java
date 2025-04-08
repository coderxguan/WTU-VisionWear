package com.wtu.service.impl;

import com.wtu.DTO.MidjourneyTextDTO;
import com.wtu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //请求时账号共享同一个，所以url固定不变
    public static  final String url="https://api.zhishuyun.com/midjourney/imagine?token=4d03bd8c5dd047f0b0368e2b74dcdc5b";
    @Override
    public String textToimage(HttpHeaders header, MidjourneyTextDTO body) throws IOException {

        OkHttpClient client=new OkHttpClient();

        //构造FromBody对象,将参数挨个注入进FormBody中
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("action",body.getAction());
        builder.add("prompt",body.getPrompt());
        if(!body.getImageId().isEmpty()){
            builder.add("image_id",body.getImageId());
        }
        //将FormBody构造成RequestBody对象
        RequestBody requestBody = builder.build();

        System.out.println("requestBody:"+requestBody);

        Request request=new Request.Builder().
                url(url).
                post(requestBody).
                addHeader("accept", Objects.requireNonNull(header.getFirst("accept"))).
                addHeader("content-type", Objects.requireNonNull(header.getFirst("content-type"))).
                build();

        Response response = client.newCall(request).execute();
        log.info("成功完成调用!");

        //response.body()流只能用一次，信息保存在message中重复使用
        String message=null;
        if (response.body() != null) {
            message=response.body().string();
            System.out.println("具体信息:"+message);
        }


        return message;
    }
}
