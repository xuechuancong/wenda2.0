package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    JedisAdaptor jedisAdaptor;

    public Boolean fireEvent(EventModel model) {
        try {

            String json = JSONObject.toJSONString(model);
            String key = RedisKeyUtil.getEventqueueKey();
            jedisAdaptor.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
