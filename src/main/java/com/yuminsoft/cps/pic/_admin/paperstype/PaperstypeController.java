/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:11:22
 *
*/

package com.yuminsoft.cps.pic._admin.paperstype;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.yuminsoft.cps.pic._admin.privilege.PrivilegeService;
import com.yuminsoft.cps.pic.api.picture.PictureService;
import com.yuminsoft.cps.pic.common.controller.BaseController;
import com.yuminsoft.cps.pic.common.model.Paperstype;

/**
 * 目录、文件夹、图片类型 <br/>
 * Date: 2017年3月14日 下午4:11:22 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class PaperstypeController extends BaseController {

	static PaperstypeService service = PaperstypeService.me;
	static PrivilegeService pservice = PrivilegeService.me;
	static PictureService picservice = PictureService.me;
	/**
	 * 查询列表
	 * @Title: index 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:17:42
	 */
	public void index() {
		List<Paperstype> list = service.list();
		setAttr("list", list);
		render("index.html");
	}
	/**
	 * 新增
	 * @Title: add 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:18:03
	 */
	public void add() {
	}
	/**
	 * 新增保存数据
	 * @Title: save 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:18:18
	 */
	private final String RenameIgnore = "[/\\:/*/?\"<>/|]";
	@Before(PaperstypeValidator.class)
	public void save() {
		Paperstype p = getBean(Paperstype.class, "model");
		BigDecimal i = service.findBySortNum(p);
		BigDecimal j = service.findByItemCode(p);
		if (j.compareTo(BigDecimal.ZERO)==1) {
			renderJson(Ret.fail("msg", "证件编号不能重复"));
		}else if (i.compareTo(BigDecimal.ZERO)==1){
			renderJson(Ret.fail("msg", "排序号不能重复"));
		}else if (p.getFileSize().intValue()>50) {
			renderJson(Ret.fail("msg", "附件最大允许设置50M"));
		}else{
		String itemValue = p.getItemValue().replaceAll(RenameIgnore, "");
		boolean bln = p.set("ID", Paperstype.seq).set("ORG", "000000000000").set("ITEM_VALUE",itemValue).save();
		if (bln)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
		}
	}
	/**
	 * 编辑准备数据
	 * @Title: edit 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:18:42
	 */
	public void edit() {
		/*List<Picture> list = service.sysNameList();
		setAttr("list", list);*/
		if (null==getParaToInt().toString()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{
			setAttr("model", Paperstype.me.findById(getParaToInt()));
		}
	}
	/**
	 * 编辑数据更新 
	 * @Title: update 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:18:57
	 */
	@Before(PaperstypeValidator.class)
	public void update() {
		Paperstype p = getBean(Paperstype.class, "model");
		BigDecimal i = service.findBySortNum(p);
		BigDecimal j = service.findByItemCode(p);
		if (i.compareTo(BigDecimal.ZERO)==1) {
			renderJson(Ret.fail("msg", "排序号不能重复"));
		}else if (j.compareTo(BigDecimal.ZERO)==1){
			renderJson(Ret.fail("msg", "证件编号不能重复"));
		}else if (p.getFileSize().intValue()>50) {
			renderJson(Ret.fail("msg", "附件最大允许设置50M"));
		}else{
			String itemValue = p.getItemValue().replaceAll(RenameIgnore, "");
			p.setItemValue(itemValue);
			boolean bln = p.update();
		if (bln)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
		}
	}
	/**
	 * 根据id删除数据
	 * @Title: delete 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:19:38
	 */
	public void delete() {
		if (null==getParaToInt()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{
			pservice.deleteByPaperstypeId(getParaToInt());
			// 删除数据过于危险，屏蔽删除
			// Paperstype p = service.findById(getParaToInt().toString());
			//picservice.deleteByItemCode(p.getItemCode());
			boolean bln = getBean(Paperstype.class, "model").deleteById(getParaToInt());
			if (bln)
				renderJson(Ret.ok());
			else
				renderJson(Ret.fail("msg", "失败"));
		}
	}
	public void countById(){
		if (null==getParaToInt()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{
			BigDecimal i = service.countById(getParaToInt());
			if (i.compareTo(BigDecimal.ZERO)==1)
				renderJson(Ret.ok());
			else
				renderJson(Ret.fail("msg", "失败"));
		}
	}
}
