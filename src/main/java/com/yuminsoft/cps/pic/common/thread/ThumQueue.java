package com.yuminsoft.cps.pic.common.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jfinal.plugin.ehcache.CacheKit;
import com.yuminsoft.cps.pic.common.bean.ThumBean;

public class ThumQueue {
	// 缓存Key
	public static final String cache = "thumList";
	// 队列
	private static Queue<ThumBean> queue = new ConcurrentLinkedQueue<ThumBean>();

	/**
	 * 添加待生成缩略图. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月27日 下午4:25:26
	 * @param log
	 */
	public static void add(ThumBean log) {
		queue.offer(log);
		CacheKit.put(cache, log.getUuid(), log);
	}

	public static ThumBean get() {
		return queue.poll();
	}

	public static boolean isEmpty() {
		return queue.isEmpty();
	}
}
