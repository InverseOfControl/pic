### 获取 业务环节内的目录
#sql("paperstypeList")
	select pt.id "id",
       pt.item_code "code",
       pt.item_value "name",
       pt.file_size "fileSize",
       pt.file_number "fileNumber",
       pt.file_type "fileType",
       pt.sortnum "sortNum",
       (select max(sortnum) from paperstype)  "maxSortNum",
       p.operate_add "operateAdd",
       p.operate_move "operateMove",
       p.operate_remove "operateRemove",
       p.operate_download "operateDownload",
       p.operate_rename "operateRename",
       p.operate_waste "operateWaste",
       p.operate_patch_bolt "operatePatchBolt",
       p.operate_dragsort "operateDragSort",
       (select count(0) from picture pp where  pp.subclass_sort = pt.item_code and PP.APP_NO = ?) "fileAmount",
	   (select count(0) from picture pp where  pp.subclass_sort = pt.item_code and PP.APP_NO = ? and pp.if_waste = 'Y') "wasteAmount",
	   (select count(0) from picture pp where  pp.subclass_sort = pt.item_code and PP.APP_NO = ? and pp.if_patch_bolt = 'Y') "patchBoltAmount"
	from role r
	  left join privilege p on r.id=p.role_id
	  left join paperstype pt on pt.id = p.paperstype_id
	where r.node_key = ?
	order by pt.sortnum
#end
  
###获取图片列表
#sql("pictureList")
	select t.id,
       t.imgname,
       t.savename,
       t.subclass_sort,
       to_char(t.uptime, 'yyyy-MM-dd HH24:mi:ss') "UPTIME",
       t.app_no,
       t.sys_name,
       t.if_waste,
       t.if_patch_bolt,
       t.creator,
       t.create_jobnum,
       to_char(t.create_time, 'yyyy-MM-dd HH24:mi:ss')  "CREATE_TIME",
       t.modifier,
       t.modified_jobnum,
       to_char(t.modifier_time, 'yyyy-MM-dd HH24:mi:ss')     "MODIFIER_TIME",
	   t.sortsid,
	   t.version
	from picture t
	where t.app_no = ? and t.subclass_sort = ? 
	order by t.sortsid 
#end 
  
###获取按钮是否显示
#sql("privilegeList")
	select decode(sign(sum(t.operate_add)), 0, 0, 1, 1, -1, 0) "operateAdd",
       decode(sign(sum(t.operate_move)), 0, 0, 1, 1, -1, 0) "operateMove",
       decode(sign(sum(t.operate_remove)), 0, 0, 1, 1, -1, 0) "operateRemove",
       decode(sign(sum(t.operate_download)), 0, 0, 1, 1, -1, 0) "operateDownload",
       decode(sign(sum(t.operate_rename)), 0, 0, 1, 1, -1, 0) "operateRename",
       decode(sign(sum(t.operate_waste)), 0, 0, 1, 1, -1, 0) "operateWaste",
       decode(sign(sum(t.operate_patch_bolt)), 0, 0, 1, 1, -1, 0) "operatePatchBolt",
       decode(sign(sum(t.operate_dragsort)), 0, 0, 1, 1, -1, 0) "operateDragSort"
	 from PRIVILEGE t
	 where role_id in (SELECT id FROM role r where r.node_key = ?)
#end
   
###获取环节对应的权限
#sql("permissionList")
   	select p.operate_add||p.operate_move||p.operate_remove||p.operate_download||p.operate_rename||p.operate_waste||p.operate_patch_bolt||p.operate_dragsort "permission",
   		r.id,r.node_key,t.id,t.item_code "item_code" 
   	from PRIVILEGE p 
		left join ROLE r on p.role_id=r.id 
		left join PAPERSTYPE t on p.paperstype_id=t.id
	where r.node_key= ?
#end
 	
### 批量获取文件集合
#sql("pictureLists")
   	select t.id,
       t.imgname,
       t.savename,
       t.subclass_sort,
       to_char(t.uptime, 'yyyy-MM-dd HH24:mi:ss') "UPTIME",
       t.app_no,
       t.sys_name,
       t.if_waste,
       t.if_patch_bolt,
       t.creator,
       t.create_jobnum,
       to_char(t.create_time, 'yyyy-MM-dd HH24:mi:ss')  "CREATE_TIME",
       t.modifier,
       t.modified_jobnum,
       to_char(t.modifier_time, 'yyyy-MM-dd HH24:mi:ss')     "MODIFIER_TIME",
       t.sortsid,
       t.version
   	from picture t
   	where t.app_no = #para(appNo)
   	   #if(null!=sorts)
   	       and t.subclass_sort in(
   	          #for(sort : sorts)
   	             #(for.index>0 ? "," : "") #para(sort)
   	          #end
   	       )
   	   #end
   	order by t.sortsid 
#end 