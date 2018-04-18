/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:03:36
 *
*/

package com.yuminsoft.cps.pic._admin.filetag;

import java.util.List;

import com.yuminsoft.cps.pic.common.model.Filetag;
/**
 * ClassName:FiletagService <br/>
 * Date: 2017年3月14日 下午4:03:36 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FiletagService {

	public static final FiletagService me = new FiletagService();
	final static Filetag dao = new Filetag().dao();
	
	public List<Filetag>  getFiletagList(){
		List<Filetag> ftList = dao.find("SELECT * FROM filetag ");
		return ftList;
	}
}
