/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月20日上午9:10:31
 *
*/

package com.yuminsoft.cps.pic.api.picture;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.yuminsoft.cps.pic._admin.syslog.SyslogService;
import com.yuminsoft.cps.pic.api.common.ApiAppNoValidator;
import com.yuminsoft.cps.pic.api.common.ApiController;
import com.yuminsoft.cps.pic.api.common.ApiPlatformValidator;
import com.yuminsoft.cps.pic.common.aop.ValiType;
import com.yuminsoft.cps.pic.common.aop.Validator;
import com.yuminsoft.cps.pic.common.kit.StringKit;
import com.yuminsoft.cps.pic.common.model.Paperstype;
import com.yuminsoft.cps.pic.common.model.Picture;

/**
 * 文件操作相关接口 <br/>
 * Date: 2017年3月20日 上午9:10:31 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
@Before(ApiPlatformValidator.class)
public class PictureController extends ApiController {
	static PictureService service = PictureService.me;
	static SyslogService logService = SyslogService.me;

	/**
	 * 获取文件集合接口
	 * 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 上午9:47:43
	 * @param appNo
	 * @param subclass_sort
	 */
	@Validator(key = "appNo", errormsg = "业务编号[appNo]必填")
	@Validator(key = "subclass_sort", errormsg = "证件类型[subclass_sort]必填")
	public void list() {
		List<Picture> list = service.listByAppNoSort(getPara("appNo"), getPara("subclass_sort"));
		
		logService.saveSyslog("文件列表", "获取文件列表集合");
		renderJson(JSON.toJSONString(Ret.ok("errorcode", "000000").set("result", list)));
	}

	/**
	 * 重命名
	 * 
	 * @Title: rename
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 上午10:51:33
	 */
	private final String RenameIgnore = "[/\\:/*/?\"<>/|]";

	@Validator(key = "value", errormsg = "参数(value)不能为空")
	@Validator(key = "pk", errormsg = "参数(pk)不能为空")
	public void rename() {
		// 验证是否可以重命名
		if (StrKit.notBlank(getPara("closingDate"))
				&& !service.validateClosingDate(getParaToInt("pk"), getPara("closingDate"))) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "文件不可修改!"));
			return;
		}
		Picture p = service.findById(getPara("pk"));
		String imgName = getPara("value").replaceAll(RenameIgnore, "");
		Integer sortsid = null;
		String sortStr = StringKit.getSort(imgName);
		if (null != sortStr) {
			sortsid = Integer.valueOf(sortStr);
		}
		if ("Y".equals(p.getIfPatchBolt())) {
			if (null == sortsid) {
				sortsid = 9999;
			} else {
				sortsid = Integer.valueOf("9999" + sortsid.toString());
			}
		}
		if (null == sortsid) {
			sortsid = 1111;
		}
		int i = service.updateImgName(imgName, sortsid, getPara("pk"));
		Picture newp = service.findById(getPara("pk"));
		if (i > 0) {
			logService.saveSyslog("重命名",
					"将文件(id：" + getPara("pk") + ")'" + p.getImgname() + "'重命名成'" + newp.getImgname() + "'");
			renderJson(Ret.ok("errorcode", "000000").set("rename", imgName));
		} else {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
		}
	}

	/**
	 * 根据ID删除文件表
	 * 
	 * @Title: delete
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 上午10:52:25
	 */
	@Validator(key = "ids", errormsg = "参数(ids)不能为空")
	public void delete() {
		String arrs = getPara("ids");
		String[] ss = arrs.trim().split(",");
		if (ss.length == 0) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "ID不能为空"));
			return;
		}
		for (String id : ss) {
			if (StrKit.isBlank(id)) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "ID不能为空"));
				return;
			}
			// closingDate 截止日（日期时间之前的数据不可以修改）
			if (StrKit.notBlank(getPara("closingDate"))
					&& !service.validateClosingDate(Integer.parseInt(id), getPara("closingDate"))) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "文件不可删除!"));
				return;
			}
		}
		for (String id : ss) {
			service.remove(id);
		}
		renderJson(Ret.ok("errorcode", "000000"));
	}

	@Before(ApiAppNoValidator.class)
	@Deprecated
	public void deleteDirFiles() {
		if (isBlank("subclassSort", "subclassSort不能为空")) {
			return;
		}
		String subclassSort = getPara("subclassSort");
		List<Picture> list = service.listByAppNoSort(getAppNo(), subclassSort);
		if (list == null || list.size() == 0) {
			renderJson(Ret.ok("errorcode", "000000"));
			return;
		}
		for (Picture picture : list) {
			if (null == picture.getId()) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败(ID不存在)"));
				return;
			}
			Integer id = picture.getId().intValue();
			if (StrKit.notBlank(getPara("closingDate")) && !service.validateClosingDate(id, getPara("closingDate"))) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "文件不可删除!"));
				return;
			}
		}
		if (service.deleteBySubclassSort(list)) {
			// 清理缓存
			CacheKit.remove("dirCount", getAppNo() + subclassSort);
			renderJson(Ret.ok("errorcode", "000000"));
		} else {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
		}
	}

	/**
	 * 移动到(根据{id,id}移动到目录id下面，数据库中根据目录编号改变放置的目录值)
	 * 
	 * @Title: move
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月20日 下午4:21:47
	 */
	@Validator(key = "ids", errormsg = "参数(ids)不能为空")
	@Validator(key = "paperstypeId", errormsg = "参数(paperstypeId)不能为空")
	public void move() {
		String arrs = getPara("ids");// 文件表的id集合
		String arr = getPara("paperstypeId");// 目录表
		String[] ss = arrs.split(",");
		Paperstype pt = service.findPaperType(arr);
		// closingDate 截止日（日期时间之前的数据不可以修改）
		for (String id : ss) {
			if (StrKit.notBlank(getPara("closingDate"))
					&& !service.validateClosingDate(Integer.parseInt(id), getPara("closingDate"))) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "文件不可移动!"));
				return;
			}
		}
		Ret ret = service.updatePath(ss, pt);
		if (ret.isOk()) {
			renderJson(Ret.ok("errorcode", "000000"));
		} else if (ret.isFail()) {
			renderJson(ret);
		} else {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
		}
	}

	/**
	 * 文件作废
	 * 
	 * @Title: waste
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月21日 下午2:00:16
	 */
	@Validator(key = "ids", errormsg = "参数(ids)不能为空")
	@Validator(key = "ifWaste", errormsg = "参数(ifWaste)不能为空")
	@Validator(key = "ifWaste", errormsg = "参数(ifWaste)传值错误，只能是Y或者N", in = "Y,N", type = ValiType.IN)
	public void waste() {
		String arrs = getPara("ids");
		String[] ss = arrs.trim().split(",");

		int bln = 0;
		for (int i = 0; i < ss.length; i++) {
			if (StrKit.isBlank(ss[i])) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "格式错误"));
				return;
			}
			Picture p = service.findById(ss[i]);
			if (null == p) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "ID(" + ss[i] + ")值不存在"));
				return;
			}
			Integer sortsid = null;
			String sortStr = StringKit.getSort(p.getImgname());
			if (null != sortStr) {
				sortsid = Integer.valueOf(sortStr);
			}
			if ("Y".equals(getPara("ifWaste"))) {
				if (null == sortsid) {
					sortsid = 666;
				} else {
					sortsid = Integer.valueOf("666" + sortsid.toString());
				}
			}
			if (null == sortsid) {
				sortsid = 1111;
			}
			bln += service.updateWaste(getPara("ifWaste"), ss[i], sortsid);
			if ("Y".equals(getPara("ifWaste"))) {
				logService.saveSyslog("文件作废", "将文件(id：" + ss[i] + ")'" + p.getImgname() + "'作废");
			} else {
				logService.saveSyslog("文件作废", "将文件(id：" + ss[i] + ")'" + p.getImgname() + "'取消作废");
			}
		}
		if (bln == ss.length)
			renderJson(Ret.ok("errorcode", "000000"));
		else
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
	}

	/**
	 * 文件补件
	 * 
	 * @Title: updatePatchBolt
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月21日 下午2:00:16
	 */
	@Validator(key = "ids", errormsg = "参数(ids)不能为空")
	@Validator(key = "ifPatchBolt", errormsg = "参数(ifPatchBolt)不能为空")
	@Validator(key = "ifPatchBolt", errormsg = "参数(ifPatchBolt)传值错误，只能是Y或者N", in = "Y,N", type = ValiType.IN)
	public void patchBolt() {
		String arrs = getPara("ids");
		String[] ss = arrs.trim().split(",");
		for (String id : ss) {
			// closingDate 截止日（日期时间之前的数据不可以修改）
			if (StrKit.notBlank(getPara("closingDate"))
					&& !service.validateClosingDate(Integer.parseInt(id), getPara("closingDate"))) {
				renderJson(Ret.fail("errorcode", "111111").set("errormsg", "文件不可修改!"));
				return;
			}
		}
		int bln = 0;
		for (int i = 0; i < ss.length; i++) {
			Picture p = service.findById(ss[i]);
			bln += service.updatePatchBolt(getPara("ifPatchBolt"), ss[i]);
			if ("Y".equals(getPara("ifPatchBolt"))) {
				logService.saveSyslog("文件作废", "将文件(id：" + ss[i] + ")'" + p.getImgname() + "'补件");
			} else {
				logService.saveSyslog("文件作废", "将文件(id：" + ss[i] + ")'" + p.getImgname() + "'取消补件");
			}
		}
		if (bln == ss.length)
			renderJson(Ret.ok("errorcode", "000000"));
		else
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
	}

	/**
	 * 文件拷贝
	 * 
	 * @Title: updateAppno
	 * @Description: FIXME
	 * @author renyz@yuminsoft.com
	 * @date: 2017年6月6日 上午11:07:02
	 */
	@Before(ApiAppNoValidator.class)
	@Validator(key = "newAppNo", errormsg = "参数(newAppNo)不能为空")
	public void updateAppno() {
		String appNo = getPara("appNo");
		String newAppNo = getPara("newAppNo");
		String subclassSort = getPara("subclassSort");
		if (appNo.equals(newAppNo)) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "参数(appNo)传值不能与参数(newAppNo)一致"));
			return;
		}
		Ret ret = service.updateAppNo(appNo, newAppNo, subclassSort);
		if (ret.isOk()) {
			logService.saveSyslog("文件作废", "更改appNo由" + appNo + "'更改为" + newAppNo);
			renderJson(Ret.ok("errorcode", "000000"));
		} else if (ret.isFail()) {
			renderJson(ret);
		} else {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
		}
	}

	/**
	 * 重新排序接口
	 * 
	 * @Title: updateSortsid
	 * @Description: FIXME
	 * @author renyz@yuminsoft.com
	 * @date: 2017年6月6日 下午5:30:18
	 */
	@Before(ApiAppNoValidator.class)
	@Validator(key = "ids", errormsg = "参数(ids)不能为空")
	@Validator(key = "subclassSort", errormsg = "参数(subclassSort)不能为空")
	public void updateSortsid() {
		String subclassSort = getPara("subclassSort");
		String appNo = getPara("appNo");
		String arrs = getPara("ids");
		if (null == arrs || StrKit.isBlank(arrs.trim()) || 0 == arrs.split(",").length) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "参数(ids)不能为空"));
			return;
		}
		Ret ret = service.updateSortsid(arrs, subclassSort, appNo);
		renderJson(ret);
	}

	/**
	 * 图片旋转接口 接口字段:id:图片id,rotate:left/right(左右旋转90度)
	 */
	@Validator(key = "id", errormsg = "参数(id)不能为空")
	@Validator(key = "rotate", errormsg = "参数(rotate)不能为空")
	public void rotate() {
		String id = getPara("id");
		String rotate = getPara("rotate");
		if ("left".equals(rotate) && "right".equals(rotate)) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "参数值(rotate)错误"));
			return;
		}
		boolean bln = service.rotate(id, rotate);
		if (bln) {
			Picture p = service.findById(id);
			service.working(p);
			renderJson(FastJson.getJson().toJson(Ret.ok("errorcode", "000000").set("result", p)));
		} else
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
	}

	/**
	 * 批量查询接口-根据借款编号和文件类型集合查询文件（如果文件类型为空，则为全查）
	 */
	@Before(ApiAppNoValidator.class)
	public void batchList() {
		renderJson(FastJson.getJson()
				.toJson(Ret.ok("errorcode", "000000").set("result", service.lists(getAppNo(), getPara("sorts")))));
	}

	/**
	 * 批量删除接口-根据借款编号和文件类型批量删除文件
	 */
	@Before(ApiAppNoValidator.class)
	public void batchDelete() {
		Ret ret = service.batchDelete(getAppNo(), getPara("sorts"));
		renderJson(ret);
	}

	/**
	 * 批量作废接口：根据借款编号和文件类型集合批量作废文件
	 */
	@Before(ApiAppNoValidator.class)
	public void batchBlankOut() {
		Ret ret = service.batchBlankOut(getAppNo(), getPara("sorts"));
		renderJson(ret);
	}
}
