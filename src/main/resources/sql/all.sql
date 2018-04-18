#-- 
   Api SQL 
--#

### 命名空间，调用时需添加命名空间，比如 dao.getSql("api.paperstypeList")

#namespace("api")
  #include("api.sql")
#end

#namespace("syslog")
  #include("syslog.sql")
#end