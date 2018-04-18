/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年1月23日上午10:23:50
 *
*/

package com.yuminsoft.cps.pic.common.ftp;

import java.io.File;
import java.io.InputStream;

/**
 * 图片FTP连接池 <br/>
 * Date: 2017年1月23日 上午10:23:50 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public interface FTPService {

	/**
	 * 上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:39:57
	 * @param srcfile
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(String srcfile, String fileName);

	/**
	 * 上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:40:05
	 * @param srcfile
	 *            文件路径
	 * @param dir
	 *            目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(String srcfile, String dir, String fileName);

	/**
	 * 上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:40:08
	 * @param file
	 *            文件
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(File file, String fileName);

	/**
	 * 上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:40:11
	 * @param file
	 *            文件
	 * @param dir
	 *            目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(File file, String dir, String fileName);

	/**
	 * 上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:29:38
	 * @param fis
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(InputStream fis, String fileName);

	/**
	 * 上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年1月23日 上午10:29:53
	 * @param fis
	 *            输入流
	 * @param dir
	 *            目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public boolean upload(InputStream fis, String dir, String fileName);

	/**
	 * 下载文件
	 * 
	 * @param path
	 * @return
	 */
	public InputStream download(String path);
}
