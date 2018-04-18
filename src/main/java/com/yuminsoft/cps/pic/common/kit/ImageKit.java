package com.yuminsoft.cps.pic.common.kit;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.core.ImageCommand;
import org.im4java.process.ArrayListOutputConsumer;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;

/**
 * 图片处理工具类
 * 
 * @author YM10138
 *
 */
/*
 * 请勿使用ImageIO获取图片信息 ImageIO.read(new File()) 对于大图片报：Caused by:
 * java.lang.OutOfMemoryError: Java heap space。
 */
public class ImageKit {

	/**
	 * 是否使用 GraphicsMagick
	 */
	private static final boolean USE_GRAPHICS_MAGICK_PATH = true;

	/**
	 * 水印图片路径
	 */
	private static String watermarkImagePath = "watermark.png";

	/**
	 * 水印图片
	 */
	private static Image watermarkImage = null;

	static {
		try {
			// watermarkImage = ImageIO.read(new URL(watermarkImagePath));
			watermarkImage = ImageIO.read(new File(watermarkImagePath));
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * 命令类型
	 * 
	 * @author hailin0@yeah.net
	 * @createDate 2016年6月5日
	 *
	 */
	private enum CommandType {
		convert("转换处理"), identify("图片信息"), compositecmd("图片合成");
		@SuppressWarnings("unused")
		private String name;

		CommandType(String name) {
			this.name = name;
		}
	}

	/**
	 * 获取 ImageCommand
	 * 
	 * @param command
	 *            命令类型
	 * @return
	 */
	private static ImageCommand getImageCommand(CommandType command) {
		ImageCommand cmd = null;
		switch (command) {
		case convert:
			cmd = new ConvertCmd(USE_GRAPHICS_MAGICK_PATH);
			break;
		case identify:
			cmd = new IdentifyCmd(USE_GRAPHICS_MAGICK_PATH);
			break;
		case compositecmd:
			cmd = new CompositeCmd(USE_GRAPHICS_MAGICK_PATH);
			break;
		}
		if (cmd != null && System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			cmd.setSearchPath(USE_GRAPHICS_MAGICK_PATH ? PropKit.get("gmpath") : PropKit.get("impath"));
		}
		return cmd;
	}

	/**
	 * 获取图片信息
	 * 
	 * @param srcImagePath
	 *            图片路径
	 * @return Map {height=, filelength=, directory=, width=, filename=}
	 */
	public static ImageBean getImageInfo(String srcImagePath) {
		IMOperation op = new IMOperation();
		op.format("%w,%h,%d,%f,%b");
		op.addImage(srcImagePath);
		IdentifyCmd identifyCmd = (IdentifyCmd) getImageCommand(CommandType.identify);
		ArrayListOutputConsumer output = new ArrayListOutputConsumer();
		identifyCmd.setOutputConsumer(output);
		try {
			identifyCmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<String> cmdOutput = output.getOutput();
		if (cmdOutput.size() != 1)
			return null;
		String line = cmdOutput.get(0);
		String[] arr = line.split(",");
		ImageBean image = new ImageBean();
		image.setFilename(arr[3]);
		image.setWidth(Integer.parseInt(arr[0]));
		image.setHeight(Integer.parseInt(arr[1]));
		image.setFilelength(arr[4]);
		image.setDirectory(arr[2]);
		return image;
	}

	/**
	 * 文字水印
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @param content
	 *            文字内容（不支持汉字）
	 * @throws Exception
	 */
	public static void addTextWatermark(String srcImagePath, String destImagePath, String content) throws Exception {
		IMOperation op = new IMOperation();
		op.font("微软雅黑");
		// 文字方位-东南
		op.gravity("southeast");
		// 文字信息
		op.pointsize(18).fill("#BCBFC8").draw("text 10,10 " + content);
		// 原图
		op.addImage(srcImagePath);
		// 目标
		op.addImage(createDirectory(destImagePath));
		ImageCommand cmd = getImageCommand(CommandType.convert);
		cmd.run(op);
	}

	/**
	 * 图片水印
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @param dissolve
	 *            透明度（0-100）
	 * @throws Exception
	 */
	public static void addImgWatermark(String srcImagePath, String destImagePath, Integer dissolve) throws Exception {
		// 原始图片信息
		ImageBean info = getImageInfo(srcImagePath);
		int w = info.getWidth();
		int h = info.getHeight();

		IMOperation op = new IMOperation();
		// 水印图片位置
		op.geometry(watermarkImage.getWidth(null), watermarkImage.getHeight(null),
				w - watermarkImage.getWidth(null) - 10, h - watermarkImage.getHeight(null) - 10);
		// 水印透明度
		op.dissolve(dissolve);
		// 水印
		op.addImage(watermarkImagePath);
		// 原图
		op.addImage(srcImagePath);
		// 目标
		op.addImage(createDirectory(destImagePath));
		ImageCommand cmd = getImageCommand(CommandType.compositecmd);
		cmd.run(op);
	}

	/**
	 * 压缩图片
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @param width
	 *            压缩后的宽
	 * @param height
	 *            压缩后的高
	 * @param quality
	 *            压缩质量（0-100）
	 * @param needWatermark
	 *            是否加水印
	 * @return
	 * @throws Exception
	 */
	public static boolean resize(String srcImagePath, String destImagePath, int width, int height, Double quality,
			boolean needWatermark) throws Exception {
		// 按照原有形状压缩（横图、竖图）
		ImageBean info = getImageInfo(srcImagePath);
		if (null == info)
			return false;
		int w = info.getWidth();
		int h = info.getHeight();
		// if ((w > h && width < height) || (w < h && width > height)) {
		// int temp = width;
		// width = height;
		// height = temp;
		// }
		// 是否压缩
		if (w < width && h < height) {
			// 不压缩-是否加水印
			if (needWatermark) {
				// 不压缩，加水印
				addImgWatermark(srcImagePath, destImagePath, 100);
			}
			// 不调整尺寸压缩
			ImageCommand cmd = getImageCommand(CommandType.convert);
			IMOperation op = new IMOperation();
			op.addImage();
			op.quality(quality);
			op.addImage();
			cmd.run(op, srcImagePath, destImagePath);
			return true;
		}
		// 压缩-是否加水印
		if (needWatermark) {
			// 压缩-加水印比例
			double cropRatio = 0f;
			if ((width + 0.0) / (w + 0.0) > (height + 0.0) / (h + 0.0)) {
				cropRatio = (height + 0.0) / (h + 0.0);
			} else {
				cropRatio = (width + 0.0) / (w + 0.0);
			}
			IMOperation op = new IMOperation();
			ImageCommand cmd = getImageCommand(CommandType.compositecmd);
			op.geometry(watermarkImage.getWidth(null), watermarkImage.getHeight(null),
					(int) (w * cropRatio) - watermarkImage.getWidth(null) - 10,
					(int) (h * cropRatio) - watermarkImage.getHeight(null) - 10);
			op.addImage(watermarkImagePath);
			op.addImage();
			op.quality(quality);
			op.resize(width, height);
			op.addImage();
			cmd.run(op, srcImagePath, createDirectory(destImagePath));
			return true;
		}

		// 压缩-不加水印
		ImageCommand cmd = getImageCommand(CommandType.convert);
		IMOperation op = new IMOperation();
		op.addImage();
		op.quality(quality);
		op.resize(width, height);
		op.addImage();
		cmd.run(op, srcImagePath, destImagePath);
		return true;
	}

	/**
	 * 去除Exif信息，可减小文件大小
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @throws Exception
	 */
	public static void removeProfile(String srcImagePath, String destImagePath) throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);
		op.profile("*");
		op.addImage(createDirectory(destImagePath));
		ImageCommand cmd = getImageCommand(CommandType.convert);
		cmd.run(op);
	}

	/**
	 * 等比缩放图片（如果width为空，则按height缩放; 如果height为空，则按width缩放）
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @param width
	 *            缩放后的宽度
	 * @param height
	 *            缩放后的高度
	 * @throws Exception
	 */
	public static void scaleResize(String srcImagePath, String destImagePath, Integer width, Integer height)
			throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);
		op.sample(width, height);
		op.addImage(createDirectory(destImagePath));
		ImageCommand cmd = getImageCommand(CommandType.convert);
		cmd.run(op);
	}

	/**
	 * 从原图中裁剪出新图
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @param x
	 *            原图左上角
	 * @param y
	 *            原图左上角
	 * @param width
	 *            新图片宽度
	 * @param height
	 *            新图片高度
	 * @throws Exception
	 */
	public static void crop(String srcImagePath, String destImagePath, int x, int y, int width, int height)
			throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);
		op.crop(width, height, x, y);
		op.addImage(createDirectory(destImagePath));
		ImageCommand cmd = getImageCommand(CommandType.convert);
		cmd.run(op);
	}

	/**
	 * 将图片分割为若干小图
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param destImagePath
	 *            目标图片路径
	 * @param width
	 *            指定宽度（默认为完整宽度）
	 * @param height
	 *            指定高度（默认为完整高度）
	 * @return 小图路径
	 * @throws Exception
	 */
	public static List<String> subsection(String srcImagePath, String destImagePath, Integer width, Integer height)
			throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);
		op.crop(width, height);
		op.addImage(createDirectory(destImagePath));

		ImageCommand cmd = getImageCommand(CommandType.convert);
		cmd.run(op);

		return getSubImages(destImagePath);
	}

	/**
	 * 图片旋转(顺时针旋转) 拼装命令示例: gm convert -rotate 90 /apps/watch.jpg
	 * /apps/watch_compress.jpg
	 *
	 * @param imagePath
	 *            源图片路径
	 * @param newPath
	 *            处理后图片路径
	 * @param degree
	 *            旋转角度
	 */
	public static boolean rotate(String imagePath, String newPath, double degree) {
		boolean flag;
		try {
			// 1.将角度转换到0-360度之间
			degree = degree % 360;
			if (degree <= 0) {
				degree = 360 + degree;
			}
			IMOperation op = new IMOperation();
			op.addImage();
			op.rotate(degree);
			op.addImage();
			ImageCommand cmd = getImageCommand(CommandType.convert);
			cmd.run(op, imagePath, newPath);
			flag = true;
		} catch (Exception e) {
			flag = false;
			LogKit.error("图片旋转失败!", e);
		}
		return flag;
	}

	/**
	 * 获取图片分割后的小图路径
	 * 
	 * @param destImagePath
	 *            目标图片路径
	 * @return 小图路径
	 */
	private static List<String> getSubImages(String destImagePath) {
		// 文件所在目录
		String fileDir = destImagePath.substring(0, destImagePath.lastIndexOf(File.separatorChar));
		// 文件名称
		String fileName = destImagePath.substring(destImagePath.lastIndexOf(File.separatorChar) + 1);
		// 文件名（无后缀）
		String n1 = fileName.substring(0, fileName.lastIndexOf("."));
		// 后缀
		String n2 = fileName.replace(n1, "");

		List<String> fileList = new ArrayList<String>();
		String path = null;
		for (int i = 0;; i++) {
			path = fileDir + File.separatorChar + n1 + "-" + i + n2;
			if (new File(path).exists())
				fileList.add(path);
			else
				break;
		}
		return fileList;
	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 * @return path
	 */
	private static String createDirectory(String path) {
		File file = new File(path);
		if (!file.exists())
			file.getParentFile().mkdirs();
		return path;
	}
}
