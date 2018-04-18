/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月16日下午2:53:22
 *
*/

package com.yuminsoft.cps.pic.api.file;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.yuminsoft.cps.pic._admin.syslog.SyslogService;
import com.yuminsoft.cps.pic.api.paperstype.PaperstypeService;
import com.yuminsoft.cps.pic.common.bean.ThumBean;
import com.yuminsoft.cps.pic.common.kit.StringKit;
import com.yuminsoft.cps.pic.common.kit.UserKit;
import com.yuminsoft.cps.pic.common.model.Picture;
import com.yuminsoft.cps.pic.common.model.Privilege;
import com.yuminsoft.cps.pic.common.thread.ThumQueue;

/**
 * 文件相关操作 <br/>
 * Date: 2017年3月16日 下午2:53:22 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FileDataService {

	public static final FileDataService me = new FileDataService();
	static PaperstypeService paperstypeService = PaperstypeService.me;
	final static Privilege pdao = Privilege.me;
	static SyslogService logService = SyslogService.me;
	static com.yuminsoft.cps.pic._admin.paperstype.PaperstypeService pservice = com.yuminsoft.cps.pic._admin.paperstype.PaperstypeService.me;
	private static Log log = Log.getLog(FileDataService.class);
	String picurl = PropKit.get("pic.url");
	String picfileview = PropKit.get("pic.file.view");

	/**
	 * 统计业务状态下 文件夹的附件数与最大排序号. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月20日 下午5:44:19
	 * @param sysName
	 * @param appNo
	 * @param subclassSort
	 * @return
	 */
	public Record count(String appNo, String subclassSort) {
		return Db.findFirst(
				"select count(0) \"count\", max(t.sortsid) \"sortsid\" from PICTURE t where t.app_no=? and t.subclass_sort=?",
				appNo, subclassSort);
	}

	/**
	 * 上传文件(根据文件名匹配目录). 文件类型.*说明:.*不代表支持所有文件类型，表示未匹配到目录或匹配目录已满，将其放入.*标示的目录<br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月20日 上午11:21:39
	 * @param model
	 * @param file
	 * @return
	 */
	public Ret saveFile(Picture model, UploadFile file, String nodeKey, String appNo) {
		List<Record> list = paperstypeService.list(nodeKey, appNo);
		// 排序
		Collections.sort(list, new Comparator<Record>() {
			@Override
			public int compare(Record o1, Record o2) {
				if (null == o2.getStr("name"))
					return -1;
				if (null == o1.getStr("name"))
					return 1;
				return o2.getStr("name").length() - o1.getStr("name").length();
			}
		});
		Record acceptAllFileDir = null;// 接收其他目录文件 备用目录
		for (Record r : list) {
			if (StrKit.notBlank(r.getStr("fileType")) && r.getStr("fileType").indexOf(".*") != -1) {
				acceptAllFileDir = r;
				break;
			}
		}
		// 查询目录
		Record dir = null;
		for (Record r : list) {
			if (file.getOriginalFileName().startsWith(r.getStr("name"))) {
				dir = r;
				break;
			}
		}
		if (null == dir) {
			if (null == acceptAllFileDir)
				return Ret.fail("errorcode", "111111").set("errormsg", "未匹配到文件夹");
			else {
				dir = acceptAllFileDir;
			}
		}
		return save(model, file, dir);
	}

	/**
	 * 上传文件到指定目录. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月28日 上午10:55:35
	 * @param model
	 * @param file
	 * @param nodeKey
	 * @param appNo
	 * @param code
	 * @return
	 */
	public Ret saveFile(Picture model, UploadFile file, String nodeKey, String appNo, String code) {
		List<Record> list = paperstypeService.list(nodeKey, appNo);
		// 查询目录
		Record dir = null;
		for (Record r : list) {
			if (r.getStr("code").equals(code)) {
				dir = r;
				break;
			}
		}
		if (null == dir) {
			removeFile(file.getFile());
			return Ret.fail("errorcode", "111111").set("errormsg", "文件夹不存在");
		}
		return save(model, file, dir);
	}

	private Ret vali(Picture model, Record dir, Record record) {
		synchronized (this) {
			String key = model.getAppNo() + dir.getStr("code");
			Integer countCache = CacheKit.get("dirCount", key);
			Integer count = record.getBigDecimal("count").intValue();
			if (null == countCache) {
				CacheKit.put("dirCount", key, count + 1);
			} else {
				if (count > countCache) {
					CacheKit.put("dirCount", key, count);
				} else if (countCache >= (dir.getBigDecimal("fileNumber").intValue())) {
					return Ret.fail("errorcode", "111111").set("errormsg", "文件夹附件已满");
				} else {
					CacheKit.put("dirCount", key, countCache + 1);
				}
			}
		}
		return Ret.ok();
	}

	// 保存文件
	private Ret save(Picture model, UploadFile file, Record dir) {
		Record record = count(model.getAppNo(), dir.getStr("code"));
		// 通过缓存控制个数
		Ret ret = vali(model, dir, record);
		if (!ret.isOk()) {
			removeFile(file.getFile());
			return ret;
		}
		// 验证
		ret = validator(dir, file, record, false);
		if (!ret.isOk()) {
			removeFile(file.getFile());
			return ret;
		}
		model.put("ID", Picture.seq);
		String fileName = UUID.randomUUID().toString().replaceAll("-", "")
				+ file.getOriginalFileName().substring(file.getOriginalFileName().lastIndexOf("."));
		model.setSavename(fileName);
		model.setSubclassSort(dir.getStr("code"));
		model.setImgname(file.getOriginalFileName().substring(0, file.getOriginalFileName().lastIndexOf(".")));
		model.setUptime(new Timestamp(System.currentTimeMillis()));
		if (StrKit.isBlank(model.getIfPatchBolt())) {
			model.setIfPatchBolt("N");
		} else {
			model.setIfPatchBolt(model.getIfPatchBolt());
		}
		model.setCreator(UserKit.get().getOpreator());
		model.setCreateJobnum(UserKit.get().getJobNumber());
		model.setCreateTime(new Timestamp(System.currentTimeMillis()));
		model.setIfWaste("N");
		/**
		 * PIC-36【优化】上传附件时排序优化 上传附件时，附件命名为XXXXX-n，按照附件名短横线后的序号进行排序； 显示时，按照顺序排列。
		 * 排序规则（参见附件）：有序号未作废的附件按照序号排序（如有重复的号码，排列在一起，不分先后） > 无序号且未作废的附件 >
		 * 作废的附件按照序号排序（如有重复的号码，排列在一起，不分先后）
		 */
		// 业务设置的排序号。
		// 未设置的排序号为 1111。
		// 作废的附件序号前加 9999
		BigDecimal sortsid = null;
		String sortStr = StringKit.getSort(model.getImgname());
		if (null != sortStr) {
			sortsid = new BigDecimal(sortStr);
		}
		if ("Y".equals(model.getIfPatchBolt())) {
			if (null == sortsid) {
				sortsid = new BigDecimal("9999");
			} else {
				sortsid = new BigDecimal("9999" + sortsid.toString());
			}
		}
		if (null == sortsid) {
			sortsid = new BigDecimal("1111");
		}
		model.setSortsid(sortsid);
		// 图片保存
		String path = PropKit.get("upload.dir") + "/" + model.getSysName() + "/" + model.getAppNo() + "/"
				+ dir.getStr("code");
		File savefile = new File(path + "/" + fileName);
		// 目录
		File targetDir = new File(path);
		if (!targetDir.exists())
			targetDir.mkdirs();
		boolean isThum = file.getOriginalFileName().matches(PropKit.get("image.thum.regex"));
		boolean bln = file.getFile().renameTo(savefile);
		log.debug("上传文件:" + bln);
		if (!bln || !savefile.exists()) {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件上传失败");
		}
		// 先存储文件，再保存数据库
		model.setVersion(0);
		bln = model.save();
		// 压缩
		// 仅压缩图片
		if (isThum) {
			ThumQueue.add(new ThumBean(savefile.getPath(), path + "/220x220_" + fileName,
					UUID.randomUUID().toString().replaceAll("-", ""), 220, 220, 80.0));
		}
		logService.saveSyslog("文件上传", "将文件[ " + model.getImgname() + "]上传至[" + model.getSubclassSort() + "]");
		removeFile(file.getFile());
		if (!bln) {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件保存失败");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("id", model.getId());
		String uri = picfileview + model.getSysName() + "/" + model.getAppNo() + "/" + model.getSubclassSort() + "/";
		result.put("url", uri + model.getSavename());
		result.put("thumUrl", uri + "220x220_" + model.getSavename());
		return Ret.ok("errorcode", "000000").set("result", result);
	}

	/**
	 * 验证 个数、大小、文件类型. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月20日 下午5:24:19
	 * @return
	 */
	public Ret validator(Record dir, UploadFile file, Record record, boolean backup) {
		// 是否允许上传
		if (0 == dir.getBigDecimal("operateAdd").intValue()) {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件夹不允许上传");
		}
		// 文件个数
		BigDecimal count = record.getBigDecimal("count");
		synchronized (count) {
			if (null != count && count.intValue() >= (dir.getBigDecimal("fileNumber").intValue())) {
				removeFile(file.getFile());
				return Ret.fail("errorcode", "111111").set("errormsg", "文件夹附件已满");
			}
		}
		// 文件类型
		if (StrKit.notBlank(dir.getStr("fileType"))) {
			String[] str = dir.getStr("fileType").split(",");
			boolean bln = true;
			for (String s : str) {
				if (file.getOriginalFileName().toLowerCase().endsWith(s.trim().toLowerCase())) {
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
		long fileSize = file.getFile().length();
		BigDecimal size = dir.get("fileSize", PropKit.getInt("file.MaxPostSize", 10));
		if (fileSize > size.multiply(new BigDecimal("1048576")).longValue()) {
			return Ret.fail("errorcode", "111111").set("errormsg", "文件最大支持 " + size + "mb");
		}
		return Ret.ok();
	}

	private void removeFile(File file) {
		if (file.isFile())
			file.delete();
	}

	/**
	 * 根据环节查询按钮权限
	 * 
	 * @Title: findByNodeKey
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月24日 上午11:01:42
	 * @param nodeKey
	 * @return
	 */
	public Record findByNodeKey(String nodeKey) {
		return Db.findFirst(pdao.getSql("api.privilegeList"), nodeKey);
	}

	public List<Record> findPermissionList(String nodeKey) {
		return Db.find(pdao.getSql("api.permissionList"), nodeKey);
	}
}
