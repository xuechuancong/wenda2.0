package com.nowcoder.util;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.CommentController;
import com.nowcoder.model.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class JedisAdaptor implements InitializingBean{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommentController.class);


    private JedisPool pool;

    public static void printObject(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void main(String[] args) {

        Jedis jedis = new Jedis("redis://127.0.0.1:6379/9");
        jedis.flushDB();
        jedis.set("foo", "bar1");
        printObject(0, jedis.get("foo"));
        jedis.rename("foo", "hello");
        printObject(1, jedis.get("hello"));
        jedis.setex("hello2",  15, "world");
        printObject(2, jedis.get("hello2"));

        //
        jedis.set("pv", "100");
        printObject(3, jedis.get("pv"));
        jedis.incr("pv");
        printObject(4, jedis.get("pv"));
        jedis.incrBy("pv", 3);
        printObject(5, jedis.get("pv"));

        //list
        String listName = "list";
        for (int i = 0; i < 12; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }

        //jedis.multi();
        //redis事务操作
        try {
            Transaction tx = jedis.multi();
            tx.zadd("q", 33, "Jim");
            tx.zadd("qq", 21, "Mac");
            List<Object> objs = tx.exec();
            tx.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        printObject(6, jedis.lrange(listName, 0, 12));
        printObject(7, jedis.lrange(listName, 0,3));
        printObject(8, jedis.lpop(listName));
        printObject(9, jedis.lrange(listName, 0, 11));
        printObject(10, jedis.lindex(listName, 3));
        printObject(11, jedis.lrange(listName, 0, 11));
        printObject(12, jedis.llen(listName));

        printObject(13, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "xxx"));
        printObject(14, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx1"));
        printObject(15, jedis.lrange(listName, 0, 15));

        //hash
        String userKey = "userxxx";
        jedis.hset(userKey, "name", "Jim");
        jedis.hset(userKey, "age", "22");
        jedis.hset(userKey, "phone", "123123123");
        printObject(16, jedis.hget(userKey, "name"));
        printObject(17, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        printObject(18, jedis.hgetAll(userKey));
        printObject(19, jedis.hexists(userKey, "email"));
        printObject(20, jedis.hexists(userKey, "name"));
        printObject(21, jedis.hkeys(userKey));
        printObject(22, jedis.hvals(userKey));
        jedis.hset(userKey, "phone", "121212121");
        jedis.hsetnx(userKey, "name", "mac");
        jedis.hsetnx(userKey, "school", "papap");
        printObject(23, jedis.hgetAll(userKey));

        //set
        String likeKeys1 = "commentLike1";
        String likeKeys2 = "commentLike2";

        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKeys1, String.valueOf(i));

            jedis.sadd(likeKeys2, String.valueOf(i*i));

        }

        printObject(24, jedis.smembers(likeKeys1));
        printObject(25, jedis.smembers(likeKeys2));
        printObject(26, jedis.sunion(likeKeys1, likeKeys2));
        printObject(27, jedis.sdiff(likeKeys1, likeKeys2));
        printObject(28, jedis.sinter(likeKeys1, likeKeys2));
        printObject(29, jedis.sismember(likeKeys1, "5"));
        printObject(30, jedis.sismember(likeKeys2, "2"));

        jedis.srem(likeKeys1, "5");
        printObject(31, jedis.smembers(likeKeys1));

        jedis.smove(likeKeys2, likeKeys1, "64");
        printObject(32, jedis.smembers(likeKeys1));
        printObject(33, jedis.smembers(likeKeys2));

        printObject(34, jedis.scard(likeKeys1));
        printObject(35, jedis.scard(likeKeys2));

        printObject(36, jedis.srandmember(likeKeys1, 4));

        //优先队列:zset
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 70, "Jim");
        jedis.zadd(rankKey, 20, "Ben");
        jedis.zadd(rankKey, 65, "Mac");
        jedis.zadd(rankKey, 95, "Lucy");
        jedis.zadd(rankKey, 54, "Lee");
        jedis.zadd(rankKey, 70, "Jane");

        printObject(37, jedis.zcard(rankKey));
        printObject(38, jedis.zcount(rankKey, 60, 90));
        printObject(39, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");
        printObject(40, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 3, "luc");
        printObject(41, jedis.zscore(rankKey, "luc"));
        printObject(42, jedis.zrange(rankKey, 0, 100));
        printObject(43, jedis.zrange(rankKey, 0, 4));
        printObject(44, jedis.zrevrange(rankKey, 0, 3));

        for (Tuple tuple: jedis.zrangeByScoreWithScores(rankKey, 60, 100)) {
            printObject(45, tuple.getElement() + " : " + String.valueOf(tuple.getScore()));
        }

        printObject(46, jedis.zrank(rankKey, "Lucy"));
        printObject(47, jedis.zrevrank(rankKey, "Lucy"));

        /*
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            printObject(48, j.get("foo"));
            j.close();

        }
        */

        //redis做缓存
        User user = new User();
        user.setName("Jim");
        user.setPassword("pppp");
        user.setHeadUrl("www.papppa.com");
        user.setSalt("xxx");

        jedis.set("user1", JSONObject.toJSONString(user));
        printObject(49, jedis.get("user1"));

        String value = jedis.get("user1");
        User user2 = JSONObject.parseObject(value, User.class);
        printObject(50, JSONObject.toJSON(user2));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://127.0.0.1:6379/10");
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }



    public Boolean sismember(String key, String value) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Jedis getJedis() {
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
        } finally {

        }
        return null;

    }

    public List<Object> exec(Transaction tx, Jedis jedis) {

        try {
            tx.exec();
        } catch (Exception e) {
            logger.error("出现异常！", e.getMessage());
            //回滚
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    logger.error("tx关闭出现异常！", ioe.getMessage());
                }

            }

            if (jedis != null) {
                jedis.close();
            }
        }

        return null;

    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public String lpop(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpop(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

}













