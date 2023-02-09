package org.call.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.call.common.IDWorker;
import org.call.pojo.MqEntity;
import org.call.pojo.RequestDTO;
import org.call.pojo.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RestController
public class InterfaceController {

    @Value("${mq.voice.topic}")
    private String topic;

    @Value("${mq.voice.tag.test}")
    private String tag;

    @Value("${voiceNoticeUrl}")
    private String voiceNoticeUrl; // https://uat.weygo.cn/interface/importnumber?

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private IDWorker idWorker;

    @PostMapping("/importnumber")
    public ResponseDTO importnumber(RequestDTO requestDTO) throws IOException {

        // 解析data;
        BASE64Decoder decoder = new BASE64Decoder();
        String base64Str = new String(decoder.decodeBuffer(requestDTO.getData()));

        // json字符串转JSONObject对象
        JSONObject jsonObject = JSONObject.parseObject(base64Str);
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            log.info("导入号码：{}，导入的地点：{}", next.getKey(), next.getValue().toString());
            // 消息异步解耦
            long msgId = idWorker.nextId();
            MqEntity mqEntity = new MqEntity(msgId, next.getKey());

            try {
                sendCall(topic, tag, String.valueOf(msgId), JSON.toJSONString(mqEntity));
            } catch  (Exception e1) {
                log.error("号码导入确认失败", e1);
            }

        }
        return new ResponseDTO("success");
    }

    private void sendCall(String topic, String tag, String keys, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(topic, tag, keys, body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

    public static void main(String[] args) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        String base64Str = new String(decoder.decodeBuffer("eyIxNTkzMzIxNDQ3MSI6WyIyMDIz5bm0MDLmnIgwOeaXpTEw5pe2MTfliIYiLCIiLCIiLCIiLCLl\n" +
                "m57lpI0wOuS4jeWPguS4jizlm57lpI0xOuW4guacrOe6pyzlm57lpI0zOumrmOaWsOWMuiJdLCIx\n" +
                "ODY0OTE0NTQ2MyI6WyIyMDIz5bm0MDLmnIgwOeaXpTEw5pe2MTfliIYiLCIiLCIiLCIiLCLlm57l\n" +
                "pI0wOuS4jeWPguS4jizlm57lpI0xOuW4guacrOe6pyzlm57lpI0zOumrmOaWsOWMuiJdfQ=="));
        System.out.println(base64Str);
    }

}
