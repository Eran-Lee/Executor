package com.eran;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorTest {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
		Runnable task = new Runnable() {
			public void run() {
				System.out.println("HeartBeat.................");
			}
		};
		//1.模拟心跳
		//executor.scheduleAtFixedRate(task, 5, 3, TimeUnit.SECONDS);
		
		//2.线程池
		ExecutorService threadPool = ExecutorTest.newCachedThreadPool();
		for(int i=1;i<=3;i++) {
			final int taski = i;
//			TimeUnit.SECONDS.sleep(1);  //注解掉，则创建了三个线程
			threadPool.execute(new Runnable() {
				public void run() {
					System.out.println("线程名字：" + Thread.currentThread().getName() + ",任务名为：" + taski);
				}
			});
		}
		
		//3.Executor的生命周期
		ExecutorService executor2 = Executors.newSingleThreadExecutor();
		Future<String> future = executor2.submit(new Callable<String>() {
			public String call() {
				return "MOBIN";
			}
		});
		System.out.println("任务的执行结果：" + future.get());
		
		//4.ExecutorCompleteService,谁先执行完就返回谁
		ExecutorService executor3 = Executors.newFixedThreadPool(10);
		ExecutorCompletionService completionService = new ExecutorCompletionService<>(executor3);
		for(int i=1;i<=10;i++) {
			final int result = i;
			completionService.submit(new Callable() {
				public Object call() throws Exception{
					Thread.sleep(new Random().nextInt(5000));
					return result;
				}
			});
		}
		System.out.println(completionService.take().get());
		
		System.out.println("-----------ThreadPoolExecutor测试1---------");
//		ThreadPoolExecutor executor4= new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(5));
		//不过在java doc中，并不提倡我们直接使用ThreadPoolExecutor，而是使用Executors类中提供的几个静态方法来创建线程池
		//ExecutorService executor4 = Executors.newFixedThreadPool(5);
		ExecutorService executor4 = Executors.newSingleThreadExecutor();
		for(int i=0;i<15;i++){
//			MyTask myTask = new MyTask(i); //需要将MyTask类改为静态的才行
			MyTask myTask = new ExecutorTest().new MyTask(i);
			executor4.execute(myTask);
//			System.out.println("线程池中线程数目：" + executor4.getPoolSize() + "待执行的任务数目：" + executor4.getQueue().size()
//					+ ",已执行完的任务数目：" + executor4.getTaskCount());
		}
		executor4.shutdown();
	}
	
	public static ExecutorService newCachedThreadPool() {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}
	
	class MyTask implements Runnable {
	    private int taskNum;
	     
	    public MyTask(int num) {
	        this.taskNum = num;
	    }
	     
	    @SuppressWarnings("static-access")
		@Override
	    public void run() {
	        System.out.println("正在执行task "+taskNum);
	        try {
	            Thread.currentThread().sleep(4000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        System.out.println("task "+taskNum+"执行完毕");
	    }
	}
}
