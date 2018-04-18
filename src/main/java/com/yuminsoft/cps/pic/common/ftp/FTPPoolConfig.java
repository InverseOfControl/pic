/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年1月22日下午6:09:30
 *
*/

package com.yuminsoft.cps.pic.common.ftp;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * FTP连接设置参数 <br/>
 * Date: 2017年1月22日 下午6:09:30 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FTPPoolConfig extends GenericKeyedObjectPoolConfig {
	public FTPPoolConfig() {
		setTestWhileIdle(true);// 发呆过长移除的时候是否test一下
		setTimeBetweenEvictionRunsMillis(1 * 60000L);// -1不启动。默认1min一次
		setMinEvictableIdleTimeMillis(10 * 60000L); // 可发呆的时间,30mins
		setTestOnBorrow(true);
	}
}
