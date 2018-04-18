/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年1月22日下午5:59:13
 *
*/

package com.yuminsoft.cps.pic.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * FTP 连接池 <br/>
 * Date: 2017年1月22日 下午5:59:13 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FTPClientPool extends GenericKeyedObjectPool<FTPClientConfigure, FTPClient> {
	
	public FTPClientPool(KeyedPooledObjectFactory<FTPClientConfigure, FTPClient> factory, GenericKeyedObjectPoolConfig config) {
		super(factory, config);
	}

}
