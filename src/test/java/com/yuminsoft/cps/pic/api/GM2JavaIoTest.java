package com.yuminsoft.cps.pic.api;

import java.io.File;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class GM2JavaIoTest {

	public static void main(String[] args) {
		String path = "F:/cache/a1 - 副本.jpg";

		DateTime start = DateTime.now();
		for (int i = 0; i < 10000; i++) {
			File file = new File(path);
			if (file.exists()) {
				System.out.println(file.getName());
			} else {
			}
			file = null;
		}
		DateTime end = DateTime.now();
		System.out.println(Seconds.secondsBetween(start, end).getSeconds());
		/*PropKit.use("F:/workspace/PIC/src/main/resources/cps_pic.properties");
		for (int i = 0; i < 100000; i++) {
			ImageBean ib = ImageKit.getImageInfo(path);
			System.out.println(ib);
		}
		System.out.println(Seconds.secondsBetween(end, DateTime.now()).getSeconds());*/
	}
}
