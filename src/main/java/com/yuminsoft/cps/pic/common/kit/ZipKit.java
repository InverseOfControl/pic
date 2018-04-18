package com.yuminsoft.cps.pic.common.kit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 文件压缩
 * 
 * @author YM10138
 *
 */
public class ZipKit {
	private static final int BUFFER = 8192;

	/**
	 * 压缩单个或多文件
	 * 
	 * @param zipPath
	 *            压缩后的文件路径
	 * @param srcPathName
	 *            文件路径
	 * @param downloadFileName
	 *            文件别名
	 */
	public static void compress(String zipPath, List<String> srcPathName, List<String> downloadFileName) {
		try {
			File file = new File(zipPath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			// 创建写出流操作
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			compress(fileOutputStream, srcPathName, downloadFileName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 压缩单个或多文件方法
	 * 
	 * @param zipPath
	 *            压缩后的文件路径
	 * @param srcPathName
	 *            文件路径
	 * @param downloadFileName
	 *            文件别名
	 */
	public static void compress(OutputStream o, List<String> srcPathName, List<String> downloadFileName) {
		// 压缩后的文件对象
		try {
			// 创建写出流操作
			CheckedOutputStream cos = new CheckedOutputStream(o, new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			for (int i = 0; i < srcPathName.size(); i++) {
				String srcPath = srcPathName.get(i);
				String fileName = null;
				if (null != downloadFileName) {
					fileName = downloadFileName.get(i);
				}
				// 创建需要压缩的文件对象
				File file = new File(srcPath);
				if (!file.exists()) {
					// throw new RuntimeException(srcPath + "不存在！");
					continue;
				}
				/*
				 * (1)如果在zip压缩文件中不需要一级文件目录，定义String basedir = "";
				 * 下面的compress方法中当判断文件file是目录后不需要加上basedir = basedir +
				 * file.getName() + File.separator;
				 * (2)如果只是想在压缩后的zip文件里包含一级文件目录，不包含二级以下目录， 直接在这定义String basedir =
				 * file.getName() + File.separator;
				 * 下面的compress方法中当判断文件file是目录后不需要加上basedir = basedir +
				 * file.getName() + File.separator;
				 * (3)如果想压缩后的zip文件里包含一级文件目录，也包含二级以下目录，即zip文件里的目录结构和原文件一样
				 * 在此定义String basedir = "";
				 * 下面的compress方法中当判断文件file是目录后需要加上basedir = basedir +
				 * file.getName() + File.separator;
				 */
				// String basedir = file.getName() + File.separator;
				String basedir = "";
				compress(file, out, basedir, fileName);
			}
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void compress(File file, ZipOutputStream out, String basedir, String downloadFileName) {
		/*
		 * 判断是目录还是文件
		 */
		if (file.isDirectory()) {
			basedir += file.getName() + File.separator;
			compressDirectory(file, out, basedir);
		} else {
			compressFile(file, out, basedir, downloadFileName);
		}
	}

	/**
	 * 压缩一个目录
	 */
	private static void compressDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists()) {
			return;
		}
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* 递归 */
			compress(files[i], out, basedir, null);
		}
	}

	/**
	 * 压缩一个文件
	 */
	public static void compressFile(File file, ZipOutputStream out, String basedir, String downloadFileName) {
		if (!file.exists()) {
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			// 创建Zip实体，并添加进压缩包
			ZipEntry entry = new ZipEntry(basedir + null != downloadFileName ? downloadFileName : file.getName());
			out.putNextEntry(entry);
			// 读取待压缩的文件并写进压缩包里
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
