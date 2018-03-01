package com.nowcoder;

import org.junit.runner.RunWith;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


class MyThread extends Thread {

    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {

        try {

            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(String.format("T1, %d: %d", tid, i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class Consumer implements Runnable {
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {

            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable {
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {

            while (true) {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep(100);
                    q.put(String.valueOf(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class MultiThreadTest {

    public static void threadTest() {
        for (int i = 0; i < 10; i++) {
            //new MyThread(i).start();
        }

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; j++) {
                            Thread.sleep(1000);
                            System.out.println();
                            System.out.println(String.format("T2, %d: %d", fi, j) );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }



    private static Object obj = new Object();
    public static void testSynchronized1() {
        synchronized (obj) {
            try {

                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("S1, %d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2() {
        synchronized (new Object()) {
            try {

                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("S2, %d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized3() {
        synchronized (obj) {
            try {

                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("S3, %d", i));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void testSynchronized() {
        for (int i = 0; i < 10; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                    testSynchronized3();
                }
            }).start();
        }
    }



    public static void testBlockingQueue() {
        BlockingQueue<String > q = new ArrayBlockingQueue<String>(10);
        new Thread( new Producer(q)).start();
        new Thread( new Consumer(q), "Consumer1").start();
        new Thread( new Consumer(q), "Consumer2").start();

    }

    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    private static int id;

    public static void testThreadLocal() {


        for (int i = 0; i < 10; i++) {
            final int fi = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocal.set(fi);
                        Thread.sleep(1000);
                        System.out.println(String.format("threadLocal, %d", threadLocal.get()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        id = fi;
                        Thread.sleep(1000);
                        System.out.println(String.format("id, %d", id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    public static void testExecutor() {
        //ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newFixedThreadPool(4);

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println(String.format("Executor1: %d", i));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println(String.format("Executor2: %d", i));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.shutdown();

        while ( !service.isTerminated() ) {
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination.");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static int count = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static void testWithoutAtomic() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(1000);
                            count++;
                            System.out.println(count);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }

    public static void testWithAtomic() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(1000);
                            System.out.println(atomicInteger.incrementAndGet());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }

    public static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                //throw new IllegalArgumentException("异常");
                Thread.sleep(1000);
                return 1;
            }
        });

        service.shutdown();

        try {
            System.out.println(future.get() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //threadTest();
        //testSynchronized();
        //testBlockingQueue();

        //testThreadLocal();
        //testExecutor();
        //testWithoutAtomic();
        //testWithAtomic();
        //testFuture();
    }
}
