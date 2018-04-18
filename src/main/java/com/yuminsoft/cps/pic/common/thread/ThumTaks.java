/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月27日下午4:20:14
 *
*/

package com.yuminsoft.cps.pic.common.thread;

import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;
import com.yuminsoft.cps.pic.common.bean.ThumBean;
import com.yuminsoft.cps.pic.common.kit.ImageKit;

/**
 * 缩略图
 * 
 * @author YM10138
 *
 */
public class ThumTaks implements Runnable {
	private static Log log = Log.getLog(ThumTaks.class);

	@Override
	public void run() {
		while (!ThumQueue.isEmpty()) {
			ThumBean img = ThumQueue.get();
			try {
				ImageKit.resize(img.getSrcPath(), img.getDestPath(), img.getWidth(), img.getHeight(), img.getQuality(),
						false);
				// 清理缓存
				CacheKit.remove(ThumQueue.cache, img.getUuid());
			} catch (Exception e) {
				log.error("生成缩略图异常", e);
			}
		}
	}
}
