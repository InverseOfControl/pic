/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:01:32
 *
*/

package com.yuminsoft.cps.pic._admin.filetag;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.yuminsoft.cps.pic.common.controller.BaseController;
import com.yuminsoft.cps.pic.common.model.Filetag;

/**
 * ClassName:FiletagController <br/>
 * Date:     2017年3月14日 下午4:01:32 <br/>
 * @author   gaojf@yuminsoft.com
 */
public class FiletagController extends BaseController {
	private FiletagService service = FiletagService.me;
	/**
	 * 标签查询
	 * @Title: index 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午5:53:34
	 */
	public void index(){
		setAttr("list",service.getFiletagList());
		render("filetag.html");
	}
	/**
	 * 新增
	 * @Title: save 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午6:00:48
	 */
	public void add() {
	}
	@Before(FiletagValidator.class)
	public void save() {
		boolean bln = getBean(Filetag.class, "model").set("ID", Filetag.seq).save();
		if (bln)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
	}
	/**
	 * 编辑
	 * @Title: update 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午6:00:58
	 */
	public void edit() {
		setAttr("model", Filetag.me.findById(getParaToInt()));
	}
	@Before(FiletagValidator.class)
	public void update() {
		boolean bln = getBean(Filetag.class, "model").update();
		if (bln)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
	}
	/**
	 * 删除
	 * @Title: delete 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午6:01:07
	 */
	public void delete() {
		boolean bln = getBean(Filetag.class, "model").deleteById(getParaToInt());
		if (bln)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
	}
}
