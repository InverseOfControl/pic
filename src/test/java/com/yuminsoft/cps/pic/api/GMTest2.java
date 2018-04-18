package com.yuminsoft.cps.pic.api;

import java.io.IOException;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

public class GMTest2 {

	public static void main(String[] args) throws Exception {
		resizeImages("F:/cache/a1.jpg","F:/cache/a2.jpg");
	}

	public static void resizeImages(String... pImageNames) throws IOException, InterruptedException, IM4JavaException {
		String imPath = "D:\\Program Files\\GraphicsMagick-1.3.26-Q16";
		// create command
		ConvertCmd cmd = new ConvertCmd(true);
		cmd.setSearchPath(imPath);

		// create the operation, add images and operators/options
		IMOperation op = new IMOperation();
		op.addImage();// 这里相当于插入了一个占位符，无参方法相当于op.addImage(Operation.IMG_PLACEHOLDER)
						// addImage方法还有两个重载方法
						// op.addImage(String... images) 支持修饰符
						// 如op.addImage("[300x200]");
						// op.addImage(Operation.IMG_PLACEHOLDER+"[300x200]");
						// op.addImage(int count) 一次性传入占位符的数量
		op.rotate(90.0);
		op.addImage();

		for (String srcImage : pImageNames) {
			int lastDot = srcImage.lastIndexOf('.');
			String dstImage = srcImage.substring(0, lastDot) + "_rotate.jpg";
			cmd.run(op, srcImage, srcImage);// 在run的时候，逐一替换占位符，这样相当于复用了Operation对象
											// run方法的第二个参数实际是一个Object[]支持任意个数的替换
			System.out.println("over " + dstImage);
		}
	}
}
