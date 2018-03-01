package com.nowcoder.service;


import com.nowcoder.model.Comment;
import com.nowcoder.util.JedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    JedisAdaptor jedisAdaptor;

    /**
     *  用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @param userId    主动发起关注的用户id，也就是当前hostholder保存的用户
     * @param entityType  三种
     * @param entityId    实体类型对应的id
     * @return
     */

    public Boolean follow(int userId, int entityType, int entityId) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdaptor.getJedis();
        Transaction tx = jedisAdaptor.multi(jedis);
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        //当前用户对这类实体+1
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdaptor.exec(tx, jedis);

        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;

    }


    public Boolean unfollow(int userId, int entityType, int entityId) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdaptor.getJedis();
        Transaction tx = jedisAdaptor.multi(jedis);
        tx.zrem(followerKey, String.valueOf(userId));
        //当前用户对这类实体-1
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdaptor.exec(tx, jedis);

        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;

    }

    //获取所有粉丝
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdaptor.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdaptor.zrevrange(followerKey, offset, offset+count));
    }

    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdaptor.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdaptor.zrevrange(followeeKey, offset, offset+count));
    }

    //关注这个实体类型的粉丝数目（entityType, entityId） -->  userId
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdaptor.zcard(followerKey);
    }

    //返回userId关注的entityType类型人数: userId ---> (entityType, entityId)
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdaptor.zcard(followeeKey);
    }

    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


    /**
     * 判断用户是否关注某个实体
     * @param userId   当前用户
     * @param entityType
     * @param entityId
     * @return
     */
    public Boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdaptor.zscore(followerKey, String.valueOf(userId)) != null;
    }

}
















