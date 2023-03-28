package org.call.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.call.pojo.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.call.topic}", consumerGroup = "${mq.voice.consumer.group.name}", messageModel = MessageModel.CLUSTERING)
public class VoiceMQListener implements RocketMQListener<MessageExt> {

    // 1. 业务层biz
    @Autowired
    RestTemplate restTemplate;

    // 2. 与clientId对应的 url
    @Value("${sdClientUrl}")
    String clientUrl;  // 回调通知接口

    private static final String[] KEY_INFO = {"0", "1"};

    @Override
    public void onMessage(MessageExt messageExt) {
        // 1.解析消息内容
        String body = null;
        try {
            body = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
            log.info("接收到消息body, {}", body);

            // json字符串转JSONObject对象
            JSONObject jsonObject = JSONObject.parseObject(body);

            // 2.调用业务层, 进行按键结果回调
            Thread.sleep(1000 * 30);  // 模拟接听耗时

            int index = (int) (System.currentTimeMillis() % 2);

            String result = KEY_INFO[index];

            String number = jsonObject.getString("telephoneNumber");

            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setResult(result);
            responseDTO.setTelephoneNumber(number);
            // 调用接口回传 /v1/demoCallResult
            try{
                callNoticeInfo(responseDTO, clientUrl);
            }catch (Exception ignored){
            }
            log.info("消息消费成功!");

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

    private void callNoticeInfo(ResponseDTO responseDTO, String clientUrl) {
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置请求类型
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String reqLog = JSONObject.toJSONString(responseDTO);
        System.out.println(reqLog);
        // 封装参数和头信息
        HttpEntity httpEntity = new HttpEntity(reqLog, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(clientUrl, httpEntity, String.class);
    }
}
