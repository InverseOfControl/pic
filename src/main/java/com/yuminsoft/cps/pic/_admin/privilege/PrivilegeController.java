/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:00:46
 *
*/

package com.yuminsoft.cps.pic._admin.privilege;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.yuminsoft.cps.pic.common.controller.BaseController;
import com.yuminsoft.cps.pic.common.model.Paperstype;
import com.yuminsoft.cps.pic.common.model.Privilege;
import com.yuminsoft.cps.pic.common.model.Role;

/**
 * 权限表相关接口
 * @ClassName: PrivilegeController 
 * @Description: TODO
 * @author: renyz@yuminsoft.com
 * @date: 2017年3月27日 上午10:13:37
 */
public class PrivilegeController extends BaseController {
	static PrivilegeService service = PrivilegeService.me;
	/**
	 * 查询列表
	 * @Title: index 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月16日 下午2:01:27
	 */
	public void index() {
		List<Role> flist = service.flist();
		setAttr("flist", flist);
		List<Privilege> alist = service.alllist();
		setAttr("alist", alist);
		render("privilege.html");
	}
	/**
	 * 新增
	 * @Title: save 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午6:00:48
	 */
	public void add() {
		if (StrKit.isBlank(getPara("roleId"))) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(roleId)传值不能为空"));
			return;
		}else{
			List<Paperstype> list = service.itemValueList();
			setAttr("roleId", getPara("roleId"));
			setAttr("list", list);
		}
	}
	/**
	 * 保存方法实现
	 * @Title: save 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:14:06
	 */
	public void save() {
		Paperstype p = getBean(Paperstype.class,"pt");
		Privilege pl =  getBean(Privilege.class, "model");
		if (StrKit.isBlank(getPara("roleId"))) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(roleId)传值不能为空"));
			return;
		}else{
			BigDecimal i = service.countByNode(pl,p.getId(),getPara("roleId"));
			if (i.compareTo(BigDecimal.ZERO)==1) {
				renderJson(Ret.fail("msg", "此环节下目录已存在！"));
			}else{
			boolean bln = pl.set("ID", Privilege.seq).set("PAPERSTYPE_ID", p.getId()).set("ROLE_ID", getPara("roleId")).save();
			if (bln)
				renderJson(Ret.ok());
			else
				renderJson(Ret.fail("msg", "失败"));
			}
		}
	}
	/**
	 * 编辑弹窗数据准备
	 * @Title: edit 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:14:21
	 */
	public void edit() {
		if (null==getParaToInt()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{	
			List<Paperstype> list = service.itemValueList();
			Privilege p = Privilege.me.findById(getParaToInt());
			setAttr("list", list);
			setAttr("model", p);
			setAttr("pt", Paperstype.me.findById(p.getPaperstypeId()));
		}
	}
	/**
	 * 编辑数据更新
	 * @Title: update 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:14:48
	 */
	public void update() {
		Paperstype p = getBean(Paperstype.class,"pt");
		Privilege pl =  getBean(Privilege.class, "model");
		BigDecimal i = service.countByNode(pl,p.getId(),pl.getRoleId().toString());
		boolean bln = pl.set("PAPERSTYPE_ID", p.getId()).update();
		if (i.compareTo(BigDecimal.ZERO)==1) {
			renderJson(Ret.fail("msg", "此环节下目录已存在！"));
		}else{
			if (bln)
				renderJson(Ret.ok());
			else
				renderJson(Ret.fail("msg", "失败"));
		}
	}
	/**
	 * 根据Id删除
	 * @Title: delete 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:15:04
	 */
	public void delete() {
		if (null==getParaToInt()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{	
			boolean bln = getBean(Privilege.class, "model").deleteById(getParaToInt());
			if (bln)
				renderJson(Ret.ok());
			else
				renderJson(Ret.fail("msg", "失败"));
		}
	}
}
