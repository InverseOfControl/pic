### 获取 业务环节内的目录
#sql("page")
    SELECT * FROM SYSLOG l 
    WHERE 1=1
	    #if(null != operator && ""!=operator)
	      AND l."OPERATOR"=#para(operator)
	    #end
	    #if(null != jobNumber && ""!=jobNumber)
	      AND l."JOB_NUMBER"=#para(jobNumber)
	    #end
	    #if(null != operationType && ""!=operationType)
	      AND l."OPERATION_TYPE"=#para(operationType)
	    #end
	    #if(null != operationStartTime && ""!=operationStartTime)
	     AND to_char(l.OPERATION_TIME,'yyyy-mm-dd')>=#para(operationStartTime)
	    #end
	    #if(null != operationEndTime && ""!=operationEndTime)
	     AND to_char(l.OPERATION_TIME,'yyyy-mm-dd')<=#para(operationEndTime)
	    #end
    ORDER BY
    	l."ID" DESC
#end