package com.yuminsoft.cps.pic.common.cron4j;

import java.io.File;
import java.io.FileFilter;

import org.joda.time.DateTime;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

/**
 * 每天清理 前一小时上传失败文件
 * 
 * @author YM10138
 *
 */
public class ClearUploadCacheTask implements Runnable {
	private static final Log log = Log.getLog(ClearUploadCacheTask.class);

	@Override
	public void run() {
		try {
			// 1小时之前
			DateTime time = DateTime.now().minusHours(1);
			// 缓存目录
			String dir = PropKit.get("upload.dir") + PropKit.get("upload.dir.cache");
			File file = new File(dir);
			if (file.isDirectory()) {
				// 上传异常未删除的文件
				File[] list = file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						// 文件且日期小于 time
						return pathname.isFile() && time.isAfter(pathname.lastModified());
					}
				});
				// 删除文件
				if (null != list && list.length > 0)
					for (File f : list)
						f.delete();
			}
		} catch (Exception e) {
			log.error("清理缓存文件失败", e);
		}
	}
}
