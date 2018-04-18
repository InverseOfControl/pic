package com.yuminsoft.cps.pic.common.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 调度时间小于1分钟使用此类，大于1分钟使用系统集成第三方工具cron4j
 * 
 * @author YM10138
 *
 */
public class ScheduledExecutor {

	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

	/**
	 * 启动线程. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月27日 下午5:20:16
	 */
	public static void start() {
		// 日志 三秒后启动 每两秒执行一次
		service.scheduleWithFixedDelay(new SysLogTaks(), 3, 2, TimeUnit.SECONDS);
		// 缩略图 三秒后启动 每一秒执行一次
		service.scheduleWithFixedDelay(new ThumTaks(), 3, 1, TimeUnit.SECONDS);
	}

	/**
	 * 停止任务
	 */
	public static void shutdown() {
		service.shutdown();
	}
}
