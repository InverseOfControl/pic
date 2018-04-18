/**
 * Created by VIC on 2017-03-22.
 */
(function($) {
	// content 父容器 默认Body
	// element 对象集合

	var $document = $(document);

	// Events
	var EVENT_KEYDOWN = 'keydown'; // 键盘
	var EVENT_WHEEL = 'wheel mousewheel DOMMouseScroll';// 滚轮
	var EVENT_MOUSEMOVE = 'mousemove touchmove pointermove MSPointerMove';
	var EVENT_MOUSEDOWN = 'mousedown touchstart pointerdown MSPointerDown';// 拖拽
	var EVENT_MOUSEUP = 'mouseup touchend touchcancel pointerup pointercancel MSPointerUp MSPointerCancel';

	var CLASS_TRANSITION = 'viewer-transition';
	/**
	 * 图片对象
	 * 
	 * @param title
	 * @param src
	 * @param index
	 * @returns
	 */
	function img(title, src, index, id) {
		this.title = title;
		this.src = src;
		this.index = index;
		this.id = id;
	}

	// 图片等比缩放
	function adaptive($content, id) {
		var result = '';
		$img = $('#' + id);
		var naturalWidth = $img[0].naturalWidth;
		var naturalHeight = $img[0].naturalHeight;
		// 限制图片宽高
		if (naturalWidth > $content.innerWidth() || naturalHeight > $content.innerHeight()) {
			if (naturalWidth - $content.innerWidth() >= naturalHeight - $content.innerHeight()) {
				result = 'max-width:' + ($content.innerWidth() - 10) + 'px;';
			} else {
				result = 'max-height:' + ($content.innerHeight() - 10) + 'px;';
			}
		}
		return result;
	}
	// 缓存
	var cache = {
		count : 0,
		imgs : new Array(),
		option : null,
		index : null
	}

	var imgdialog = '';
	imgdialog += '<div class="pic-img-dialog">';
	imgdialog += '   <div class="dlg-hd"><a title="关闭" class="dlg-img-close"></a></div>';
	imgdialog += '   <div class="content">{img}</div>';
	imgdialog += '   <div class="operate-area"><div class="operate-container"><div class="img-control"><em class="control control-last pic-image-last" title="上一张"></em><em class="control control-left pic-image-left" title="左旋转"></em><span class="control control-count">{count}</span><em class="control control-right pic-image-right" title="右旋转"></em><em class="control control-next pic-image-next" title="下一张"></em></div></div></div>';
	imgdialog += '</div>';

	var methods = {
		init : function(options) {
			return this.each(function() {
				var $this = $(this);
				var settings = $this.data('imageKit');

				if (typeof (settings) == 'undefined') {

					var defaults = {
						content : '',
						element : 'img'
					}

					settings = $.extend({}, defaults, options);

					$this.data('imageKit', settings);
				} else {
					settings = $.extend({}, settings, options);
				}
				// 代码在这里运行
				cache.option = settings;
			});
		},
		destroy : function(options) {
			return $(this).each(function() {
				var $this = $(this);
				$this.removeData('imageKit');
			});
		},
		/**
		 * 渲染
		 */
		render : function(options) {
			var that = this;
			var x = 0;
			cache.imgs.count = 0;
			cache.imgs.splice(0, cache.imgs.length);
			this.find(cache.option.element).each(function() {
				var $this = $(this);
				if ($this.attr('src')) {
					x++;
					$this.attr('data-index', x);
					$content = cache.option.content.length > 0 ? $(cache.option.content) : $(document.body);
					$this.click(function() {
						// 图片
						$img = $(this);
						var naturalWidth = $img[0].naturalWidth;
						var naturalHeight = $img[0].naturalHeight;
						if (naturalWidth == 0 || naturalHeight == 0) {
							layer.msg('加载失败');
							return;
						}
						var i = '<span><img src="' + $this.attr('src') + '" class="viewer-move viewer-transition" ';
						i += 'style="' + adaptive($content, $img.attr('id')) + '"';
						i += '></span>';

						// 图片统计
						var count = $this.attr('data-index') + '-' + cache.count;

						$dlghdLeft = $content.offset().left + $(cache.option.content).innerWidth() - 125;

						$content.append(imgdialog.replace(/\{count\}/g, count).replace(/\{img\}/g, i));
						cache.index = parseInt($this.attr('data-index'));
						// 关闭按钮
						$('.dlg-img-close').click(function() {
							$('.pic-img-dialog').remove();
							// 取消事件绑定
							$document.off(EVENT_KEYDOWN);
							$document.off(EVENT_WHEEL);
						});
						// 左旋转
						$('.pic-image-left').click(function() {
							$img = $('.pic-img-dialog').find('img');
							var index = parseInt($img.attr('class').substring(3));
							if (index == 0) {
								$img.attr('class', 'rot3');
							} else {
								$img.attr('class', 'rot' + (index - 1));
							}
						});
						// 右旋转
						$('.pic-image-right').click(function() {
							$img = $('.pic-img-dialog').find('img');
							var index = parseInt($img.attr('class').substring(3));
							if (index == 3) {
								$img.attr('class', 'rot0');
							} else {
								$img.attr('class', 'rot' + (index + 1));
							}
						});
						// 上一张
						$('.pic-image-last').click(function() {
							$img = $('.pic-img-dialog').find('img');
							var index = cache.index;
							if (1 == index) {
								layer.msg('前面没有图片啦!');
							} else {
								for (x in cache.imgs) {
									if ((index - 1) == cache.imgs[x].index) {
										$img.attr('src', cache.imgs[x].src);
										$img.attr('style', adaptive($content, cache.imgs[x].id));
										$('.pic-img-dialog').find('.control-count').text(cache.imgs[x].index + '-' + cache.count);
										cache.index = cache.imgs[x].index;
										break;
									}
								}
								$img.attr('class', 'rot0');
							}
						});
						// 下一张
						$('.pic-image-next').click(function() {
							$img = $('.pic-img-dialog').find('img');
							var index = cache.index;
							if (cache.count == index) {
								layer.msg('后面没有图片啦!');
							} else {
								for (x in cache.imgs) {
									if ((index + 1) == cache.imgs[x].index) {
										$img.attr('src', cache.imgs[x].src);
										$img.attr('style', adaptive($content, cache.imgs[x].id));
										$('.pic-img-dialog').find('.control-count').text(cache.imgs[x].index + '-' + cache.count);
										cache.index = cache.imgs[x].index;
										break;
									}
								}
								$img.attr('class', 'rot0');
							}
						});
						// 拖拽
						$document.find('.viewer-move').dragmove();
					});
					cache.imgs.push(new img($this.attr('title'), $this.attr('src'), parseInt($this.attr('data-index')), $this.attr('id')));
				}
			});
			cache.count = x;
			/**
			 * 绑定事件
			 */
			$document.on(EVENT_KEYDOWN, function(event) {
				if (event.keyCode == 37) {
					// 上一张图片
					$('.pic-image-last').trigger("click");
				}
				if (event.keyCode == 39) {
					// 下一张图片
					$('.pic-image-next').trigger("click");
				}
			});
			// 放大缩小
		}
	};

	$.fn.imageKit = function() {
		var method = arguments[0];
		if (methods[method]) {
			method = methods[method];
			arguments = Array.prototype.slice.call(arguments, 1);
		} else if (typeof (method) == 'object' || !method) {
			method = methods.init;
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.imageKit');
			return this;
		}
		return method.apply(this, arguments);
	}
})(jQuery);