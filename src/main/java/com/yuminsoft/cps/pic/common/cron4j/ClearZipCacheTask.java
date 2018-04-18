package com.yuminsoft.cps.pic.common.cron4j;

import java.io.File;

import org.joda.time.DateTime;

import com.jfinal.kit.FileKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

/**
 * 每天清理 前一天的ZIP缓存
 * 
 * @author YM10138
 *
 */
public class ClearZipCacheTask implements Runnable {
	private static final Log log = Log.getLog(ClearZipCacheTask.class);

	@Override
	public void run() {
		try {
			// 前一天
			DateTime time = DateTime.now().minusDays(1);
			// 前一天的缓存目录
			String dir = PropKit.get("upload.dir") + PropKit.get("upload.dir.cache") + "/" + time.toString("yyyyMMdd");
			File file = new File(dir);
			if (file.isDirectory()) {
				// 删除
				FileKit.delete(file);
			}
		} catch (Exception e) {
			log.error("清理缓存ZIP失败", e);
		}
	}
}
