package com.nowcoder;

import redis.clients.jedis.Jedis;


class Producer1 implements Runnable {
    private Jedis jedis;

    public Producer1(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void run() {


        try {
            Thread.sleep(1000);
            for (int i = 0; i < 100; i++) {
                jedis.lpush(RedisUtil1.getProducerConsumer(), String.valueOf(i));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer1 implements Runnable {
    private Jedis jedis;

    public Consumer1(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + jedis.lpop(RedisUtil1.getProducerConsumer()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ThreadTest extends Thread {



    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("redis://127.0.0.1:6379/8")){

            jedis.flushDB();
            new Thread(new Producer1(jedis), "producer1").start();
            new Thread(new Consumer1(jedis), "consumer1").start();
            new Thread(new Consumer1(jedis), "consumer2").start();

        }




    }
}
