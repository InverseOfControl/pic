(function(global) {
	function sendRequest(url,params, fn){
		$.post(ctx+url, params, function(result) {
			if (fn) {
				fn(result);
			}
		});
	}
	var API = {
		getPaperstypeList : function(params, fn) {
			sendRequest("/api/paperstype/list?random="+Math.random().toString().substring(2),params, fn);
		},
		pictureUpdateSortsid:function(params, fn){
			sendRequest("/api/picture/updateSortsid",params, fn);
		},
		getPictureList:function(params, fn){
			sendRequest("/api/picture/list?random="+Math.random().toString().substring(2),params, fn);
		},
		pictureRotate:function(params, fn){
			sendRequest("/api/picture/rotate",params, fn);
		},
		pictureRename:function(params, fn){
			sendRequest("/api/picture/rename",params, fn);
		},
		picturePatchBolt:function(params, fn){
			sendRequest("/api/picture/patchBolt",params, fn);
		},
		pictureWaste:function(params, fn){
			sendRequest("/api/picture/waste",params, fn);
		},
		pictureMove:function(params, fn){
			sendRequest("/api/picture/move",params, fn);
		},
		pictureDelete:function(params, fn){
			sendRequest("/api/picture/delete",params, fn);
		},
		downLoad:function(url){
			window.open(ctx + '/api/filedata/download/' + url);
		},
		uploadFile:function(params){
			var xhr = new XMLHttpRequest();
			// 上传进度
			xhr.upload.addEventListener("progress", params.progress||function(event) {
			}, false);
			xhr.addEventListener("load", params.load||function(event) {
			}, false);
			xhr.addEventListener("error", params.error||function(event){
			}, false);
			xhr.addEventListener("abort", params.abort||function(event){
			}, false);
			xhr.open("POST", ctx + "/api/filedata/upload" +(params.data?"?"+$.param(params.data):""),true);// 修改成自己的接口
			xhr.send(params.formData);
		}
	};
	"function"==typeof define&&define.amd&&define("api",[],function(){return API});
	global.API=API;
})(this);
