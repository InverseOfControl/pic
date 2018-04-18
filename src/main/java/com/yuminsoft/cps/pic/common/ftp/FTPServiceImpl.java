/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年1月22日下午6:11:01
 *
*/

package com.yuminsoft.cps.pic.common.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

/**
 * ClassName:FTPTemplate <br/>
 * Date: 2017年1月22日 下午6:11:01 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FTPServiceImpl implements FTPService {

	private static final Log log = Log.getLog(FTPServiceImpl.class);

	// 本地字符编码
	private static String LOCAL_CHARSET = "UTF-8";

	// FTP协议里面，规定文件名编码为iso-8859-1
	private static String SERVER_CHARSET = "ISO-8859-1";

	private static FTPClientConfigure config;

	// 连接池
	final GenericKeyedObjectPool<FTPClientConfigure, FTPClient> pool = new FTPClientPool(new FTPClientFactory(),
			new FTPPoolConfig());

	/**
	 * 获取. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:22:42
	 * @return
	 */
	private FTPClient get() {
		try {
			if (null == config)
				config = new FTPClientConfigure(PropKit.get("ftp.host"), PropKit.getInt("ftp.port"),
						PropKit.get("ftp.username"), PropKit.get("ftp.password"));
			return pool.borrowObject(config);
		} catch (Exception e) {
			log.error("获取FTP链接", e);
		}
		return null;
	}

	/**
	 * 归还. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:22:52
	 * @param obj
	 */
	private void returnObject(FTPClient obj) {
		pool.returnObject(config, obj);
	}

	@Override
	public boolean upload(String srcfile, String fileName) {
		return upload(new File(srcfile), fileName);
	}

	@Override
	public boolean upload(String srcfile, String dir, String fileName) {
		return upload(new File(srcfile), dir, fileName);
	}

	@Override
	public boolean upload(File file, String fileName) {
		return upload(file, null, fileName);
	}

	@Override
	public boolean upload(File file, String dir, String fileName) {
		try {
			return upload(new FileInputStream(file), dir, fileName);
		} catch (FileNotFoundException e) {
			log.error("FTP客户端出错", e);
		}
		return false;
	}

	@Override
	public boolean upload(InputStream fis, String fileName) {
		return upload(fis, null, fileName);
	}

	@Override
	public boolean upload(InputStream fis, String dir, String fileName) {
		boolean result = false;
		FTPClient client = get();
		if (null == client)
			return result;
		try {
			// 切换至跟目录
			client.changeWorkingDirectory("/");
			// 设置上传目录
			if (StrKit.notBlank(dir)) {
				// 防止中文目录乱码
				dir = new String(dir.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
				// 切换目录
				if (!client.changeWorkingDirectory(dir)) {
					// 切换失败，创建目录
					String[] dirs = dir.replaceAll("\\\\", "/").replaceAll("//", "/").split("/");
					for (String d : dirs) {
						if (StrKit.isBlank(d))
							continue;
						if (!client.changeWorkingDirectory(d)) {
							if (client.makeDirectory(d)) {
								if (!client.changeWorkingDirectory(d)) {
									log.error("切换目录失败！");
									return result;
								}
							} else {
								log.error("创建目录失败！");
								return result;
							}
						}
					}
				}
			}
			fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
			result = client.storeFile(fileName, fis);
		} catch (IOException e) {
			log.error("FTP客户端出错", e);
			result = false;
		} finally {
			IOUtils.closeQuietly(fis);
			returnObject(client);
		}
		return result;
	}

	/**
	 * @path /uploads/loan/13uploads/15/11/26/11261540986张锐.jpg
	 */
	@Override
	public InputStream download(String path) {
		// 设置上传目录
		if (StrKit.isBlank(path)) {
			return null;
		}
		FTPClient client = get();
		if (null == client)
			return null;
		String remote = null;
		try {
			// 防止中文目录乱码
			path = new String(path.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
			// 切换目录
			String dir = path.substring(0, path.lastIndexOf("/"));
			client.changeWorkingDirectory(dir);
			// 下载文件
			remote = path.substring(path.lastIndexOf("/") + 1);
			return client.retrieveFileStream(remote);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
