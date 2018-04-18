var cacheUrl = 'sysName=' + requestData.sysName + '&nodeKey=' + requestData.nodeKey + '&appNo=' + requestData.appNo + '&operator=' + requestData.operator + '&jobNumber='
		+ requestData.jobNumber + '&ifPatchBolt=' + requestData.ifPatchBolt;
// PIC JS
$(function() {
	// 滚动条
	$('#pic-side').perfectScrollbar();
	$('#pic-body').perfectScrollbar();

	var dirs;// 目录集合
	var paperstype; // 当前选择的目录
	var paperstypeIndex = 0; // 当前选择的目录Index
	var selectedNodeArray = new Array(); // 已选择文件
	var selectedOldNodeArray = new Array(); // 已选择文件
	var fileArray = new Array(); // 当前列表所有文件
	var selectFirst = true;
	var rotate = true;
	var templates = {
		picDirItem : $.templates("#picDirItemTmpl"),
		checkboxItem : $.templates("#checkboxTmpl"),
		picItem : $.templates("#picItemTmpl"),
		uploadFileItem : $.templates("#uploadFileItemTmpl"),
		uploadImgItem : $.templates("#uploadImgItemTmpl")
	};

	// 拖动排序
	var sortable;
	function initSortable() {
		sortable && sortable.destroy();
		sortable = null;
		$('#file-grid').unbind("dragend");
		if (!permission.checkPermission(paperstype && paperstype.code, 7)) {
			return;
		}
		sortable = new Sortable($('#file-grid').get(0));
		$('#file-grid').on("dragend", function(event) {
			initViewer();
			var params = $.extend({}, requestData);
			params.subclassSort = paperstype.code;
			var ids = [];
			$('#file-grid :checkbox').each(function() {
				ids.push($(this).val());
			});
			params.ids = ids.join(',');
			var index = layer.load();
			API.pictureUpdateSortsid(params, function(data) {
				if (!data.isOk) {
					layer.msg(data.errormsg);
				} else {
					selectFirst = false;
					loadingFiles();
				}
				layer.close(index);
			});
		});
	}

	// 显示所有
	$('button[data-action=query-all]').click(function() {
		$('#pic-dir li').each(function() {
			$(this).show();
		});
	});
	// 显示存在附件的目录
	$('button[data-action=query]').click(function() {
		$('#pic-dir li').each(function() {
			if ($(this).data('number') > 0) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	});
	// 显示没有附件的目录
	$('button[data-action=query-empty]').click(function() {
		$('#pic-dir li').each(function() {
			if ($(this).data('number') == 0) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	});
	// 通过 Code 获取 Dir
	function getDir(code) {
		var result = null;
		for (x in dirs) {
			if (dirs[x].code == code) {
				result = dirs[x];
				break;
			}
		}
		return result;
	}
	loadingDir();
	// 加载目录
	function loadingDir() {
		rotate = true;
		API.getPaperstypeList({}, function(data) {
			if (data.isOk) {
				var result = data.result;
				var $dir = $('#pic-dir');
				$dir.empty();
				var renderData = [];
				var isAlert = true;
				for (x in result) {
					var itemData = $.extend({}, result[x]);
					itemData.cname = itemData.name + '(' + itemData.fileAmount + '/' + itemData.patchBoltAmount + ')';
					if (paperstype && result[x].id == paperstype.id) {
						itemData.active = "active";
					}
					// 样式
					if (result[x].operateAdd == 0 && view != 'Y') {
						itemData.icon = '<i style="font-size: 12px;color: #000;" class="iconfont icon-zhongzhizhihang"></i>';
					} else {
						itemData.icon = '<i style="font-size: 12px;color: #000;" class="iconfont icon-upload"></i>';
					}
					if (result[x].fileAmount > 0)
						isAlert = false;
					renderData.push(itemData);
				}
				$dir.append(templates.picDirItem.render(renderData));
				$("#moveWindowModal>ul").html(templates.checkboxItem.render(renderData));
				dirs = result;
				dirs.sort(function(a, b) {
					return a.name.length <= b.name.length;
				});

				if (operatorUpload != 1) {
					$('button[data-action=query]').click();
					if (isAlert) {
						layer.msg('未上传附件！', {
							// 不自动关闭
							time : 0
						});
					}
				}
			} else {
				layer.msg('文件夹加载失败，' + data.errormsg);
			}
		});
	}
	// 上传权限

	/**
	 * 监听
	 */
	var regxImage = new RegExp(/\.(bmp|jpg|png|gif|svg|jpeg)$/i);
	// 目录点击事件
	$('.pic-dir-item').livequery('click', function() {
		$('.pic-dir-item').removeClass('active');
		$(this).addClass('active');

		var index = layer.load();
		// 隐藏操作按钮
		$('.pic-tool .pic-btn:not(.pic-btn-upload)').hide().attr('disabled', "true");

		// 加载列表文件
		var code = $(this).attr('data-code');
		if (code) {
			paperstype = getDir(code);
			selectFirst = true;
			loadingFiles();
		}
		// 清空已选文件
		selectedNodeArray.splice(0, selectedNodeArray.length);
		if (paperstype.fileAmount > 0) {
			// 按钮
			if (paperstype.operateRemove == 1)
				$('.pic-btn-remove').show();
			if (paperstype.operateDownload == 1)
				$('.pic-btn-down').show();
			if (paperstype.operateMove == 1)
				$('.pic-btn-move').show();
			if (paperstype.operateWaste == 1)
				$('.pic-btn-invalid').show();
			if (paperstype.operatePatchBolt == 1)
				$('.pic-btn-goback').show();
		}
		layer.close(index);
	});
	// PDF
	$('.fileicon-large-pdf').livequery('click', function() {
		var value = $(this).data('value')
		layer.open({
			type : 2,
			title : false,
			area : [ '95%', '95%' ],
			shade : 0.8,
			closeBtn : 1,
			shadeClose : true,
			content : ctx + '/api/filedata/pdfView?id=' + value + '&operator=' + requestData.operator + '&jobNumber=' + requestData.jobNumber
		});
	});
	// 载入列表文件
	function loadingFiles() {
		if (paperstype && paperstype.code) {
			initSortable();
			API.getPictureList({
				subclass_sort : paperstype.code
			}, function(data) {
				var $div = $('#file-grid');
				// 清理
				$div.empty();
				layer.closeAll();
				if (data.isOk) {
					var result = data.result;
					fileArray = data.result;
					var renderData = [];
					if (data.result.length == 0) {
						layer.msg('目录为空');
					}
					for (x in result) {
						var itemData = $.extend({}, result[x]);
						itemData.name = itemData.imgname;
						itemData.value = itemData.id;
						var $tags = '';
						if (itemData.ifWaste == 'Y') {
							$tags = '作废';
						}
						if (itemData.ifPatchBolt == 'Y') {
							$tags += ' 补件'
						}
						// $tags += ' ' + itemData.sortsid;
						itemData.tags = $tags;
						if (itemData.savename.match(regxImage)) {
							// 图片
							itemData.isImg = true;
							itemData.thumsrc = itemData.thumUrl;
							itemData.src = ctx + '/assets/img/time-line-bg.gif';
							// itemData.originalPath = itemData.originalPath;
						} else {
							// 文件
							itemData.icon = icon(itemData.savename);
						}
						renderData.push(itemData);
					}
					$div.append(templates.picItem.render(renderData));
					initViewer();
					// 重命名
					if (1 == paperstype.operateRename && view) {
						rename();
					}
					// 异步加载
					var array = new Array();
					$div.find('img[data-thumsrc]').each(function() {
						// 压缩
						var that = $(this);
						array.push(that);
					});
					nextLoadingImage(0, array, paperstype.id);
				} else {
					layer.msg(data.errormsg);
				}
			});
		}
	}
	// 顺序加载图片，避免大图加载到页面
	function nextLoadingImage(i, array, paperstypeId) {
		if (paperstypeId != paperstype.id) {
			// 强制终止加载
			return;
		}
		if (i >= array.length) {
			// 已加载完毕
			return;
		}
		var that = array[i];
		// 如果是旧图片禁止绘制缩略图
		if (that.data("oldid")) {
			that.attr('src', that.data('thumsrc'));
			nextLoadingImage(i + 1, array, paperstypeId);
			return;
		}
		var image = new Image();
		// image.crossOrigin = "anonymous"; // 解决跨域问题 部分请求失败
		image.src = that.data('thumsrc');
		// var index = layer.tips('加载中...', that, {
		// tips : [ 3, '#3595CC' ],
		// zIndex:-10,
		// time : 0
		// });
		if (image.complete) { // 如果图片已经存在于浏览器缓存，直接调用回调函数
			// layer.close(index);
			nextLoadingImage(i + 1, array, paperstypeId);
			// 接受 jpeg, jpg, png 类型的图片
			if (/\.(bmp|jpg|png|jpeg)$/i.test(that.data('savename'))) {
				if (paperstypeId != paperstype.id) {
					// 强制终止加载
					return;
				}
				// that.attr('src', resize(image, 84, 84));
				that.attr('src', image.src);
			} else {
				that.attr('src', image.src);
			}
			return; // 直接返回，不用再处理onload事件
		}
		image.onload = function() {
			// layer.close(index);
			nextLoadingImage(i + 1, array, paperstypeId);
			// 接受 jpeg, jpg, png 类型的图片
			if (/\.(bmp|jpg|png|jpeg)$/i.test(that.data('savename'))) {
				if (paperstypeId != paperstype.id) {
					// 强制终止加载
					return;
				}
				// that.attr('src', resize(image, 84, 84));
				// 生成缩略图存在跨域问题 Tomcat 转发静态地址存在问题
				that.attr('src', image.src);
			} else {
				that.attr('src', image.src);
			}
		}
		image.onabort = function() {
			nextLoadingImage(i + 1, array, paperstypeId);
		}
		image.onerror = function() {
			nextLoadingImage(i + 1, array, paperstypeId);
		}

	}
	function resize(image, width, height) {
		var canvas = document.createElement("canvas");
		var ctx = canvas.getContext("2d");
		var cw = image.width;
		var ch = image.height;
		var w = image.width;
		var h = image.height;
		canvas.width = w;
		canvas.height = h;
		console.log(canvas)
		if (cw > 84 && cw > ch) {
			w = 84;
			h = (84 * ch) / cw;
			canvas.width = w;
			canvas.height = h;
		}
		if (ch > 84 && ch > cw) {
			h = 84;
			w = (84 * cw) / ch;
			canvas.width = w;
			canvas.height = h;
		}
		ctx.drawImage(image, 0, 0, w, h);

		// 取出 base64 格式数据
		var base64data = canvas.toDataURL();
		canvas = ctx = null;
		return base64data;
	}
	var regxGif = new RegExp(/\.(gif)$/i);
	function initViewer() {
		$('#file-grid').viewer('destroy');
		$('#file-grid').viewer({
			navbar : false,
			scalable : false,
			fullscreen : false,
			rotateLeft : function(e) {
				// GIF动图禁止旋转
				var url = e.data.data('original-url')
				if (url && url.match(regxGif)) {
					layer.msg('动态图片不允许旋转');
					return;
				}
				// 未返回旋转结果前禁止再次旋转
				if (!rotate) {
					return;
				}
				rotate = false;
				var index = layer.load();
				var id = e.data.data('value');
				API.pictureRotate({
					id : id,
					rotate : 'left'
				}, function(data) {
					layer.close(index);
					if (data.isOk) {
						// 更新本地图片
						updateCacheImg(data.result);
					} else {
						layer.msg('图片失败');
					}
					rotate = true;
				});
			},
			rotateRight : function(e) {
				// GIF动图禁止旋转
				var url = e.data.data('original-url')
				if (url && url.match(regxGif)) {
					layer.msg('动态图片不允许旋转');
					return;
				}
				// 未返回旋转结果前禁止再次旋转
				if (!rotate) {
					return;
				}
				rotate = false;
				var index = layer.load();
				var id = e.data.data('value');
				API.pictureRotate({
					id : id,
					rotate : 'right'
				}, function(data) {
					layer.close(index);
					if (data.isOk) {
						// 更新本地图片
						updateCacheImg(data.result);
					} else {
						layer.msg('图片失败');
					}
					rotate = true;
				});
			},
			hide : function() {
				// 强制刷新当前目录文件
				selectFirst = false;
			},
			prev : function(that) {
				var data = that.data;
				// 触发加载上一个有附件的文件夹
				if (data.prevIndex == data.index) {
					$('#pic-dir li.active').prevAll().each(function() {
						if (parseInt($(this).data('number')) > 0) {
							$(this).trigger('click');
							return false;
						}
						return true;
					});
				}
			},
			next : function(that) {
				var data = that.data;
				// 触发加载下一个有附件的文件夹
				if (data.prevIndex == data.index) {
					$('#pic-dir li.active').nextAll().each(function() {
						if (parseInt($(this).data('number')) > 0) {
							$(this).trigger('click');
							return false;
						}
						return true;
					});
				}
			}
		});
		// 第一张图片 第一张图片加载成功模拟点击
		if (selectFirst) {
			$('#file-grid').find('img:first').load(function() {
				$(this).trigger('click');
				selectFirst = false;
			});
		}
	}
	// 更新缓存图片
	function updateCacheImg(obj) {
		var img = $('#' + obj.id);
		// 小缩略图
		img.data('url', obj.url);
		// 原图
		img.attr('src', obj.thumUrl);
		img.attr('thumsrc', obj.thumUrl);
		$('#file-grid').viewer('update');
	}
	// 列表文件点击事件
	$('.pic-item').livequery('click', function() {
		var parent = $(this).parents('.pic-grid-view-item');
		var val = $(this).val();
		var oldid = $(this).data('oldid');
		if ($(this).is(":checked")) {
			// 选中
			parent.addClass('active');
			if (!oldid)
				selectedNodeArray.push(val);
			else
				selectedOldNodeArray.push(val);
		} else {
			// 未选
			parent.removeClass('active');
			if (oldid)
				selectedOldNodeArray.splice($.inArray(val, selectedOldNodeArray), 1);
			else
				selectedNodeArray.splice($.inArray(val, selectedNodeArray), 1);
		}
		// 切换按钮
		if (selectedNodeArray.length == 1) {
			triggerBtn('.pic-btn-remove', paperstype.operateRemove);
			triggerBtn('.pic-btn-down', paperstype.operateDownload);
			triggerBtn('.pic-btn-move', paperstype.operateMove);
			triggerBtn('.pic-btn-invalid', paperstype.operateWaste);
			triggerBtn('.pic-btn-goback', paperstype.operatePatchBolt);
		} else if (selectedNodeArray.length > 1) {
			triggerBtn('.pic-btn-remove', paperstype.operateRemove);
			triggerBtn('.pic-btn-down', paperstype.operateDownload);
			triggerBtn('.pic-btn-move', paperstype.operateMove);
			triggerBtn('.pic-btn-invalid', paperstype.operateWaste);
			triggerBtn('.pic-btn-goback', paperstype.operatePatchBolt);
		} else {
			triggerBtn('.pic-btn-remove', 0);
			triggerBtn('.pic-btn-down', 0);
			triggerBtn('.pic-btn-move', 0);
			triggerBtn('.pic-btn-invalid', 0);
			triggerBtn('.pic-btn-goback', 0);
		}
	});

	// 按钮操作切换
	function triggerBtn(clazz, operate) {
		if (view == 'Y')
			return;
		if (1 == operate) {
			$(clazz).removeAttr("disabled");
		} else {
			$(clazz).attr('disabled', "true");
		}
	}

	// 重命名
	$.fn.editable.defaults.mode = 'popup';
	$.fn.editableform.buttons = $("#reNameBtns");
	function rename() {
		$('.filename').editable({
			type : 'text',
			validate : function(value) {
				if ($.trim(value) == '') {
					return '必填';
				}
			},
			success : function(data, newValue) {
				API.pictureRename({
					value : newValue,
					pk : $(this).data().pk
				}, function(data) {
					if (data.isOk) {
						layer.msg('修改成功!');
						renameAfter($(this).attr('data-pk'), data.rename);
						refresh();
						return true;
					} else {
						layer.msg(data.errormsg);
						return false;
					}
				});
			}
		});
	}

	function renameAfter(id, name) {
		setTimeout(function() {
			$('a[data-pk=' + id + ']').attr('title', name).text(name);
		}, 1000);
	}

	// 弹出上传窗口
	$('.pic-btn-upload').click(function() {
		$("#uploadWindowModal>.pic-grid-view").empty();
		$('#file').val('');
		uploadfiles.splice(0, uploadfiles.length);
		layer.open({
			title : '附件上传',
			type : 1,
			skin : 'layui-layer-rim', // 加上边框
			area : [ '90%', '80%' ], // 宽高
			btn : [ '全部清空', '取消' ],
			content : $("#uploadWindowModal"),
			yes : function() {
				$("#uploadWindowModal>.pic-grid-view").empty();
				$('#file').val('');
				uploadfiles.splice(0, uploadfiles.length);
			},
			btn2 : function(index) {
				layer.close(index);
				$('#file').val('');
				uploadfiles.splice(0, uploadfiles.length);
				refresh();
			},
			cancel : function(index) {
				layer.close(index);
				$('#file').val('');
				uploadfiles.splice(0, uploadfiles.length);
				refresh();
			}
		});
	});
	// 上传按钮
	$('#file').livequery('change', function() {
		var files = $(this).prop('files');
		if (files.length == 0) {
		} else {
			for (var x = 0, xlen = this.files.length; x < xlen; x++) {
				setTimeout(loadFile(files[x], files[x].name, xlen), 1000);
			}
		}
	});

	// 选择的文件添加到待上传窗口内
	var maxCanvaxSize = 3145728;
	function loadFile(file, fileName) {
		var uuid = Math.uuid(36);
		file.uuid = uuid;
		var error = '';
		var bln = true;
		// 最大文件大小
		if (file.size > maxPostSize) {
			bln = false;
			error = '文件太大';
		}
		// 目录检查
		for (var x = 0, xlen = dirs.length; x < xlen; x++) {
			if (fileName.startWith(dirs[x].name)) {
				// ...
				break;
			}
			if ('其他' == (dirs[x].name)) {
				break;
			}
			if (x == (xlen - 1)) {
				bln = false;
				error = '未找到匹配的文件夹';
			}
		}
		// ...
		// 文件格式
		// ...
		// 目录数量
		if (bln) {
			uploadfiles.push(file);
		}
		var data = {
			uuid : uuid,
			error : error,
			name : fileName
		};
		if (file.type.startWith('image')) {
			// 如果图片大于3M，不显示缩略图
			if (maxCanvaxSize < file.size) {
				data.src = ctx + '/assets/img/time-line-bg.gif';
				$("#uploadWindowModal>.pic-grid-view").append(templates.uploadImgItem.render(data));
				return;
			}
			var reader = new FileReader();
			reader.onload = function(e) {
				// 压缩
				var canvas = document.createElement("canvas");
				var ctx = canvas.getContext("2d");
				var image = new Image();
				image.src = this.result;
				image.onload = function() {
					var base64data = null;
					// 接受 jpeg, jpg, png 类型的图片
					if (/\/(?:jpeg|jpg|png)/i.test(file.type)) {
						var cw = image.width;
						var ch = image.height;
						var w = image.width;
						var h = image.height;
						canvas.width = w;
						canvas.height = h;
						if (cw > 84 && cw > ch) {
							w = 84;
							h = (84 * ch) / cw;
							canvas.width = w;
							canvas.height = h;
						}
						if (ch > 84 && ch > cw) {
							h = 84;
							w = (84 * cw) / ch;
							canvas.width = w;
							canvas.height = h;
						}
						ctx.drawImage(image, 0, 0, w, h);

						// 取出 base64 格式数据
						base64data = canvas.toDataURL(file.type, 0.75);
						canvas = ctx = null;
					} else {
						base64data = e.target.result;
					}
					data.src = base64data;
					$("#uploadWindowModal>.pic-grid-view").append(templates.uploadImgItem.render(data));
				}
			};
			reader.readAsDataURL(file);
		} else {
			data.fileiconLarge = icon(fileName);
			$("#uploadWindowModal>.pic-grid-view").append(templates.uploadFileItem.render(data));
		}
	}

	function compress(img, fileType) {
		var canvas = document.createElement("canvas");
		var ctx = canvas.getContext('2d');

		var width = img.width;
		var height = img.height;

		canvas.width = width;
		canvas.height = height;

		ctx.fillStyle = "#fff";
		ctx.fillRect(0, 0, canvas.width, canvas.height);
		ctx.drawImage(img, 0, 0, width, height);

		// 压缩
		var base64data = canvas.toDataURL(fileType, 0.75);
		canvas = ctx = null;

		return base64data;
	}

	var uploadfiles = new Array();
	// 限制上传个数,限制并发量
	var uploadFileSize = 2;
	// 终止上传
	var isUpload = true;
	var uploadLayerIndex = 0;
	// 全部上传
	$('.pic-btn-allupload').livequery('click', function() {
		if (uploadfiles.length == 0) {
		} else {
			isUpload = true;
			uploadLayerIndex = layer.load();
			for (var x = 0; x < uploadFileSize; x++) {
				setTimeout(uploadFile(), 1000);
			}
		}
	});
	// 文件上传
	function uploadFile() {
		if (!isUpload) {
			layer.close(uploadLayerIndex);
			return;
		}
		if (uploadfiles.length == 0) {
			return;
		}
		var file = uploadfiles.shift();
		$div = $('div[data-uuid="' + file.uuid + '"]');
		if ($div.find('.pic-progress').length == 0)
			$div.append('<div class="pic-progress"><span style="width: 0%;"><span></span></span></div>');
		var fd = new FormData();
		fd.append("fileToUpload", file);
		API.uploadFile({
			formData : fd,
			data : $.extend({}, requestData, {
				dataSources : 1
			}),
			progress : function(event) {
				uploadProgress(event, file.uuid);
			},
			load : function(event) {
				uploadComplete(event, file.uuid);
				if (uploadfiles.length == 0) {
					layer.close(uploadLayerIndex);
				} else {
					setTimeout(uploadFile(), 1000);
				}
			},
			error : function(event) {
				setTimeout(uploadFile(), 1000);
				if (uploadfiles.length == 0) {
					layer.close(uploadLayerIndex);
				}
				alert("There was an error attempting to upload the file.");
			},
			abort : function(event) {
				setTimeout(uploadFile(), 1000);
				if (uploadfiles.length == 0) {
					layer.close(uploadLayerIndex);
				}
				alert("The upload has been canceled by the user or the browser dropped the connection.");
			}
		});
	}
	// 进度条
	function uploadProgress(evt, uuid) {
		$div = $('div[data-uuid="' + uuid + '"]');
		$span = $div.find('.pic-progress').find('span');
		if (evt.lengthComputable) {
			var percentComplete = Math.round(evt.loaded * 100 / evt.total);
			$span.css('width', percentComplete.toString() + '%');
			$span.text(percentComplete.toString() + '%');
		} else {
			$span.text('unable to compute');
		}
	}
	function uploadComplete(evt, uuid) {
		/* 服务器端返回响应时候触发event事件 */
		var data = JSON.parse(evt.target.responseText);
		$div = $('div[data-uuid="' + uuid + '"]');
		$span = $div.find('.pic-progress').find('span');
		if (data.isOk) {
			$span.text('上传成功 ');
		} else {
			$span.text(data.errormsg);
		}
	}

	// 获取文件图标
	function icon(fileName) {
		var name = fileName.toLowerCase();
		var result = 'fileicon-large-misc';
		if (name.endWith('.pdf')) {
			result = 'fileicon-large-pdf';
		} else if (name.endWith('.zip')) {
			result = 'fileicon-large-zip';
		} else if (name.endWith('.docx') || name.endWith('.doc')) {
			result = 'fileicon-large-word';
		} else if (name.endWith('.xlsx') || name.endWith('.xls')) {
			result = 'fileicon-large-excel';
		}
		return result;
	}
	function refresh() {
		// 刷新目录
		loadingDir();
		// 刷新文件列表
		loadingFiles();
		// 清空已选
		selectedNodeArray.splice(0, selectedNodeArray.length);
		selectedOldNodeArray.splice(0, selectedOldNodeArray.length);
		// 按钮
		triggerBtn('.pic-btn-remove', 0);
		triggerBtn('.pic-btn-down', 0);
		triggerBtn('.pic-btn-move', 0);
		triggerBtn('.pic-btn-invalid', 0);
		triggerBtn('.pic-btn-goback', 0);
	}
	/**
	 * 删除
	 */
	$('.pic-btn-remove').click(function() {
		layer.msg('确定删除？', {
			time : 0,
			btn : [ '确定', '取消' ],
			yes : function(index) {
				layer.close(index);
				API.pictureDelete({
					ids : selectedNodeArray.join()
				}, function(data) {
					if (data.isOk) {
						layer.msg('成功');
						refresh();
					} else {
						layer.msg(data.errormsg);
					}
				});
			}
		});
	});
	/**
	 * 下载
	 */
	$('.pic-btn-down').click(function() {
		if (selectedNodeArray.length == 1) {
			for (i in fileArray) {
				if (fileArray[i].id == selectedNodeArray[0]) {
					$.fileDownload(fileArray[i].url + '?n=' + fileArray[i].name);
					break;
				}
			}
		} else
			$.fileDownload(ctx + '/api/filedata/batchDownload?ids=' + selectedNodeArray.join());
	});
	/**
	 * 移动到
	 */
	$('.pic-btn-move').click(function() {
		layer.open({
			type : 1,
			title : false,
			skin : 'layui-layer-rim', // 加上边框
			content : $("#moveWindowModal"),
			btn : [ '确定', '取消' ],
			yes : function(index) {
				var val = $('input:radio[name="pic-checkbox"]:checked').val();
				if (val) {
					layer.close(index);
					var index = layer.load();
					API.pictureMove({
						ids : selectedNodeArray.join(),
						paperstypeId : val
					}, function(data) {
						layer.close(index);
						if (data.isOk) {
							layer.msg('成功');
							refresh();
						} else {
							layer.msg(data.errormsg);
						}
					});
				} else {
					layer.msg('请先选择文件夹！');
				}
			}
		});
	});
	/**
	 * 作废
	 */
	$('.pic-btn-invalid').click(function() {
		layer.confirm('作废与取消作废.', {
			btn : [ '作废', '取消作废', '取消' ],
			title : false
		}, function() {
			pictureWaste({
				ids : selectedNodeArray.join(),
				ifWaste : 'Y'
			});
		}, function() {
			pictureWaste({
				ids : selectedNodeArray.join(),
				ifWaste : 'N'
			});
		});
	});
	function pictureWaste(params) {
		var index = layer.load();
		API.pictureWaste(params, function(data) {
			layer.close(index);
			if (data.isOk) {
				layer.msg('成功');
				refresh();
			} else {
				layer.msg(data.errormsg);
			}
		});
	}

	function patchBolt(params) {
		var index = layer.load();
		API.picturePatchBolt(params, function(data) {
			layer.close(index);
			if (data.isOk) {
				layer.msg('成功');
				refresh();
			} else {
				layer.msg(data.errormsg);
			}
		});
	}
	/**
	 * 退回
	 */
	$('.pic-btn-goback').click(function() {
		layer.confirm('补件与取消补件.', {
			btn : [ '补件', '取消补件', '取消' ],
			title : false
		}, function() {
			patchBolt({
				ids : selectedNodeArray.join(),
				ifPatchBolt : 'Y'
			});
		}, function() {
			patchBolt({
				ids : selectedNodeArray.join(),
				ifPatchBolt : 'N'
			});
		});
	});
});

// String startWith和endWith的扩展
String.prototype.startWith = function(str) {
	if (str == null || str == "" || this.length == 0 || str.length > this.length)
		return false;
	if (this.substr(0, str.length).toUpperCase() == str.toUpperCase())
		return true;
	else
		return false;
	return true;
}
String.prototype.endWith = function(str) {
	if (str == null || str == "" || this.length == 0 || str.length > this.length)
		return false;
	if (this.substring(this.length - str.length).toUpperCase() == str.toUpperCase())
		return true;
	else
		return false;
	return true;
}