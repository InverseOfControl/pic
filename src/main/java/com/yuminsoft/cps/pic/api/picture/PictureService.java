/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:48:51
 *
*/

package com.yuminsoft.cps.pic.api.picture;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.jfinal.kit.FileKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.yuminsoft.cps.pic._admin.paperstype.PaperstypeService;
import com.yuminsoft.cps.pic._admin.syslog.SyslogService;
import com.yuminsoft.cps.pic.api.file.FileDataService;
import com.yuminsoft.cps.pic.common.bean.Config;
import com.yuminsoft.cps.pic.common.kit.ImageKit;
import com.yuminsoft.cps.pic.common.kit.UserKit;
import com.yuminsoft.cps.pic.common.model.OldPicture;
import com.yuminsoft.cps.pic.common.model.Paperstype;
import com.yuminsoft.cps.pic.common.model.Picture;

/**
 * 文件类型接口 <br/>
 * Date: 2017年3月17日 上午9:48:51 <br/>
 * 
 * @author renyz@yuminsoft.com
 */
public class PictureService {
	private static Log log = Log.getLog(PictureService.class);

	public static final PictureService me = new PictureService();
	final static Picture dao = new Picture().dao();
	final static PaperstypeService pservice = PaperstypeService.me;
	final static SyslogService logService = SyslogService.me;
	final static FileDataService fileDataService = FileDataService.me;

	/**
	 * 获取借款编号目录下的文件
	 * 
	 * @param appNo
	 * @param subclassSort
	 * @return
	 */
	public List<Picture> listByAppNoSort(String appNo, String subclassSort) {
		// 清理缓存
		CacheKit.remove("dirCount", appNo + subclassSort);
		List<Picture> list = dao.find(dao.getSql("api.pictureList"), appNo, subclassSort);
		// 目录中最大排序号
		Picture sort = dao.findFirst("SELECT MAX(SORTSID) SORTSID FROM PICTURE WHERE app_no = ? and SUBCLASS_SORT = ?",
				appNo, subclassSort);
		for (Picture picture : list) {
			working(picture);
			if (null != sort)
				picture.setMaxSortsid(sort.getSortsid().intValue());
		}
		// PIC-34【优化】针对迁移数据中的借款申请，如影像资料未分组，均放入其他文件夹
		// 将OldPicture表数据全部放入其他目录内,仅提供查看与单文件下载
		// 旧借款编号最大长度9
		if (null != appNo && "M".equals(subclassSort) && appNo.length() <= 9) {
			List<OldPicture> oldList = OldPicture.me.find("SELECT * FROM OLD_PICTURE WHERE LOAN_ID=?", appNo);
			for (OldPicture p : oldList) {
				Picture pt = new Picture();
				pt.setId(p.getId());
				pt.setOldId(p.getId());
				pt.setImgname(p.getFileName());
				pt.setSavename(p.getFilepath().substring(p.getFilepath().lastIndexOf("/") + 1));
				pt.setAppNo(p.getLoanId().toString());
				pt.setUrl(Config.me.getNginxUrl()
						+ (p.getFilepath().length() > 14 ? p.getFilepath().substring(14) : p.getFilepath()));
				pt.setThumUrl(pt.getUrl());
				pt.setName(p.getFileName());
				list.add(pt);
			}
		}
		return list;
	}

	/**
	 * 重命名图片名称
	 * 
	 * @Title: updateImgName
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 上午11:12:08
	 * @param imgName
	 * @param id
	 * @return
	 */
	public int updateImgName(String imgName, Integer sortid, String id) {

		return Db.update(
				"UPDATE PICTURE SET IMGNAME = ?,MODIFIER = ?,MODIFIED_JOBNUM = ?,MODIFIER_TIME = ?,SORTSID=? WHERE ID = ? ",
				imgName, UserKit.get().getOpreator(), UserKit.get().getJobNumber(),
				new Timestamp(System.currentTimeMillis()), sortid, id);
	}

	/**
	 * 根据Id删除
	 * 
	 * @Title: delete
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 上午11:39:02
	 * @param id
	 * @return
	 */
	public int delete(String id) {
		return Db.update("DELETE PICTURE WHERE ID = ?", id);
	}

	/**
	 * 插入标签图片关联表
	 * 
	 * @Title: saveFiletagPicture
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 下午1:40:18
	 * @param id
	 * @param pictureId
	 * @param filetagId
	 * @return
	 */
	public int saveFiletagPicture(String id, String pictureId, String filetagId) {
		return Db.update("INSERT INTO FILETAG_PICTURE VALUES(?,?,?)", id, pictureId, filetagId);
	}

	public Paperstype findPaperType(String id) {
		return pservice.findById(id);
	}

	public int updateSubclassSort(String itemCode, BigDecimal add, String id) {
		return Db.update(
				"UPDATE PICTURE SET SUBCLASS_SORT = ? ,SORTSID = ?,MODIFIER = ?,MODIFIED_JOBNUM = ?,MODIFIER_TIME = ?  WHERE ID = ?",
				itemCode, add, UserKit.get().getOpreator(), UserKit.get().getJobNumber(),
				new Timestamp(System.currentTimeMillis()), id);
	}

	public int updateWaste(String ifWaste, String id, Integer sortsid) {
		return Db.update(
				"UPDATE PICTURE SET if_waste = ?,MODIFIER = ?,MODIFIED_JOBNUM = ?,MODIFIER_TIME = ?,SORTSID=?  WHERE ID = ?",
				ifWaste, UserKit.get().getOpreator(), UserKit.get().getJobNumber(),
				new Timestamp(System.currentTimeMillis()), sortsid, id);
	}

	public int updatePatchBolt(String ifPatchBolt, String id) {
		return Db.update(
				"UPDATE PICTURE SET if_patch_bolt= ?,MODIFIER = ?,MODIFIED_JOBNUM = ?,MODIFIER_TIME = ?  WHERE ID = ?",
				ifPatchBolt, UserKit.get().getOpreator(), UserKit.get().getJobNumber(),
				new Timestamp(System.currentTimeMillis()), id);
	}

	public Picture findById(String id) {
		return dao.findById(id);
	}

	public int remove(String id) {
		Picture p = dao.findById(id);
		if (null == p)
			return 0;
		String path = Config.me.getUploadDir() + p.getSysName() + "/" + p.getAppNo() + "/" + p.getSubclassSort() + "/";
		File file = new File(path + p.getSavename());
		// 原文件
		if (file.exists()) {
			file.delete();
		}
		// 缩略图
		File f220 = new File(path + "220x220_" + p.getSavename());
		if (f220.exists()) {
			f220.delete();
		}
		File f1080 = new File(path + "1920x1080_" + p.getSavename());
		if (f1080.exists()) {
			f1080.delete();
		}
		// 清理缓存
		CacheKit.remove("dirCount", p.getAppNo() + p.getSubclassSort());
		logService.saveSyslog("文件删除", "删除(id:" + id + ")'" + p.getImgname() + "'");
		return delete(id);
	}

	public Ret updatePath(String[] ss, Paperstype pt) {
		// 根据目录查询出目录下的文件大小文件个数等
		Record record = null;
		String path = null;
		for (int i = 0; i < ss.length; i++) {
			Picture oldP = dao.findById(ss[i]);
			String code = oldP.getSubclassSort();
			if (i == 0) {
				record = fileDataService.count(oldP.getAppNo(), pt.getItemCode());
				if (null != record && -1 != pt.getFileNumber().intValue()) {
					if (record.getBigDecimal("count").add(new BigDecimal(ss.length))
							.compareTo(pt.getFileNumber()) == 1) {
						return Ret.fail("errormsg", "超出目录数量限制");
					}
				} else {
					return Ret.fail();
				}
				path = Config.me.getUploadDir() + oldP.getSysName() + File.separator + oldP.getAppNo() + File.separator;
			}
			/*
			 * 移动文件
			 */
			// 原文件
			String oldPath = path + oldP.getSubclassSort() + File.separator;
			File oldFile = new File(oldPath + oldP.getSavename());
			// 验证文件大小，格式，数量是否有误
			// ...待优化点
			Ret ret = validator(pt, oldFile, record, false);
			if (!ret.isOk()) {
				return ret;
			}
			// 目标地址
			String targetPath = path + pt.getItemCode() + File.separator;
			File targetDir = new File(targetPath);
			if (!targetDir.exists())
				targetDir.mkdirs();

			// 主图
			File newFile = new File(targetPath + oldP.getSavename());
			boolean bln = oldFile.renameTo(newFile);
			if (!bln) {
				log.error("移动失败,id:" + ss[i] + " loan_no:" + oldP.getAppNo());
			}
			/**
			 * 缩略图移动
			 */
			File file220x220 = new File(oldPath + "220x220_" + oldP.getSavename());
			if (file220x220.isFile()) {
				file220x220.renameTo(new File(targetPath + "220x220_" + oldP.getSavename()));
			}
			File file1920x1080 = new File(oldPath + "1920x1080_" + oldP.getSavename());
			if (file1920x1080.isFile()) {
				file1920x1080.renameTo(new File(targetPath + "1920x1080_" + oldP.getSavename()));
			}
			// 调整数据库
			oldP.setSubclassSort(pt.getItemCode());
			oldP.setModifier(UserKit.get().getOpreator());
			oldP.setModifiedJobnum(UserKit.get().getJobNumber());
			oldP.setModifierTime(new Timestamp(System.currentTimeMillis()));
			oldP.update();
			// Paperstype paperstype =
			// pservice.findByItemCodeAndSysName(oldP.getSubclassSort());
			// Paperstype newpaperstype =
			// pservice.findByItemCodeAndSysName(newP.getSubclassSort());
			// logService.saveSyslog("文件移动", "将文件(id:" + ss[i] + ")'" +
			// oldP.getImgname() + "'从'"+ paperstype.getItemValue() + "'移动到'" +
			// newpaperstype.getItemValue() + "'");
			// 清理缓存
			CacheKit.remove("dirCount", oldP.getAppNo() + code);
		}
		return Ret.ok();
	}

	/**
	 * 验证 个数、大小、文件类型. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月20日 下午5:24:19
	 * @return
	 */
	public Ret validator(Paperstype dir, File oldFile, Record record, boolean backup) {
		// 文件个数
		BigDecimal count = record.getBigDecimal("count");
		if (null != count && count.intValue() >= (dir.getFileNumber().intValue())) {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件夹附件已满,部分失败");
		}
		// 文件类型
		if (StrKit.notBlank(dir.getFileType())) {
			String[] str = dir.getFileType().split(",");
			boolean bln = true;
			for (String s : str) {
				if ((oldFile.getName().substring(oldFile.getName().lastIndexOf("."), oldFile.getName().length()))
						.toLowerCase().endsWith(s.trim().toLowerCase())) {
					bln = false;
					break;
				}
			}
			if (bln) {
				return Ret.fail("errorcode", "111111").set("errormsg", "文件类型不支持!");
			}
		} else {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件夹支持类型为空");
		}
		// 大小
		long fileSize = oldFile.length();
		BigDecimal size = dir.getFileSize();
		if (fileSize > size.multiply(new BigDecimal("1048576")).longValue()) {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件最大支持 " + size + "mb");
		}
		return Ret.ok();
	}

	public List<Picture> findByAppNo(String appNo, String subclassSort) {
		if (StrKit.notBlank(subclassSort)) {
			return dao.find("select * from picture where app_no = ? and subclass_sort = ?", appNo, subclassSort);
		} else {
			return dao.find("select * from picture where app_no = ?", appNo);
		}
	}

	/**
	 * 文件拷贝或复制
	 * 
	 * @param appNo
	 * @param newAppNo
	 * @param subclassSort
	 * @return
	 */
	public Ret updateAppNo(String appNo, String newAppNo, String subclassSort) {
		List<Picture> p = findByAppNo(appNo, subclassSort);
		if (StrKit.isBlank(subclassSort)) {
			/** 整个借款数据拷贝 **/
			// 文件拷贝
			// AppNo=20170101
			// ams/20170101
			// pms/20170101
			for (String sysname : Config.me.getSystemNameArray()) {
				try {
					File srcDir = new File(Config.me.getUploadDir() + sysname + File.separator + appNo);
					if (srcDir.isDirectory()) {
						File destDir = new File(Config.me.getUploadDir() + sysname + File.separator + newAppNo);
						FileUtils.copyDirectory(srcDir, destDir);
					}
				} catch (IOException e) {
					log.error("借款数据拷贝异常", e);
					return Ret.fail();
				}
			}
		} else {
			/** 借款数据下subclassSort目录拷贝 **/
			// 文件拷贝
			// AppNo=20170101
			// ams/20170101/subclassSort
			// pms/20170101/subclassSort
			for (String sysname : Config.me.getSystemNameArray()) {
				try {
					File srcDir = new File(Config.me.getUploadDir() + sysname + File.separator + appNo + File.separator
							+ subclassSort);
					if (srcDir.isDirectory()) {
						File destDir = new File(Config.me.getUploadDir() + sysname + File.separator + newAppNo
								+ File.separator + subclassSort);
						FileUtils.copyDirectory(srcDir, destDir);
					}
				} catch (IOException e) {
					log.error("借款数据拷贝异常", e);
					return Ret.fail();
				}
			}
		}
		// 数据记录拷贝
		for (Picture picture : p) {
			if (StrKit.notBlank(subclassSort))
				picture.setSubclassSort(subclassSort);
			picture.put("ID", Picture.seq);
			picture.setAppNo(newAppNo);
			boolean bln = picture.save();
			if (!bln) {
				return Ret.fail();
			}
		}
		return Ret.ok();
	}

	public Ret updateSortsid(String arrs, String subclassSort, String appNo) {
		// 验证 subclassSort、appNo与Id是否一致
		String[] ids = arrs.trim().split(",");
		for (String id : ids) {
			if (StrKit.isBlank(id)) {
				return Ret.fail("errorcode", "111111").set("errormsg", "格式错误");
			}
			Picture p = findById(id);
			if (null == p) {
				return Ret.fail("errorcode", "111111").set("errormsg", "ID(" + id + ")值不存在");
			}
			if (!p.getSubclassSort().equals(subclassSort) || !p.getAppNo().equals(appNo)) {
				return Ret.fail("errorcode", "111111").set("errormsg", "文件值(id：" + id + ")与subclassSort、appNo不一致");
			}
		}
		int i = 1;
		for (String id : ids) {
			Db.update("update PICTURE set SORTSID = ? where id = ?", i++, id);
		}
		return Ret.ok("errorcode", "000000");
	}

	/**
	 * 图片旋转
	 * 
	 * @param id
	 * @param rotate
	 * @return
	 */
	public boolean rotate(String id, String rotate) {
		Picture p = findById(id);
		String path = Config.me.getUploadDir() + p.getSysName() + "/" + p.getAppNo() + "/" + p.getSubclassSort() + "/";
		try {
			// 文件重命名解决浏览器缓存问题
			String fileName = UUID.randomUUID().toString().replaceAll("-", "")
					+ p.getSavename().substring(p.getSavename().lastIndexOf("."));
			File old = new File(path + p.getSavename());
			if (old.isFile()) {
				double degree = 0;
				if ("left".equals(rotate)) {
					degree = -90;
				} else if ("right".equals(rotate)) {
					degree = 90;
				}
				File file = new File(path + fileName);
				old.renameTo(file);
				ImageKit.rotate(file.getPath(), file.getPath(), degree);
				// 压缩
				try {
					// 缩略图旋转
					File old220 = new File(path + "/220x220_" + p.getSavename());
					if (old220.isFile()) {
						// 存在缩略图，旋转缩略图
						File file220 = new File(path + "/220x220_" + fileName);
						old220.renameTo(file220);
						ImageKit.rotate(file220.getPath(), file220.getPath(), degree);
					} else {
						// 不存在缩略图，生产缩略图
						File file220 = new File(path + "/220x220_" + fileName);
						ImageKit.resize(file.getPath(), file220.getPath(), 220, 220, 80.0, false);
					}
					File f1080 = new File(path + "/1920x1080_" + p.getSavename());
					if (f1080.isFile())
						f1080.delete();
				} catch (IOException e) {
					log.error("旋转-压缩图片异常", e);
				}
				p.setSavename(fileName);
				p.setVersion(p.getVersion() + 1);
				p.update();
				return true;
			}
		} catch (Exception e) {
			log.error("旋转图片异常", e);
		}
		return false;
	}

	/**
	 * 验证文件是否可以操作
	 * 
	 * @param id
	 * @param closingDate
	 * @return
	 */
	public boolean validateClosingDate(Integer id, String closingDate) {
		Picture p = dao.findById(id);
		if (null == p) {
			return false;
		}
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMddHHmmss");
		DateTime closingd = DateTime.parse(closingDate, format);
		DateTime picd = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(p.getCreateTime());
		return picd.isAfter(closingd);
	}

	public void working(Picture p) {
		String path = p.getSysName() + "/" + p.getAppNo() + "/" + p.getSubclassSort() + "/";
		p.setUrl(Config.me.getPicFileView() + path + p.getSavename());
		// 判断缩略图是否存在
		String thum = Config.me.getUploadDir() + path + "220x220_" + p.getSavename();
		File file = new File(thum);
		if (file.isFile()) {
			p.setThumUrl(Config.me.getPicFileView() + path + "220x220_" + p.getSavename());
		} else
			p.setThumUrl(p.getUrl());
		p.setName(p.getImgname() + p.getSavename().substring(p.getSavename().lastIndexOf(".")));
		file = null;
	}

	/**
	 * 批量删除数据库记录，文件暂存
	 * 
	 * @param list
	 * @return
	 */
	public boolean deleteBySubclassSort(List<Picture> list) {
		for (Picture p : list)
			p.delete();
		return true;
	}

	/**
	 * 批量查询-根据借款编号和文件类型集合查询文件（如果文件类型为空，则为全查）
	 * 
	 * @param appNo
	 *            借款编号
	 * @param sorts
	 *            文件类型集合
	 * @return
	 */
	public List<Picture> lists(String appNo, String sorts) {
		Kv kv = Kv.by("appNo", appNo);
		if (StrKit.notBlank(sorts)) {
			if (sorts.endsWith(","))
				sorts = sorts.substring(0, sorts.length() - 1);
			kv.set("sorts", sorts.trim().split(","));
		}
		SqlPara sp = Db.getSqlPara("api.pictureLists", kv);
		return dao.find(sp);
	}

	/**
	 * 批量删除-根据借款编号和文件类型批量删除文件
	 * 
	 * @param appNo
	 *            借款编号
	 * @param sorts
	 *            文件类型集合
	 * @return
	 */
	public Ret batchDelete(String appNo, String sorts) {
		if (StrKit.isBlank(sorts)) {
			// 删除 借款编号 下所有文件
			for (String str : Config.me.getSystemNameArray()) {
				FileKit.delete(new File(Config.me.getUploadDir() + str + "/" + appNo));
			}
			Db.update("delete from PICTURE where app_no=?", appNo);
		} else {
			// 删除 文件类型下所有文件
			String[] sortArray = sorts.split(",");
			for (String s : sortArray) {
				for (String str : Config.me.getSystemNameArray()) {
					FileKit.delete(new File(Config.me.getUploadDir() + str + "/" + appNo + "/" + s));
				}
				Db.update("delete from PICTURE where app_no=? and subclass_sort=?", appNo, s);
			}
		}
		return Ret.ok("errorcode", "000000").set("errormsg", "成功");
	}

	/**
	 * 批量作废-根据借款编号和文件类型集合批量作废文件
	 * 
	 * @param appNo
	 *            借款编号
	 * @param sorts
	 *            文件类型集合
	 * @return
	 */
	public Ret batchBlankOut(String appNo, String sorts) {
		if (StrKit.isBlank(sorts)) {
			Db.update(
					"UPDATE PICTURE SET if_waste = 'Y',MODIFIER = ?,MODIFIED_JOBNUM = ?,MODIFIER_TIME = ? WHERE APP_NO = ?",
					UserKit.get().getOpreator(), UserKit.get().getJobNumber(),
					new Timestamp(System.currentTimeMillis()), appNo);
		} else {
			String[] sortArray = sorts.split(",");
			for (String s : sortArray) {
				Db.update(
						"UPDATE PICTURE SET if_waste = 'Y',MODIFIER = ?,MODIFIED_JOBNUM = ?,MODIFIER_TIME = ? WHERE APP_NO = ? and subclass_sort=?",
						UserKit.get().getOpreator(), UserKit.get().getJobNumber(),
						new Timestamp(System.currentTimeMillis()), appNo, s);
			}
		}
		return Ret.ok("errorcode", "000000").set("errormsg", "成功");
	}
}
