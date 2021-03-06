/*****************************************************************************
 * Program Name : mint.base.js
 * Description
 *   - mint 서비스를 사용하기 위한 main script
 *   - 서비스에 관련된 기본 항목을 정의한다
 *
 *   - Access 방법
 *     mint.{함수명};
 *     mint.callSerice(p1,p2,p3,p4);
 *
 ****************************************************************************/
/**
 * 오른쪽 마우스 막기
 */
document.oncontextmenu = function(evt){
	try {
		if(evt){
			evt.preventDefault();
		} else {
			event.keyCode = 0;
			event.returnValue = false;
		}
	} catch( e ) {
	}
};

/**
 * backspace 막기
 */
document.onkeydown = function(evt) {
	var key = (evt) ? evt.keyCode : event.keyCode;
	var el;
	if( evt.srcElement ) {
		el = evt.srcElement.tagName.toUpperCase();
	}
	//alert(el);
	if( key == 8 && el != 'INPUT' && el != 'TEXTAREA' ) {

		if( el === 'DIV' && evt.srcElement.contentEditable === 'true' ){
			return;
		}
		if(evt) {
			evt.preventDefault();
		} else {
			event.keyCode = 0;
			event.returnValue = false;
		}
	}
/*
	if(key==8 || key==116) {
		if(evt) {
			evt.preventDefault();
		} else {
			event.keyCode = 0;
			event.returnValue = false;
		}
	}
*/
};

$(window).resize(function() {
	var winHeight = $(window).height();
	$("#main_contents").height(winHeight-90);
	$("#main_contents_detail").height(winHeight-90);
});

window.onpopstate = function(evt) {
	if( evt.state != null) {
		mint.move(evt.state);
	}
};

if( typeof mint === "undefined") {
	var mint = {
	};
}

/**
 * mint service WebRoot
 */
mint.baseServiceUrl = '/mint';

/**
 * REST Call 요청시 Test 인지 Real 인지 구분값<p>
 */
mint.isTestMode = false;

/**
 * UI Language 설정 ( default=ko )
 */
mint.lang = 'ko';


/**
 * 괄호 설정
 */
mint.lb = '[';
mint.rb = ']';

/**
 * Set contextPath
 */
mint.setContextPath = function(contextPath) {
	try {
		mint.baseServiceUrl = contextPath;
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.setContextPath"});
	}
}

/**
 * 화면 이동
 * main_contents Layer 에 호출된 url 의 화면을 display 한다
 * @param url
 */

mint.move = function(url, options) {
	try {
		//페이지 로딩전 공통적으로 처리할 항목들
		{
			if(url != history.state) {
				history.pushState(url, null, null);
			}

			// 메인대시보드 Interval 초기화
			mint.common.terminateInterval("mint.move");
			// 메인대시보드 Timeout 초기화
			mint.common.terminateTimeout("mint.move");
			// 진행중이던 callService 가 있으면 초기화
			mint.callServiceAbortAll();
			// WebSocket service Off
			mint.wsServiceOff();
		}

		//Screen 확장 여부, 대시보드일경우 확장
		{
			var isFixedSideBar = false;
			var isMinimalize   = false;
			/*
			if( url === '../sub-op/OP-02-00-001' ) {
				isFixedSideBar = true;
				isMinimalize   = true;
			} else {
				isFixedSideBar = false;
				isMinimalize   = false;
			}
			*/
			mint.ui.fixedSideBar(isFixedSideBar, isMinimalize);
		}

		//options 이 있을 경우 setScreenParam 에 설정
		{
			if( ! mint.common.isEmpty( options ) ) {

				var opt = mint.common.getScreenParam('options');
				if( ! mint.common.isEmpty(opt) && ! mint.common.isEmpty(opt.detailView) && opt.detailView === 'Y' ) {
					options.detailView = opt.detailView;
				}
				mint.common.setScreenParam("options", options);
			}
		}

		if( mint.isDetailView() ) {
			mint.common.debugLog('--> detail 형식 페이지 이동');
			$.ajax({
				cache : false,
				url: url + ".html",
				success:function(data){
					$('#main_contents').hide();
					$('#main_contents_detail').show();
					$('#main_contents_detail').html(data);
				}
			});
		} else {
			mint.common.debugLog('--> 기본 형식 페이지 이동');

			$.ajax({
				cache : false,
				url: url + ".html",
				success:function(data){
					$('#main_contents_detail').html('');
					$('#main_contents_detail').hide();
					//Kendo Widgets Destory
					$('#main_contents').html('');
					kendo.destroy(document.body);
					$('#main_contents').show();
					$('#main_contents').html(data);
				}
			});
		}

		//페이지 로딩후 공통적으로 처리할 항목들
		{
			//1. 도움말창을 닫는다.
			if( $('helpWindow') && $('#helpWindow').data('kendoWindow') ) {
				$('#helpWindow').data('kendoWindow').close();
			}
			//2. 상세페이지 여부를 초기화 한다.
			mint.setDetailView('N');
			//3. 방문자 정보를 재조회 한다.
			//$.header.fn_getUserCount();
			//4. 스크롤을 화면 상단으로 이동시킨다.
			window.scrollTo(0,0);
			//5. notificationClear
			mint.common.notificationClear();
			//6. 로그인유효성체크
			mint.loginValidate();
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.move"});
	}
};

/**
 * crudx
 * @param opt
 */
mint.crudx = function(appId, attachDiv, callback, options) {
	try {
		var url = '../include/crudx.jsp';
		var fn_callback = mint.common.fn_callback(callback);

		$(attachDiv).load(url, 'appId='+appId, function(responseText, status, xhr) {

			var readyState = xhr.readyState;
			var httpStatus = xhr.status;
			var statusText = xhr.statusText;

			if( readyState == 4 && httpStatus == 200 ) {

				mint.label.attachLabel(null);

				if(options) {
					var list = options.list;
					if(list) {
						for(var i = 0; i < list.length; i++) {
							var event = mint.common.fn_callback(list[i].event);
							$(list[i].name).on('click', event);
						}
					}
				}

				if( fn_callback ) {
					fn_callback( responseText );
				}
			}
		});
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.crudx"});
	}
};

/**
 * attachContents
 * @param divId
 * @param url
 * @param callback
 * @param options
 */
mint.attachContents = function(divId, url, callback, options) {
	try {
		var fn_callback = mint.common.fn_callback(callback);

		var target = $('#' + divId);
		if( !target.length ) {
			$('body').append($('<div id="' + divId + '"></div>'));
		} else {
			target.empty();
		}

		$('#' + divId).load(url, null, function(responseText, status, xhr) {

			var readyState = xhr.readyState;
			var httpStatus = xhr.status;
			var statusText = xhr.statusText;

			if( readyState == 4 && httpStatus == 200 ) {

				//Do Something

				if( fn_callback ) {
					fn_callback( responseText );
				}
			}
		});

	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.attachContents"});
	}
};

/**
 * 호출된 페이지가 상세 페이지 인지 여부를 설정한다 'Y'/'N'
 * @param opt
 */
mint.setDetailView = function(flag) {
	try {
		var options = mint.common.getScreenParam('options');
		if( mint.common.isEmpty(options) ) {
			options = new Object();
		}
		options.detailView = flag;
		mint.common.setScreenParam('options', options);
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.setDetailView"});
	}
};

/**
 * 호출된 페이지가 상세 페이지로 설정되어 있으면 true
 * @param opt
 */
mint.isDetailView = function() {
	try {
		var flag = false;
		var options = mint.common.getScreenParam('options');
		if( ! mint.common.isEmpty(options) && ! mint.common.isEmpty(options.detailView) && options.detailView === 'Y' ) {
			flag = true;
		} else {
			flag = false;
		}
	} catch( e ) {
		flag = false;
		mint.common.errorLog(e, {"fnName" : "mint.isDetailView"});
	} finally {
		return flag;
	}
};


/**
 * 상세페이지에서 목록 버튼 클릭시 조회화면으로 이동
 * @param options
 */
mint.back = function(options) {
	try {
		var isPageMove = false;
		if( !mint.common.isEmpty( options ) ) {
			if( !mint.common.isEmpty( options.callBackPage ) ) {
				isPageMove = true;
			} else {
				isPageMove = false;
			}
  		} else {
  			isPageMove = false;
  		}

		if(isPageMove) {
			mint.move(options.callBackPage);
		} else {
			$('#main_contents').show();
  			$('#main_contents_detail').html('');
  			$('#main_contents_detail').hide();
  			mint.common.notificationClear();
		}

	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.back"});
	}
};

/**
 * IIP 메인화면으로 이동
 * @param options
 */
mint.goMain = function(options) {
	try {
		var isAuto = false;
		var envVal = mint.common.commonCodeMap.get("environmentalValues");
		if( !mint.common.isEmpty(envVal) ) {
			if( !mint.common.isEmpty(envVal["system.init.screen.url"]) ) {
				var isAuto = true;
			}else{
				var isAuto = false;
			}
		}
		if(isAuto){

			if( !mint.common.isEmpty(envVal["system.init.screen.role.used"]) ) {
				if( envVal["system.init.screen.role.used"][0] == "Y") {
					if( !mint.common.isEmpty(envVal["system.init.screen.url."+mint.session.getRoleId()]) ) {
						mint.move(envVal["system.init.screen.url."+mint.session.getRoleId()][0]);
					} else {
						mint.move(envVal["system.init.screen.url"][0]);
					}
				}
			} else {
				mint.move(envVal["system.init.screen.url"][0]);
			}
		}else{
			mint.move('../sub-an/AN-01-00-001');
		}
		mint.wsConnect();
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.goMain"});
	}
};

/**
 * callService Pool
 */
mint.callServicePool = [];

/**
 * callService Abort All
 */
mint.callServiceAbortAll = function() {
	try {
		var i = 0;
		var pooSize = mint.callServicePool.length;
		$.each(mint.callServicePool, function(idx, xhr) {
			if(!mint.common.isEmpty(xhr)) {
				i++;
				xhr.abort();
			}
		});
		mint.callServicePool = [];
		mint.common.debugLog('callServiceAbortAll::poolSize[' + pooSize + '] abortCount[' + i + ']', {"fnName" : "mint.callServiceAbortAll"});
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.callServiceAbortAll"});
	}
};

/**
 * callService Abort
 */
mint.callServiceAbort = function(xhr, isAbort) {
	try {
		var pooSize = mint.callServicePool.length;
        var index = mint.callServicePool.indexOf(xhr);
        if (index > -1) {
       	 mint.callServicePool.splice(index, 1);
        }
        if( isAbort ) {
        	xhr.abort();
        }
        //mint.common.debugLog('callServiceAbort::poolSize[' + pooSize + ']');
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.callServiceAbort"});
	}
};

/**
 * Server 로 REST 서비스를 호출한다
 * @param requestObject : ComMessage에 requestObject로 사용할 model의 json형태
 * @param appId         : 화면ID, Server Side 에서 로깅 목적으로 사용
 * @param restId        : REST-API ID
 * @param callback      : 데이터를 가져온 뒤 사용할 callback 함수명(함수명 또는 String 형태)
 * @param options       : 확장을 고려한 파라메터,
 *                         {
 *                            errorCall : true //error 가 발생한 후에도 callback 함수를 호출할건지 여부
 *                         }
 */
mint.callService = function(
		requestObject,
		appId,
		restId,
		callback,
		options) {

	try {
		//----------------------------------------------------------------------
		// Service URL
		//----------------------------------------------------------------------
		var url = mint.rest.getServiceUrl(restId);
		if( !url ) {
			mint.common.alert('CW00005', restId);
			return;
		}

		//----------------------------------------------------------------------
		// URL Parameter Replace
		//----------------------------------------------------------------------
		{
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.params ) ) {
				var paramsKeys = Object.keys(options.params);
				if( paramsKeys && paramsKeys.length > 0 ) {
					for(var i = 0; i < paramsKeys.length; i++) {
						//변경 20201014, 라우터 URL로 변경
						url = url + '&' + paramsKeys[i] + '=' + options.params[paramsKeys[i]];
					}
				}
			}
			//Uri encode 설정
			url = encodeURI( url );
		}

		//----------------------------------------------------------------------
		// Service Locale Setting
		//----------------------------------------------------------------------
		url = url + '&locale=' + mint.lang;


		//----------------------------------------------------------------------
		// callServicePool 을 사용할것인지 체크 default : false(미사용)
		//----------------------------------------------------------------------
		var isPool = false;
		{
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.isPool ) ) {
				isPool = options.isPool;
			}

			if( isPool ) {
				if( mint.callServicePool.length >= 5 ) {
					debugMsg = 'serviceCall reject(pool is max:' + mint.callServicePool.length + ') :: appId(' + appId + ') url(' + url + ')';
					mint.common.debugLog( debugMsg, {"fnName" : "mint.callService"} );
					return;
				}
			}

		}

		//----------------------------------------------------------------------
		// Waiting 이미지를 사용할것인지 여부 default : true(사용)
		//----------------------------------------------------------------------
		var waitingView = true;
		{
			// Wainting 여부를 사용자가 명시적으로 선언한게 있는지 체크해서 재설정
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.waitingView ) ) {
				if( options.waitingView === true ) {
					waitingView = true;
				} else {
					waitingView = false;
				}
			}
		}

		//----------------------------------------------------------------------
		// 조회 결과 메세지를 화면에 표시 할것인지 여부
		//----------------------------------------------------------------------
		var notificationView = true;
		{
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.notificationView ) ) {
				if( options.notificationView === true ) {
					notificationView = true;
				} else {
					notificationView = false;
				}
			}
		}

		//----------------------------------------------------------------------
		// xy를 사용할것인지 여부
		//----------------------------------------------------------------------
		var isXY = ( mint.xy.isEnable() && (url.indexOf('isXY=true') > 0 ) ) ? true : false;

		//----------------------------------------------------------------------
		// response message 의 압축여부
		//----------------------------------------------------------------------
		var isCompress = ( url.indexOf('isCompress=true') > 0 ) ? true : false;

		//----------------------------------------------------------------------
		// timeout 을 사용할것인지 체크 default : false(미사용)
		//----------------------------------------------------------------------
		var isTimeout = false;
		{
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.timeout ) ) {
				isTimeout = true;
			} else {
				isTimeout = false;
			}
		}

		//----------------------------------------------------------------------
		// ComMessage Create
		//----------------------------------------------------------------------
		var comMessage = new Object();
		{
			comMessage.objectType = "ComMessage";
			comMessage.requestObject = requestObject;
			comMessage.startTime = mint.common.getStartTime();
			comMessage.endTime = null;
			comMessage.errorCd = "0";
			comMessage.errorMsg = "";
			if( mint.common.isEmpty(mint.session) ) {
				comMessage.userId = "unknown";
			} else {
				comMessage.userId = mint.session.getUserId();
			}
			comMessage.appId = appId;

			//세션 체크를 하지 않은 프로그램의 경우, userId 를 guest 로 설정하도록 한다
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.checkSession ) ) {
				if( options.checkSession === false ){
					comMessage.userId = "unknown";
					comMessage.checkSession = false;
				}
			}
			//----------------------------------------------------------------------
			// groupId add
			//----------------------------------------------------------------------
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.groupId ) ) {
				comMessage.groupId = options.groupId;
			}
		}

		//----------------------------------------------------------------------
		// 2017/04/03 주석 처리. 필드별 xy 적용하도록 수정.
		// TODO: 향후 보완할것.
		//----------------------------------------------------------------------
		/*
		if(isXY) {
			comMessage = mint.xy.encrypt(JSON.stringify(comMessage));
		} else {
			comMessage = JSON.stringify(comMessage);
		}
		*/
		if(isXY) {
			comMessage.requestObject = mint.xy.encrypt(JSON.stringify(requestObject));
			if( mint.common.isEmpty(requestObject) ) {
				return;
			}
		}
		comMessage = JSON.stringify(comMessage);

		//----------------------------------------------------------------------
		// Async/Sync Call
		//----------------------------------------------------------------------
		var asyncCall = true;
		if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.asyncCall ) ) {
			if( options.asyncCall === false ){
				asyncCall = false;
			}
		}

		//----------------------------------------------------------------------
		// HtmltoText
		//----------------------------------------------------------------------
		var editorMode = mint.common.getEnvValue('ui.editor.mode');
		if( ! mint.common.isEmpty( editorMode ) && editorMode == 'text' ) {
			var r1 = /<[a-z].*?\/?>/ig;
			var r2 = /(<([^>(ㄱ-ㅎ|ㅏ-ㅣ|가-힣)]+)>)/ig;
			comMessage = comMessage.replace(r1, '\\n').replace(r2, '');
		}

		//Request Debug
		//mint.common.debugLog('REST URL : '+ url +'\nRequestMessage : ' + comMessage, {"fnName" : "mint.callService","logPoint" : "Request"} );

		$.ajax({
			 async : asyncCall,
			 cache : false,
		     type: 'POST',
		     dataType: 'json',
		     url: url,
		     contentType : 'application/json; charset=utf-8',
		     data: comMessage,
		     timeout: isTimeout ? options.timeout : 0,
		     complete: function(xhr,textStatus) {
		    	 if( waitingView === true ) {
		    		 mint.waitingLoad(false);
		    	 }

		    	 var readyState = xhr.readyState;
		    	 var httpStatus = xhr.status;
		    	 var statusText = xhr.statusText;

		    	 if( readyState == 0 && xhr.statusText == 'timeout' ) {
		    		 mint.callServiceAbort(xhr, false);
		    	 } else if( readyState == 4 && httpStatus == 200 ) {
		    		 //-----------------------------------------------------------------------
		    		 // callServicePool remove
		    		 //-----------------------------------------------------------------------
		    		 mint.callServiceAbort(xhr, false);

		    		 var fn_callback = mint.common.fn_callback(callback);
		    		 //-----------------------------------------------------------------------
		    		 // Response ComMessage
		    		 //-----------------------------------------------------------------------
		    		 var msg = JSON.parse(xhr.responseText);

		    		 //-----------------------------------------------------------------------
		    		 //Response Debug
		    		 //-----------------------------------------------------------------------
		    		 //mint.common.debugLog( JSON.stringify(msg), {"fnName" : "mint.callService","logPoint" : "Response"} );

		    		 //-----------------------------------------------------------------------
		    		 // ComMessage 의 errorCode 체크
		    		 //-----------------------------------------------------------------------
		    		 var errorCode = msg.errorCd;
		    		 if( errorCode == 0 ) {
		    			 if( fn_callback ) {
	    					if( options && options.includeRequest === true ) {
	    						fn_callback( msg.requestObject, msg.responseObject, msg.errorCd, msg.errorMsg );
	    					} else {
	    						fn_callback( msg.responseObject, msg.errorCd, msg.errorMsg );
	    					}
		    			 }
		    		 } else {
		    			 if( options && options.throwError === true ) {

						 } else {
			    			 if( msg.errorCd == 1002 ) {
			    				//-----------------------------------------------------------------------
			    				//1002 : 조회 결과가 없습니다, 정상 Case 로 보자..
			    				//-----------------------------------------------------------------------
			    				 if( ! mint.common.isEmpty(appId) ) {
			    				 	if( notificationView ) {
			    				 		mint.common.notification(msg.errorMsg);
			    				 	}
			    				 }
			    			 } else if( msg.errorCd == 1102 ) {
			    				 window.location = '../../do';
			    				 return;
			    			 } else {
			    				 mint.common.errorDialog('Service', msg.errorCd, msg.errorMsg, msg.errorDetail, msg);
			    			 }
		    			 }
		    			 //-----------------------------------------------------------------------
		    			 // options에 errorCall 항목을 true로 설정하는 경우
		    			 // 에러가 발생한 후에도 사용자 함수를 호출한다.
		    			 //-----------------------------------------------------------------------
		    			 if( options && options.errorCall === true ) {
		    				 if( fn_callback ) {
		    					if( options && options.includeRequest === true ) {
		    						fn_callback( msg.requestObject, msg.responseObject, msg.errorCd, msg.errorMsg );
		    					} else {
		    						fn_callback( msg.responseObject, msg.errorCd, msg.errorMsg );
		    					}
		        			 }//end if
		    			 }//end if
		    		 }
		     	 } else {
		     		 switch( httpStatus ) {
		         		 case 12029 : //server shutdown
		         			 alertMsg = mint.message.getMessage('CE12029', null);
		         			 break;
		         		 case 404 :
		         			 alertMsg = mint.message.getMessage('CE10404', null);
		         			 break;
		         		 default :
		         			 alertMsg = mint.message.getMessage('CE10000', httpStatus);
		         			 break;
		     		 }
		     		 if( httpStatus == 0 ) {
		     			 // httpStatus 가 0 일 때는 별다른 내용이 없으므로 팝업을 호출 하지 않도록 함.
		     		 } else {
		     			 mint.common.errorDialog('Http', httpStatus, alertMsg, null);
		     		 }
		    	 }
		    	 /*
		         var index = mint.callServicePool.indexOf(xhr);
		         if (index > -1) {
		        	 mint.callServicePool.splice(index, 1);
		        	 console.log('pool: ' + mint.callServicePool.length);
		         }
		         */
		     }//end complete
			, beforeSend : function(xhr) {
				mint.callServicePool.push(xhr);
				if( isXY ) {
					xhr.setRequestHeader('Mint-Encrypt', 'true');
				}
				if( isCompress ) {
					xhr.setRequestHeader('Mint-Compress', 'true');
				}
				if( waitingView === true ) {
					mint.waitingLoad(true);
				}
			}
		 });//end ajax call
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.callService"});
	}
};


/**
 * Excel 파일을 다운로드 한다
 * @param excelFileNm 	: 다운로드 받을 Excel 파일명
 * @param sheet        	: Kendo UI에서 생성된 Excel Sheet
 * @param appId         : 화면ID, Server Side 에서 로깅 목적으로 사용
 */
mint.downloadExcelFile = function(excelFileNm, sheet, appId) {
	try {

		var url = mint.rest.getServiceUrl('REST-TEST-EXPORT-EXCEL');
		if( !url ) {
			mint.common.alert('CW00005', 'REST-TEST-EXPORT-EXCEL');
			return;
		}

		var xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.responseType = 'arraybuffer';
		xhr.onload = function () {
		    if (this.status === 200) {
		        var filename = "";
		        var disposition = xhr.getResponseHeader('Content-Disposition');
		        if (disposition && disposition.indexOf('attachment') !== -1) {
		            var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
		            var matches = filenameRegex.exec(disposition);
		            if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
		        }
		        var type = xhr.getResponseHeader('Content-Type');

		        var blob = new Blob([this.response], { type: type });
		        if (typeof window.navigator.msSaveBlob !== 'undefined') {
		            // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
		            window.navigator.msSaveBlob(blob, filename);
		        } else {
		            var URL = window.URL || window.webkitURL;
		            var downloadUrl = URL.createObjectURL(blob);

		            if (filename) {
		                // use HTML5 a[download] attribute to specify filename
		                var a = document.createElement("a");
		                // safari doesn't support this yet
		                if (typeof a.download === 'undefined') {
		                    window.location = downloadUrl;
		                } else {
		                    a.href = downloadUrl;
		                    a.download = filename;
		                    document.body.appendChild(a);
		                    a.click();
		                }
		            } else {
		                window.location = downloadUrl;
		            }

		            setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
		        }
		    }
		};
//			xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');

		if(null != excelFileNm && '' != excelFileNm) {
			sheet.excelFileNm = excelFileNm;
		} else {
			sheet.excelFileNm = '';
		}

		var comMessage = new Object();
		{
			comMessage.objectType = "ComMessage";
			comMessage.requestObject = sheet;
			comMessage.startTime = mint.common.getStartTime();
			comMessage.endTime = null;
			comMessage.errorCd = "0";
			comMessage.errorMsg = "";
			comMessage.userId = mint.session.getUserId();
			comMessage.appId = appId;
		}

//			xhr.send($.param(comMessage));
		xhr.send(JSON.stringify(comMessage));

	}  catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.downloadFile"});
	}
};


/**
 * Excel 파일을 다운로드 한다
 * @param excelFileNm 	: 다운로드 받을 Excel 파일명
 * @param requestObject   : requestObject
 * @param appId         : 화면ID, Server Side 에서 로깅 목적으로 사용
 */
mint.downloadExcel = function(excelFileNm, requestObject, restId, appId) {
	try {
		var url = mint.rest.getServiceUrl(restId);
		if( !url ) {
			mint.common.alert('CW00005', restId);
			return;
		}

		mint.waitingLoad(true);

		var xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.responseType = 'arraybuffer';
		xhr.onload = function () {
			if (this.status === 200) {
				var filename = "";
				var disposition = xhr.getResponseHeader('Content-Disposition');
				if (disposition && disposition.indexOf('attachment') !== -1) {
					var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
					var matches = filenameRegex.exec(disposition);
					if (matches != null && matches[1]) {
						filename = decodeURI(matches[1].replace(/['"]/g, ''));
					}
				}
				var type = xhr.getResponseHeader('Content-Type');

				var blob = new Blob([this.response], { type: type });
				if (typeof window.navigator.msSaveBlob !== 'undefined') {
					// IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
					window.navigator.msSaveBlob(blob, filename);
				} else {
					var URL = window.URL || window.webkitURL;
					var downloadUrl = URL.createObjectURL(blob);

					if (filename) {
						// use HTML5 a[download] attribute to specify filename
						var a = document.createElement("a");
						// safari doesn't support this yet
						if (typeof a.download === 'undefined') {
							window.location = downloadUrl;
						} else {
							a.href = downloadUrl;
							a.download = filename;
							document.body.appendChild(a);
							a.click();
						}
					} else {
						window.location = downloadUrl;
					}

					setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
				}
				mint.waitingLoad(false);
			}
		};
//			xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');

		if(null != excelFileNm && '' != excelFileNm) {
			requestObject.excelFileNm = excelFileNm;
		} else {
			requestObject.excelFileNm = '';
		}

		var comMessage = new Object();
		{
			comMessage.objectType = "ComMessage";
			comMessage.requestObject = requestObject;
			comMessage.startTime = mint.common.getStartTime();
			comMessage.endTime = null;
			comMessage.errorCd = "0";
			comMessage.errorMsg = "";
			comMessage.userId = mint.session.getUserId();
			comMessage.appId = appId;
		}

//			xhr.send($.param(comMessage));
		xhr.send(JSON.stringify(comMessage));

	}  catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.downloadFile"});
	}
};


/**
 * GET 방식으로 파일 다운로드를 처리한다.
 * filePath 와 fileNm 은 fileDownLoad 호출전, encodeURI 처리하도록 한다.
 */
mint.fileDownLoad = function(filePath, fileNm) {
	try{
		var url = mint.baseServiceUrl + '/ut/download?filepath=' + filePath + '&filename=' + fileNm;
		$.fileDownload( url )
        .done(function () {})
        .fail(function () { mint.common.alert('File download failed!['+ decodeURI(fileNm) +"]", null); });
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.fileDownLoad"});
	}
}



/**
 * 화면 로딩시 선행작업 처리 ( Biz 나 Service 관점의 선행작업 )
 * mint.ui.initScreen() 은 순수 UI 관점에서의 초기화 function()
 *
 * 각 화면별 document.ready() 실행 시점에 mint.init() 을 호출 하도록 하며,
 * init 이후에 화면단 초기화 작업을 진행할 콜백 함수를 실행한다
 * @param callback
 */
mint.init = function(callback, options) {
	try {
		//TODO init 에서 별로 할일이 없네..

		//UI 초기화
		if( mint.common.isEmpty( options ) ) {
			mint.ui.initScreen();
		} else {
			mint.ui.initScreen( options );
		}

		//공통코드조회 호출
		mint.getCommonCode(callback, false);

		/*
		if( mint.common.isEmpty(mint.session) ) {
			mint.common.alert('CW00000', null);
			window.location='../main/login.jsp';
		} else {
			mint.getCommonCode(fn_callback, false);
		}
		*/
	}  catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.init"});
	}
};

/**
 * 공통코드를 가져온다
 * @param callback : 콜백함수
 * @param reload   : 공통코드를 다시 조회할지 여부(등록/수정 이후는 공통코드를 재조회할 필요가 있다)
 *                    true/false
 */
mint.getCommonCode = function(callback, reload) {

    try {
    	var fn_callback = mint.common.fn_callback(callback);
	    if( reload || ( mint.common.isEmpty(mint.common.commonCodeMap) || mint.common.commonCodeMap.size() == 0 ) ) {

	        mint.callService(null, 'mint-ui-init', 'REST-R01-CO-02-00-000'
	        		, function(jsonData){
	        			if( reload ) {
	        				mint.common.commonCodeMap.clear();
	        			}
        				mint.common.setCommonCode(jsonData);
	        			if( fn_callback ) {
	        				fn_callback();
	        			}
	        		}, {errorCall : true, params: { userId : mint.session.getUserId() } }
	        );
	    } else {
	    	if(fn_callback)
	    		fn_callback();
	    }
    } catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.getCommonCode"});
    }
};

/**
 * 처리중 이미지 표시
 * @param isShow true 처리중 이미지 표시
 */
mint.waitingLoad = function(isShow) {
	var waiting = $('#waitingLoad');
	if(!waiting) {
		var obj = $('<div id="waitingLoad" style="display:none" align="center">' +
					'<p style="text-align:center; padding:0 0 0 0; left:50%; top:50%; position:absolute;">' +
					'<span id="waitingLoad-image"><i class="fa fa-spinner fa-pulse fa-3x fa-fw"></i><span class="sr-only">Loading...</span></span>' +
					'</p>' +
					'</div>');
		$('body').prepend(obj);
		waiting = $('#waitingLoad');
	}

	var maxZ = mint.common.getTopZindex();
	if( maxZ > 20000 ) {
		$('#waitingLoad').css('z-index', mint.common.getTopZindex());
	}
	// 처리 중 이미지를 표시한다.
	if (isShow) {
		waiting.show();
	} else {
		waiting.hide();
	}
};

/**
 * loginValidate
 * 대시보드등에서 세션 유지가 필요할때 호출
 * @param
 */
mint.loginValidate = function(options) {
    try {
    	var dupObj = mint.session.getDuplicationLoginHistory();
    	if( !mint.common.isEmpty(dupObj) ) {
			var dup_loginDate = dupObj.loginDate;
			var dup_ip = dupObj.ip;
			var date = dup_loginDate.substr(0,4)+"/"+dup_loginDate.substr(4,2)+"/"+dup_loginDate.substr(6,2)+" "+dup_loginDate.substr(8,2)+":"+dup_loginDate.substr(10,2)+":"+dup_loginDate.substr(12,2);
			var dupMsg = '<span><h4>[중복 로그인]</h4><h4>이전 세션이 로그아웃 되었습니다.</h4><h4>ip : '+dup_ip+'</h4><h4>loginDate : '+date+'</h4></span>';

			mint.common.jobNotification(dupMsg, 'error',{closeButton:true});
    	}
    } catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.loginValidate"});
    }
};

/**
 * HealthCheck
 * 대시보드등에서 세션 유지가 필요할때 호출
 * @param
 */
mint.healthCheck = function(options) {
    try {
		var intervalObj = mint.common.addInterval( function() {
			mint.callService(null, 'heahthCheck', 'REST-R01-CO-01-00-000',
					function(jsonData, errorCd) {

					}, {errorCall: true, notificationView: false, waitingView: false});
		}, (1000 * 20) );
    } catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.healthCheck"});
    }
};

/**
 * 웹소켓 Request Key 저장 ( REQ/RES, Sync 일 경우 )
 */
mint.wsReqKeyStore = [];

/**
 * 웹소켓 지원여부
 */
mint.wsReqKeyStoreClear = function() {
	try{
		mint.wsReqKeyStore = [];
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsReqKeyStoreClear"});
	}
};

/**
 * 웹소켓 지원여부
 */
mint.wsSupported = function() {
	try{
		if( window.WebSocket ) {
			return true;
		} else {
			mint.common.jobNotification('브라우져가 웹소켓을 지원하지 않습니다', 'warning');
			return false;
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsSupported"});
	}
};

/**
 * 웹소켓 사용여부
 */
mint.wsUsed = function() {
	try{
		var env = mint.common.getEnvorinment();
		var flag = env['push.used'];
		if( !mint.common.isEmpty(flag) && flag[0] === 'Y' ) {
			return true;
		} else {
			return false;
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsUsed"});
	}
};

/**
 * 웹소켓 연결
 */
mint.wsConnect = function() {
	try{
		if( mint.wsUsed() ) {
			if( !mint.ws.isConnected() ) {
				var wsUrl = 'ws://' + location.host + mint.baseServiceUrl + '/push/front'
				mint.ws.connect({url: wsUrl});
			}
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsConnect"});
	}
};

/**
 * 웹소켓 Message 전송
 * @param requestObject
 * @param appId
 * @param serviceCd
 * @param msgType
 * @param callback
 * @param options
 */
mint.wsSend = function(requestObject, appId, serviceCd, msgType, callback, options) {
	try {
		if( !mint.wsUsed() ) {
			return;
		}
		if( mint.common.isEmpty(options) ) {
			options = {};
		}
		//----------------------------------------------------------------------
		// ComMessage Create
		//----------------------------------------------------------------------
		var comMessage = new Object();
		{
			comMessage.objectType = "ComMessage";
			comMessage.requestObject = requestObject;
			comMessage.startTime = mint.common.getStartTime();
			comMessage.endTime = null;
			comMessage.errorCd = "0";
			comMessage.errorMsg = "";
			if( mint.common.isEmpty(mint.session) ) {
				comMessage.userId = "unknown";
			} else {
				comMessage.userId = mint.session.getUserId();
			}
			comMessage.appId = appId;

			//세션 체크를 하지 않은 프로그램의 경우, userId 를 guest 로 설정하도록 한다
			if( ! mint.common.isEmpty( options ) && ! mint.common.isEmpty( options.checkSession ) ) {
				if( options.checkSession === false ){
					comMessage.userId = "unknown";
					comMessage.checkSession = false;
				}
			}
			//ComMessate Extension
			comMessage.extension = {objectType: "Extension", serviceCd: serviceCd, msgType: msgType};

		}

		//----------------------------------------------------------------------
		// ComMessage Convert to String
		//----------------------------------------------------------------------
		comMessage = JSON.stringify(comMessage);

		//----------------------------------------------------------------------
		// Async/Sync Call Check, defualt : Async
		//----------------------------------------------------------------------
		var asyncCall = true;
		if( !mint.common.isEmpty( options ) && !mint.common.isEmpty( options.asyncCall ) ) {
			if( options.asyncCall === false ){
				asyncCall = false;
			}
		}

		//----------------------------------------------------------------------
		// Type - REQ (Reqeust & Reply, Sync 일 경우 key save)
		//----------------------------------------------------------------------
		if( msgType === mint.ws.msgType.REQ && !asyncCall ) {
			var reqKey = requestObject.reqKey;
			if( !mint.common.isEmpty(reqKey) ) {
				mint.common.debugLog('WebSocket.SyncCall.key.saved(' + reqKey + ')');
				mint.wsReqKeyStore.push(reqKey);
				mint.waitingLoad(true);
				//----------------------------------------------------------------------
				// timeout 처리
				//----------------------------------------------------------------------
				if( options && options.timeout ) {
					mint.common.setTimeout(function() {
						var index = mint.wsReqKeyStore.indexOf(reqKey);
						if( index > -1) {
							mint.common.debugLog('WebSocket.SyncCall.key.timeout(' + reqKey + ')');
							mint.wsReqKeyStore.splice(index, 1);
							mint.waitingLoad(false);

							var errorMsg = mint.message.getMessage('CE80000');
							mint.common.notification(errorMsg, {type:'error', timeOut: '5000'});
						}
					}, options.timeout);
				}
			}
		}

		//----------------------------------------------------------------------
		// options resetting ( 필수값으로 리턴이 필요한 항목 재설정 )
		//----------------------------------------------------------------------
		{
			options.serviceCd = serviceCd;
		}

		//----------------------------------------------------------------------
		// ws sendMessage call
		//----------------------------------------------------------------------
		var fn_callback = mint.common.fn_callback(callback);

		mint.ws.sendMessage(comMessage)
		.done(function(sendData) {
			if( fn_callback ) {
				fn_callback(0, sendData, options);
			}
		})
		.fail(function(exception) {
			if( options && options.errorCall === true ) {
				if( fn_callback ) {
					fn_callback(-1, exception, options);
				}//end if
			}//end if
		});

	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsSend"});
	}
};

/**
 * 웹소켓 Message 수신
 * @param callback
 * @param options
 */
mint.wsReceive = function(callback, options) {
	try {
		if( !mint.wsUsed() ) {
			return;
		}

		var fn_callback = mint.common.fn_callback(callback);
		if( fn_callback ) {
			var listenerKey = options.serviceCd;
			mint.ws.receiveMessage(listenerKey, callback, function(msg){
				if( !mint.common.isEmpty(msg) ) {
					var errorCd   = msg.errorCd;
					var errorMsg  = msg.errorMsg;

					var extension = (!mint.common.isEmpty(msg.extension) ? msg.extension : '');
					var msgType   = (!mint.common.isEmpty(extension.msgType) ? extension.msgType : '');

					//-----------------------------------------------------------------------
					// Error Check
					//-----------------------------------------------------------------------
					if( !mint.common.isEmpty(errorCd) && errorCd !== '0' ) {
						mint.common.notification(errorMsg, {type:'error', timeOut: '5000'});
					}

					//-----------------------------------------------------------------------
					// Type - ACK
					//-----------------------------------------------------------------------
					if( msgType == mint.ws.msgType.ACK ) {
						//TODO : ack 표출이 필요하면 구현할것...waitingView or message
						return;
					}

					//-----------------------------------------------------------------------
					// Type - PUSH
					//-----------------------------------------------------------------------
					if( msgType == mint.ws.msgType.PUSH ) {
						if( options && options.includeRequest === true) {
							fn_callback(msg.requestObject, msg.responseObject, msg.startTime);
						} else {
							fn_callback(msg.responseObject, msg.startTime);
						}
						return;
					}

					//-----------------------------------------------------------------------
					// Type - RES
					//-----------------------------------------------------------------------
					if( msgType == mint.ws.msgType.RES ) {
						if( options && options.asyncCall === false ) {
							var resKey = msg.requestObject.reqKey;
							if( !mint.common.isEmpty(resKey) ) {
								var index = mint.wsReqKeyStore.indexOf(resKey);
								if( index > -1) {
									mint.common.debugLog('WebSocket.SyncCall.key.resotred(' + resKey + ')');
									mint.wsReqKeyStore.splice(index, 1);
									mint.waitingLoad(false);
								}
							} else {
								return;
							}
						}


						if( options && options.includeRequest === true) {
							fn_callback(msg.requestObject, msg.responseObject, msg.startTime);
						} else {
							fn_callback(msg.responseObject, msg.startTime);
						}
						return;
					}

					//-----------------------------------------------------------------------
					// Type - Null or Unknown
					//-----------------------------------------------------------------------
					if( mint.common.isEmpty(msgType)  ) {
						mint.waitingLoad(false);
						if( options && options.includeRequest === true) {
							fn_callback(msg, msg, null);
						} else {
							fn_callback(msg, null);
						}
						return;
					}

				} else {
					fn_callback(null);
				}
			}, options);
		} else {
			//Application Error
			mint.common.alert('CE90000');
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsReceive"});
	}
};

/**
 * 웹소켓 서비스 off
 * @param callback
 * @param options
 */
mint.wsServiceOff = function(options) {
	try {
		if( !mint.wsUsed() ) {
			return;
		}

		var keys = mint.ws.getListenerKeys();
		for( var i = 0; i < keys.length; i++ ) {
			var serviceCd = keys[i];
			mint.wsSend(null, 'wsSereviceOff', serviceCd, mint.ws.msgType.OFF, function(resultCd, msg, option) {
				//TODO
			});
		}
		mint.ws.removeListenerAll();
		mint.wsReqKeyStoreClear();
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsServiceOff"});
	}
};

/**
 * 웹소켓 서비스 off with params
 * @param params
 * @param options
 */
mint.wsServiceOffWithParam = function(params, options) {
	try {
		if( !mint.wsUsed() ) {
			return;
		}

		if( !mint.common.isEmpty(params) ) {
			mint.wsSend(params, 'wsSereviceOff', serviceCd, mint.ws.msgType.OFF, function(resultCd, msg, option) {
				//TODO
			});
		} else {
			var keys = mint.ws.getListenerKeys();
			for( var i = 0; i < keys.length; i++ ) {
				var serviceCd = keys[i];
				var listenerValues = mint.ws.getListenerValues(serviceCd);
				var opt = listenerValues[0].options;
				var requestParam = (!mint.common.isEmpty(opt) ? opt.requestParam : null);
				mint.wsSend(requestParam, 'wsSereviceOff', serviceCd, mint.ws.msgType.OFF, function(resultCd, msg, option) {
					//TODO
				});
			}
			mint.ws.removeListenerAll();
		}

	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.wsServiceOffWithParam"});
	}
};




/**
 * 객체를 추가한다.<p>
 * @param func
 */
mint.addConstructor = function(func) {
    try {
        func();
    } catch(e) {
		//mint.common.errorLog(e, {"fnName" : "mint.addConstructor"});
    }
};