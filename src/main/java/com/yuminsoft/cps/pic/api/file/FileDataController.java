/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午3:50:49
 *
*/

package com.yuminsoft.cps.pic.api.file;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.MDC;
import org.joda.time.DateTime;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.yuminsoft.cps.pic.api.common.ApiController;
import com.yuminsoft.cps.pic.api.common.ApiPlatformValidator;
import com.yuminsoft.cps.pic.api.common.ApiValidator;
import com.yuminsoft.cps.pic.api.common.ClearCacheFileInterceptor;
import com.yuminsoft.cps.pic.api.picture.PictureService;
import com.yuminsoft.cps.pic.common.bean.Config;
import com.yuminsoft.cps.pic.common.bean.UserBean;
import com.yuminsoft.cps.pic.common.interceptor.GlobalInterceptor;
import com.yuminsoft.cps.pic.common.kit.StringKit;
import com.yuminsoft.cps.pic.common.kit.UserKit;
import com.yuminsoft.cps.pic.common.kit.ZipKit;
import com.yuminsoft.cps.pic.common.model.Paperstype;
import com.yuminsoft.cps.pic.common.model.Picture;

/**
 * 接口页面 <br/>
 * Date: 2017年3月14日 下午3:50:49 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
@Before(ApiPlatformValidator.class)
public class FileDataController extends ApiController {
	private int m = 1024 * 1024;
	private FileDataService service = enhance(FileDataService.class);
	private PictureService pictureService = enhance(PictureService.class);
	private static final Log log = Log.getLog(FileDataController.class);

	/**
	 * 文件操作页面. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月16日 下午2:51:36
	 */
	@Before(ApiValidator.class)
	public void index() {
		// 验证系统名称是否支持
		if (Config.me.getSystemName().concat(",").indexOf(getPara("sysName").concat(",")) == -1) {
			setAttr("errormsg", "系统名称不支持[sysName]");
			render("error.html");
			return;
		}
		keepPara();
		// 最大文件限制
		setAttr("maxPostSize", Config.me.getFileMaxPostSize() * m);
		Record privilege = service.findByNodeKey(getNodeKey());
		setAttr("privilege", privilege);
		List<Record> permissionList = service.findPermissionList(getNodeKey());
		setAttr("permission", permissionToMap(permissionList));
		boolean view = false;
		if (null != privilege) {
			for (Object r : privilege.getColumnValues()) {
				if (null != r && r.toString().equals("1")) {
					view = true;
					break;
				}
			}
		}
		setAttr("view", view);
		render("filedata.html");
	}

	private Map<String, String> permissionToMap(List<Record> permissionList) {
		Map<String, String> result = new HashMap<String, String>();
		if (permissionList == null || permissionList.size() == 0) {
			return result;
		}
		for (Record r : permissionList) {
			result.put((String) r.get("item_code"), (String) r.get("permission"));
		}
		return result;
	}

	/**
	 * 文件比对页面. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月16日 下午2:52:06
	 */
	@Before(CompareToValidator.class)
	public void compareTo() {
		keepPara();
		render("filedataCompareTo.html");
	}

	/**
	 * 文件上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @throws UnsupportedEncodingException
	 * @date: 2017年3月17日 下午1:51:33
	 */
	@Clear
	@Before({ GlobalInterceptor.class, ClearCacheFileInterceptor.class })
	public void upload() throws UnsupportedEncodingException {
		UploadFile file = null;
		try {
			file = getFile();
		} catch (Exception e) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "表单必须是multipart/form-data类型"));
			return;
		}
		if (null == file) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "上传文件(file)不能为空"));
			return;
		}
		if (isBlank("sysName", "参数(sysName)不能为空")) {
			return;
		}
		if (isBlank("nodeKey", "参数(nodeKey)不能为空")) {
			return;
		}
		if (isBlank("appNo", "参数(appNo)不能为空")) {
			return;
		}
		if (isBlank("operator", "操作人姓名（operator）必填")) {
			return;
		}
		if (isBlank("jobNumber", "工号（jobNumber）必填")) {
			return;
		}
		if (isBlank("dataSources", "参数(dataSources)不能为空")) {
			return;
		}
		if (!"1".equals(getPara("dataSources")) && !"0".equals(getPara("dataSources"))) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "数据来源(dataSources)参数不正确，只能是1或0"));
			return;
		}
		// 验证系统名称是否支持
		if (Config.me.getSystemName().concat(",").indexOf(getPara("sysName").concat(",")) == -1) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "系统名称不支持"));
			return;
		}

		Picture model = new Picture();
		if ("Y".equals(getPara("ifPatchBolt"))) {
			model.setIfPatchBolt(getPara("ifPatchBolt"));
		}
		model.setSysName(getPara("sysName"));
		model.setAppNo(getPara("appNo"));
		model.setDataSources(new BigDecimal(getPara("dataSources")));
		String operator = getPara("operator");
		if (StrKit.notBlank(operator) && StringKit.isMessyCode(operator))
			try {
				operator = new String(operator.getBytes("iso8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("操作人姓名转码异常", e);
			}
		UserKit.put(new UserBean(MDC.get("token").toString(), operator, getPara("jobNumber")));
		Ret ret = service.saveFile(model, file, getPara("nodeKey"), getPara("appNo"));
		renderJson(ret);
	}

	/**
	 * 文件上传到指定文件夹 . <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @throws UnsupportedEncodingException
	 * @date: 2017年3月28日 上午10:47:03
	 */
	@Clear
	@Before({ GlobalInterceptor.class, ClearCacheFileInterceptor.class })
	public void uploadfile() throws UnsupportedEncodingException {
		UploadFile file = null;
		try {
			file = getFile();
		} catch (Exception e) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "表单必须是multipart/form-data类型"));
			return;
		}
		if (null == file) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "上传文件(file)不能为空"));
			return;
		}
		if (isBlank("sysName", "参数(sysName)不能为空")) {
			return;
		}
		if (isBlank("nodeKey", "参数(nodeKey)不能为空")) {
			return;
		}
		if (isBlank("appNo", "参数(appNo)不能为空")) {
			return;
		}
		if (isBlank("operator", "操作人姓名（operator）必填")) {
			return;
		}
		if (isBlank("jobNumber", "工号（jobNumber）必填")) {
			return;
		}
		if (isBlank("dataSources", "参数(dataSources)不能为空")) {
			return;
		}
		if (isBlank("code", "目录编号(code)不能为空")) {
			return;
		}

		// 验证系统名称是否支持
		if (Config.me.getSystemName().concat(",").indexOf(getPara("sysName").concat(",")) == -1) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "系统名称不支持"));
			return;
		}

		if ("1".equals(getPara("dataSources")) || "0".equals(getPara("dataSources"))) {
			Picture model = new Picture();
			model.setSysName(getPara("sysName"));
			model.setAppNo(getPara("appNo"));
			model.setDataSources(new BigDecimal(getPara("dataSources")));
			String operator = getPara("operator");
			if (StrKit.notBlank(operator) && StringKit.isMessyCode(operator))
				try {
					operator = new String(operator.getBytes("iso8859-1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.error("操作人姓名转码异常", e);
				}
			UserKit.put(new UserBean(MDC.get("token").toString(), operator, getPara("jobNumber")));
			Ret ret = service.saveFile(model, file, getPara("nodeKey"), getPara("appNo"), getPara("code"));
			renderJson(ret);
		} else {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "数据来源(dataSources)参数不正确，只能是1或0"));
		}
	}

	/**
	 * PDF预览
	 */
	public void pdfView() {
		Picture p = pictureService.findById(getPara("id"));
		pictureService.working(p);
		setAttr("model", p);
		render("pdfView.html");
	}

	/**
	 * 批量下载 <br>
	 * 业务逻辑：将请求的ids文件压缩md5.zip放到${upload.dir}/${upload.dir.cache}/yyyyMMdd目录,转发地址到nginx下载。
	 * 
	 * @throws IOException
	 */
	@Clear
	@Before(GlobalInterceptor.class)
	public void batchDownload() throws IOException {
		if (isBlank("ids", "参数(ids)不能为空")) {
			return;
		}
		String ids = getPara("ids");
		String[] idsArray = ids.trim().split(",");
		if (null == idsArray || idsArray.length == 0) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "ids解析失败"));
			return;
		}
		List<String> paths = new ArrayList<>();
		List<String> downloadFileName = new ArrayList<>();
		String dirCode = null;// 目录编号
		for (String id : idsArray) {
			if (StrKit.isBlank(id))
				continue;
			Picture p = pictureService.findById(id);
			if (null != p) {
				paths.add(PropKit.get("upload.dir") + p.getSysName() + "/" + p.getAppNo() + "/" + p.getSubclassSort()
						+ "/" + p.getSavename());
				downloadFileName.add(p.getImgname() + p.getSavename().substring(p.getSavename().lastIndexOf(".")));
				if (null == dirCode)
					dirCode = p.getSubclassSort();
			}
		}
		if (null != dirCode && paths.size() > 0) {
			String rename = null;
			DateTime time = DateTime.now();
			Paperstype p = Paperstype.me.getByItemCode(dirCode);
			if (null != p) {
				rename = p.getItemValue() + "_" + time.toString("yyyyMMddHHmm") + ".zip";
			} else {
				rename = dirCode + "_" + time.toString("yyyyMMddHHmm") + ".zip";
			}
			String path = PropKit.get("upload.dir.cache") + "/" + time.toString("yyyyMMdd") + "/"
					+ UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
			String zipPath = PropKit.get("upload.dir") + path;
			ZipKit.compress(zipPath, paths, downloadFileName);
			String encodedFileName = URLEncoder.encode(rename, "UTF8");
			redirect(PropKit.get("pic.file.view").startsWith("http") ? PropKit.get("pic.file.view")
					: "http:" + PropKit.get("pic.file.view") + path + "?n=" + encodedFileName, false);
		} else {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
		}
	}
}
