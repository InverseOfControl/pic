package com.yuminsoft.cps.pic.common.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yuminsoft.cps.pic.common.model.Syslog;

public class SyslogQueue {

	// 队列
	private static Queue<Syslog> queue = new ConcurrentLinkedQueue<Syslog>();

	/**
	 * 添加日志. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月27日 下午4:25:26
	 * @param log
	 */
	public static void add(Syslog log) {
		queue.offer(log);
	}

	public static Syslog get() {
		return queue.poll();
	}

	public static boolean isEmpty() {
		return queue.isEmpty();
	}
}
