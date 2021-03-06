<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>

<head>

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="imagetoolbar" content="no" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="Robots" content="All" />
<meta name="Keywords" content="" />
<meta name="Description" content="" />

<%@ include file="../include/includeFiles.html" %>
<%@ include file="../include/menuPath.html" %>
<title>Integrated Interface Portal</title>

</head>

<body>
    <div class="modal-dialog">
        <div class="modal-content animated fadeIn">
            <div class="modal-header">
                <span style="float: right; font-size:15px; font-weight: bold; padding: 15px 0px 0px 0px;">
                    <a class="help-over-pannel" onclick="mint.ui.help(this, 'AP00000050','H001')"></a>
                </span>
                <div>
                    <h2><lb class="lb-30"></lb> <lb class="lb-353"></lb></h2>
                </div>
            </div>

            <!--modal-body-->
            <div class="modal-body">
                <div class="ibox-content4">

                    <div class="col-lg-12">
                        <div>
                            <h4>▶ <lb class="lb-884">인터페이스 흐름도</lb></h4>
                        </div>

                       	<div class="tracking-diagram">
                       		<div id="interfaceDiagram" style="align:center;"></div>
                       	</div>
                    </div>

                    <hr class="hr-space">
                    <hr class="hr-space">

					<div class="col-lg-12">
                        <div>
							<sapn style="padding: 0px; float: left;">
								<h4> ▶ <lb class="lb-459"></lb> <lb class="lb-352"></lb></h4>
							</sapn>
							<span style="padding: 0px; float: right;">
								<span class="lable-sm" style="margin-right: 7px"><lb class="lb-459"></lb> <lb class="lb-460"></lb></span>
								<span style="margin-right: 7px"><strong><lb class="lb-89"></lb> </strong><span id="totalCnt"></span></span>
								<span style="margin-right: 7px"><strong><lb class="lb-412"></lb> </strong><span id="completeCnt"></span></span>
								<span style="margin-right: 7px"><strong><lb class="lb-476"></lb> </strong><font color="#ff0000"><span id="errorCnt"></span></font></span>
							</span>
                        </div>

                        <div style="padding:30px 0px 5px 0px;">
							<div id="nodeListGrid"></div>
                        </div>
                        <div>
							<span style="padding: 0px; float: right;">
								<span style="margin-right: 7px"><strong><lb class="">총 소요시간 : </lb> </strong><span id="totalTrunaroundTime"></span></span>
							</span>
                        </div>
                    </div>

                    <hr class="hr-space">
 					<hr class="hr-space">

 					<div class="col-lg-12">
                        <div class="panel blank-panel">
                            <div class="poupup-tabs-container">
                                <ul id="tab-detail-info" class="nav nav-tabs">
                                    <li class="" style="display:none;"><a data-toggle="tab" href="#tab-01"><lb class="lb-462">Message</lb> <lb class="lb-464">Detail</lb></a></li>
                                    <li class="active"><a data-toggle="tab" href="#tab-02"><lb class="lb-463">Error</lb> <lb class="lb-464">Detail</lb></a></li>
                                </ul>
                            </div>

                            <div class="panel-body">
                                <div class="tab-content">
                                   	<div id="tab-01" class="tab-pane">
                                        <div class="col-sm-12" style="padding: 0px 2px 0px 2px">
                                            <textarea id="msg" class="well" style="padding: 10px 10px 10px 10px; width:100%; height: 100px; overflow-y:auto;" readonly></textarea>
                                        </div>
                                    </div>
                                    <div id="tab-02" class="tab-pane active">
                                        <div class="col-sm-12" style="padding: 0px 2px 0px 2px">
                                        	<textarea id="errorMsg" class="alert alert-danger" style="padding: 10px 10px 10px 10px; width:100%; height: 100px; overflow-y:auto;" readonly></textarea>
                                        </div>
                                    </div>
                                </div><!--tab-content end-->
                            </div> <!--panel-body-->
                        </div><!--panel blank-panel-->
                    </div><!--col-lg-12 end-->

                    <hr class="hr-space">
 					<hr class="hr-space">

 					<div class="col-lg-12">
                        <div>
                            <h4>▶ <lb class="lb-152"></lb></h4>
                        </div>
                        <div>
                            <table class="table table-bordered">
                                <tr>
                                    <th class="thbg"><lb class="lb-153"></lb>( <lb class="lb-422"></lb> )</th>
                                    <td id="interfaceIdNm" colspan="3"></td>
                                </tr>
                                <tr>
                                    <th width="20%" class="thbg"><lb class="lb-80"></lb></th>
                                    <td width="30%" id="channelNm"></td>
                                    <th width="20%" class="thbg"><lb class="lb-81"></lb></th>
                                    <td width="30%" id="businessNm"></td>
                                </tr>

                                <tr>
                                	<th class="thbg"><lb class="lb-158"></lb></th>
                                    <td id="dataPrMethodNm"></td>
                                    <th class="thbg"><lb class="lb-155"></lb></th>
                                    <td id="dataPrDirNm"></td>
                                </tr>
                                <tr>
                                    <th class="thbg"><lb class="lb-162"></lb></th>
                                    <td id="appPrMethodNm"></td>
                                    <th class="thbg"><lb class="lb-166"></lb></th>
                                    <td id="dataFrequency"></td>
                                </tr>
                            </table>
                        </div>
                    </div>

					<hr class="hr-space">
					<hr class="hr-space">

 					<div class="col-lg-12">
                        <div>
                            <h4>▶ <lb class="">뱅킹표준헤더</lb></h4>
                        </div>
                        <div>
                            <table class="table table-bordered">
                            	<tr>
                                    <th width="20%" class="thbg"><lb class="">수신서비스명</lb></th>
                                    <td width="30%" id="rmsSvc"></td>
                                    <th width="20%" class="thbg"><lb class="">결과수신서비스명</lb></th>
                                    <td width="30%" id="rztRmsSvc"></td>
                                </tr>
                                <tr>
                                    <th class="thbg"><lb class="">T GID</lb></th>
                                    <td id="tGid" colspan="3"></td>
                                </tr>
                                <tr>
                                    <th class="thbg"><lb class="">O GID</lb></th>
                                    <td id="oGid" colspan="3"></td>
                                </tr>
                                <tr>
                                    <th class="thbg"><lb class="">S GID</lb></th>
                                    <td id="sGid" colspan="3"></td>
                                </tr>
                            </table>
                        </div>
                    </div>

					<hr class="hr-space">
					<hr class="hr-space">

                    <div class="col-lg-12">
                        <div>
                            <h4>▶ <lb class="lb-180"></lb></h4>
                        </div>
                        <div>
                            <div id="systemGrid" ></div>
                        </div>
                    </div>
                </div>
            </div>

            <!--modal-footer-->
            <div class="modal-footer-original" style="clear: both">
                <button id="btnClose" type="button" class="btn btn-danger btn-outline btnClose" data-dismiss="modal"><i class="fa fa-ban"></i> Close </button>
            </div>
            <!--modal-footer END-->
        </div>
    </div>
    <!--modal-content animated fadeIn END-->
<script>
$(document).ready(function() {
    //=======================================================================
    //(1) function 및 variable scope 설정 :: (메인은 main, 팝업 및 서브는 sub)
    //=======================================================================
    screenName = "CO-01-00-610";
    $.extend({
        sub : {
            //=======================================================================
            // (2) Screen 에서 사용할 variable 정의
            //=======================================================================
			detailKey : '',
            //=======================================================================
            // (3) Screen 에서 사용할 function 정의
            //=======================================================================


            //-----------------------------------------------------------------------
            // Description :: 화면 초기화
            //-----------------------------------------------------------------------
            fn_init : function() {
                try {
                    mint.init('$.sub.fn_initCallback',{isDraggable:false});
                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_init"});
                }
            },//end fn_init()

            //-----------------------------------------------------------------------
            // Description :: 화면 초기화 콜백
            //-----------------------------------------------------------------------
            fn_initCallback : function() {
                try{
                	$.sub.fn_initGridSystem();
                	$.sub.fn_initGridNode();
                	$.sub.fn_initDiagram();

                	$.sub.detailKey = window.opener.mint.common.getScreenParam('detailKey');

                	if( !mint.common.isEmpty($.sub.detailKey) ) {
                		$.sub.fn_setHeaderInfo();
                		$.sub.fn_getInterfaceInfo();
                		$.sub.fn_getTrackingDetail();
                		$.sub.fn_getTrackingDiagram();
                	}

                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_initCallback"});
                }
            },

            //-----------------------------------------------------------------------
            // Description :: fn_initGridSystem
            //-----------------------------------------------------------------------
            fn_initGridSystem : function() {
                try{
            		$("#systemGrid").kendoGrid({
						dataSource: {
							data: [],
							pageSize: mint.ui.getPageSizesS({'currentPage' : true})
						},
						selectable: true,
						scrollable: true,
						resizable: true,
						sortable: true,
						columns: [
							{
								field: "nodeTypeNm",
								title: mint.label.getLabel('lb-494'),
								width:"80px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=$.sub.fn_setData(nodeTypeNm)#",
							},
							{
								field: "systemNm",
								title: mint.label.getLabel('lb-82'),
								width: "180px;",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=$.sub.fn_setData(systemNm, systemCd)#",
							},
							{
								field: "resourceNm",
								title: mint.label.getLabel('lb-86'),
								width:"80px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=$.sub.fn_setData(resourceNm)#",
							},
							{
								field: "service",
								title: mint.label.getLabel('lb-87'),
								width:"200px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=$.sub.fn_setData(service)#",
							},
							{field:"systemCd", hidden:true},
						],
						dataBound: function (e) {
							if('undefined' != typeof $("#systemGrid").data()) {
								if(0 == $("#systemGrid").data().kendoGrid.dataSource.view().length) {
						    		$("#systemGrid").children(".k-grid-content").height('50');
						    	} else {
						    		$("#systemGrid").children(".k-grid-content").height('auto');
						    	}
							}
					    }

                    });
                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_initGridSystem"});
                }
            },

            //-----------------------------------------------------------------------
            // Description :: fn_initGrid
            //-----------------------------------------------------------------------
            fn_initGridNode : function() {
                try{
					$("#nodeListGrid").kendoGrid({
						dataSource: {
							data: [],
							pageSize: mint.ui.getPageSizesS({'currentPage' : true})
						},
						sortable: true,
						selectable: true,
						resizable: true,
						columns: [
							{
								field: "processId",
								title: mint.label.getLabel('lb-459'),
								width: "150px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								}
							},
							{
								field: "processMode",
								title: mint.label.getLabel('lb-469'),
								width: "100px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								}
							},
							{
								field: "hostId",
								title: mint.label.getLabel('lb-470') + mint.label.getLabel('lb-422'),
								width: "150px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								hidden:true
							},
							{
								field: "processTime",
								title: mint.label.getLabel('lb-471'),
								width: "110px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template : '#=$.sub.fn_changeTime(processTime)#',
							},
							{
								field: "turnaroundTime",
								title: "소요시간",
								width: "100px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=$.sub.fn_addPeriod(turnaroundTime)#"
							},
							{
								field: "status",
								title: mint.label.getLabel('lb-473'),
								width: "70px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=$.sub.fn_setColor(status)#",
							},
							{
								field: "mqmdQmgr",
								title: mint.label.getLabel('lb-263'),
								width: "110px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								}
							},
							{
								field: "recordCnt",
								title: "레코드 개수",
								width: "90px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=mint.common.addComma(recordCnt)#"
							},
							{
								field: "dataSize",
								title: mint.label.getLabel('lb-474'),
								width: "106px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								template: "#=mint.common.addComma(dataSize)#"
							},
							{
								field: "tGid",
								title: "T GID",
								width: "265px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								}
							},
							{
								field: "oGid",
								title: "O GID",
								width: "265px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								}
							},
							{
								field: "sGid",
								title: "S GID",
								width: "265px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								}
							},
							{
								field: "compressYn",
								title: mint.label.getLabel('lb-475'),
								width: "70px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								hidden:true
							},
							{
								field: "directoryNm",
								title: mint.label.getLabel('lb-899'),
								width: "250px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								hidden:true
							},
							{
								field: "fileNm",
								title: mint.label.getLabel('lb-916'),
								width: "250px",
								headerAttributes: {
									style: "text-align: center"
								},
								attributes: {
									style: "text-align: center"
								},
								hidden:true
							},
							{field:"errorMsg", hidden:true},
							{field:"msg", hidden:true},
							{field:"status", hidden:true}
						],
                        dataBound: function(e) {
							if('undefined' != typeof $("#nodeListGrid").data()) {
								if(0 == $("#nodeListGrid").data().kendoGrid.dataSource.view().length) {
						    		$("#nodeListGrid").children(".k-grid-content").height('50');
						    	} else {
						    		$("#nodeListGrid").children(".k-grid-content").height('auto');
						    	}
							}

							//-----------------------------------------------------------------------
							// Node Count 처리
							//-----------------------------------------------------------------------
							var totalCnt = 0;
							var completeCnt = 0;
							var errorCnt = 0;
							var totalTrunaroundTime = 0;

							var items = e.sender.dataItems();
							if( items.length > 0 ) {
								for(var i = 0; i < items.length; i++ ) {
									var status = items[i].status;
									if( status == "99" ) {
										errorCnt++;
									} else {
										completeCnt++;
									}
								}
								totalTurnaroundTime = items[0].totalTurnaroundTime;
							}

		                    totalCnt = completeCnt + errorCnt;

		                    $("#totalCnt").html(totalCnt);
		                    $("#completeCnt").html(completeCnt);
		                    $("#errorCnt").html(errorCnt);
		                    $("#totalTrunaroundTime").html($.sub.fn_addPeriod(totalTurnaroundTime));

                        }
                    });
                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_initGridNode"});
                }
            },

          //-----------------------------------------------------------------------
            // Description :: fn_setInterfaceInfo
            //-----------------------------------------------------------------------
            fn_setHeaderInfo : function() {
                try {

                	//-----------------------------------------------------------------------
                    // 기본 정보
                    //-----------------------------------------------------------------------
                    $("#rmsSvc").html( $.sub.detailKey.rmsSvc );
                    $("#rztRmsSvc").html( $.sub.detailKey.rztRmsSvc );
                    $("#oGid").html( $.sub.detailKey.oGid);
                    $("#tGid").html( $.sub.detailKey.tGid );
                    $("#sGid").html( $.sub.detailKey.sGid );

                } catch ( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setInterfaceInfo"});
                }
            },

            //-----------------------------------------------------------------------
            // Description :: fn_getInterfaceInfo
            //-----------------------------------------------------------------------
            fn_getInterfaceInfo : function() {
            	try {

            		if( mint.common.isEmpty($.sub.detailKey.interfaceId) ) {
            			return;
            		}

            		mint.callService( null, 'CO-01-00-610', 'REST-R02-CO-01-00-008',
            				function( jsonData ) {
								$.sub.fn_setInterfaceInfo( jsonData );
            				},
            				{ errorCall : true, params : { interfaceId : $.sub.detailKey.interfaceId } }
            		);

            	} catch ( e ) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_getInterfaceInfo"});
            	}
            },

            //-----------------------------------------------------------------------
            // Description :: fn_setInterfaceInfo
            //-----------------------------------------------------------------------
            fn_setInterfaceInfo : function(jsonData) {
                try {
                	if( !mint.common.isEmpty(jsonData) ) {
                        //-----------------------------------------------------------------------
                        // 기본 정보
                        //-----------------------------------------------------------------------
						$("#interfaceIdNm").html( jsonData.interfaceNm + "( " + jsonData.integrationId + " )");
                        if( !mint.common.isEmpty( jsonData.channel ) ) {
                            $("#channelNm").html( jsonData.channel.channelNm );
                        }
                        if( !mint.common.isEmpty( jsonData.businessList ) && jsonData.businessList.length > 0 ) {
                            $("#businessNm").html( jsonData.businessList[0].businessNm );
                        }
                        $("#appPrMethodNm").html( jsonData.appPrMethodNm );
                        $("#dataPrDirNm").html(  jsonData.dataPrDirNm );
                        $("#dataFrequency").html( jsonData.dataFrequency );
                        $("#dataPrMethodNm").html( jsonData.dataPrMethodNm );

                        //-----------------------------------------------------------------------
                        // 연계 시스템
                        //-----------------------------------------------------------------------
                        if( !mint.common.isEmpty(jsonData.systemList) ) {
                    		var dataSource = new kendo.data.DataSource({
								data: jsonData.systemList,
								schema: {
									model: {
										fields: {
											nodeTypeNm: {type: "String"},
											systemNm: {type: "String"},
											systemCd: {type: "String"},
											resourceNm: {type: "String"},
											service: {type: "String"}
										}
									}
								},
								page:1,
								pageSize:mint.ui.getPageSizesS({'currentPage' : true}),
								sort: { field: "nodeTypeNm", dir: "asc" }
      						});
      						$("#systemGrid").data("kendoGrid").setDataSource(dataSource);
                        }
                    }
                } catch ( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setInterfaceInfo"});
                }
            },

            //-----------------------------------------------------------------------
            // Description :: fn_getTrackingDetail
            //-----------------------------------------------------------------------
            fn_getTrackingDetail : function() {
            	try {
            		var requestObject = new Object();
            		mint.callService(requestObject, 'CO-01-00-610', 'REST-R02-OP-01-01-NH',
            				function( jsonData ) {
            					$.sub.fn_setTrackingDetail( jsonData );
            				},
            				{ errorCall : true, notificationView : false, params : {logKey1: $.sub.detailKey.hostId, logKey2: $.sub.detailKey.groupId, logKey3: $.sub.detailKey.integrationId, logKey4: $.sub.detailKey.msgDateTime} }
            		);
            	} catch ( e ) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_getTrackingDetail"});
            	}
            },

            //-----------------------------------------------------------------------
            // Description :: fn_setTrackingDetail
            //-----------------------------------------------------------------------
            fn_setTrackingDetail : function(jsonData) {
            	try {
            		var dataSource = new kendo.data.DataSource({
						data: jsonData,
                        schema: {
                            model: {
                                fields: {
                                    processMode: {type: "String"},
                                    hostId: {type: "String"},
                                    processTime: {type: "String"},
                                    status: {type: "String"},
                                    dataSize: {type: "String"},
                                    recordCnt: {type: "String"},
                                    compressYn: {type: "String"},
                                    errorMsg: {type: "String"},
                                    msgToString: {type: "String"},
                                    msg: {type: "String"},
                                    statusNm: {type: "String"},
                                    directoryNm: {type: "String"},
                                    fileNm: {type: "String"},
                                    turnaroundTime: {type: "String"},
                                    mqmdQmgr: {type: "String"}
                                }
                            }
                        },
						page:1,
						pageSize:mint.ui.getPageSizesS({'currentPage' : true}),
						sort: { field: "processTime", dir: "asc" }
					});
					$("#nodeListGrid").data("kendoGrid").setDataSource(dataSource);

            	} catch ( e ) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setTrackingDetail"});
            	}
            },


            //-----------------------------------------------------------------------
            // Description :: 하단 msg detail과 error msg를 세팅한다.
            //-----------------------------------------------------------------------
            fn_setMsg : function(data) {
                try {
                	var grid = $("#nodeListGrid").data("kendoGrid");
                    var selectedItem = grid.dataItem(grid.select());

                    $("#errorMsg").val( mint.common.isEmpty( selectedItem.errorMsg ) ? '' : selectedItem.errorMsg );

                   	$('#tab-detail-info a:eq(1)').tab('show');
                } catch ( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setMsg"});
                }
            },

            //-----------------------------------------------------------------------
            // Description :: node list의 상태 정보 색깔을 변경한다.
            //-----------------------------------------------------------------------
            fn_setColor : function(val) {
                try {
                	var result = "";
                    if (val && val != null && val == "99") {
                        result = "<b style='color:red'>" + "오류" + "</b>";
                    } else if (val && val != null && val == "01") {
                        result = "완료";
                    } else {
                        result = "완료";
                    }
                    return result;
                } catch ( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setColor"});
                }
            },

            fn_setData : function(val1, val2) {
                try {
                	var result = "";
               		if(!mint.common.isEmpty(val2)){
               			result = "<span title='" + val1 +"'>" + val1 + "(" + val2 + ")</span>";
               		} else {
               			result = "<span title='" + val1 +"'>" + val1 + "</span>";
               		}
                	return result;
                } catch ( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setData"});
                }
            },

            fn_addPeriod : function(val) {
                try {
                	if(val == '0') {
                		return '';
                	} else {
	                	return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + "(ms)";
                	}
                } catch ( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_addPeriod"});
                }
            },

          	//-----------------------------------------------------------------------
            // Description :: 그리드 시간 설정
            //-----------------------------------------------------------------------
            fn_changeTime : function(val) {
                try {
                    if(val != null && val != "") {
                        var str = val.substring(0, 17);
                        var returnVal = str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10) + ":" + str.substring(10, 12) + ":" + str.substring(12, 14) + "." + str.substring(14,17);
                        return returnVal;
                    } else {
                    	return "";
                    }
                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_changeTime"});
                }
            },

          	//-----------------------------------------------------------------------
            // Description :: fn_initDiagram
            //-----------------------------------------------------------------------
            fn_initDiagram: function(val) {
                try {
               	    $("#interfaceDiagram").kendoDiagram({
               	    	layout: {
               	            type: "tree",
               	            subtype: "right",
               	        },
               	        editable: false,
               	        zoom: 1,
               	        zoomRate: 0,
               	        connectionDefaults: {
	           	            stroke: {
	           	                color: "#979797",
	           	                width: 1
	           	            },
               	            type: "polyline",
               	            endCap: "ArrowEnd",
               	        },
               	    });
               	 	$("#interfaceDiagram").unbind("mousewheel");
                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_getTrackingDiagram"});
                }
            },

          	//-----------------------------------------------------------------------
            // Description :: fn_getTrackingDiagram
            //-----------------------------------------------------------------------
            fn_getTrackingDiagram: function(val) {
                try {
               		var requestObject = new Object();
               	    mint.callService(requestObject, 'CO-01-00-610', 'REST-R05-OP-01-01-NH', '$.sub.fn_setTrackingDiagram',
               	    		{
               	    			errorCall : true,
               	    			notificationView : false,
               	    			params : {
               	    				logKey1: $.sub.detailKey.hostId,
               	    				logKey2: $.sub.detailKey.groupId,
               	    				logKey3: $.sub.detailKey.integrationId,
               	    				logKey4: $.sub.detailKey.msgDateTime
               	    			}
               	    		}
               	    );

                } catch( e ) {
                    mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_getTrackingDiagram"});
                }
            },

          	//-----------------------------------------------------------------------
            // Description :: fn_setTrackingDiagram
            //-----------------------------------------------------------------------
            fn_setTrackingDiagram : function(jsonData) {
            	try {
					if(!mint.common.isEmpty(jsonData)){
	            	    var diagram = $("#interfaceDiagram").getKendoDiagram();
	            	    var myMap = new Map();
						var posX = 10;
						var posY = 10;

	            		var imgW = 32;
	            		var imgH = 32;

						var fontSize = '11';
						var preIndex = 0;
						var preMaxTextLength = 0;
						var nodeDistance = 150;

						var isTopY = false;

    	    			//-----------------------------------------------------------------------
    	    			// Node 배치
    	    			//-----------------------------------------------------------------------
            	    	for(var i=0; i< jsonData.nodeList.length;i++){

            	    		var nodeIndex   = jsonData.nodeList[i].nodeIndex;

            	    		if( nodeIndex == 0 ) {
            	    			//-----------------------------------------------------------------------
            	    			// 연결 정보가 없는 노드 정보는 최상위에 배열 한다.
            	    			//-----------------------------------------------------------------------
            	    			if( i == 0 ) {
            	    				posX = 10;
            	    			} else {
            	    				posX = posX + nodeDistance + ( preMaxTextLength <= 20 ? 0 : preMaxTextLength  * 4 );
            	    				preMaxTextLength = 0;
            	    			}
								posY = posY

								shapeX = posX;
								shapeY = posY;

								isTopY = true;
            	    		} else{
            	    			//-----------------------------------------------------------------------
            	    			// 연결 정보가 있는 노드 정보는 nodeIndex 에 따라 배열 한다.
            	    			//-----------------------------------------------------------------------

            	    			if( preIndex < nodeIndex ) {
            	    				//-----------------------------------------------------------------------
            	    				// 새로운 Index Group :: X 축값을 생성한다.
            	    				//-----------------------------------------------------------------------
            	    				if( nodeIndex == 1 ) {
            	    					posX = 10;
            	    					preMaxTextLength = 0;
            	    				} else {
            	    					//-----------------------------------------------------------------------
            	    					// X 값을 계산할때, 이전 노드의 텍스트를 감안하여 계산
            	    					//-----------------------------------------------------------------------
            	    					posX = posX + nodeDistance + ( preMaxTextLength <= 20 ? 0 : preMaxTextLength  * 4 );
            	    					preMaxTextLength = 0;
            	    				}
            	    				posY = isTopY ? 70 : 10;

            	    				shapeX = posX;
									shapeY = posY;
            	    			} else {
            	    				posX = posX;
            	    				posY = posY + 50;

            	    				shapeX = posX;
	            	    			shapeY = posY;
            	    			}
            	    		}

            	    		preIndex = nodeIndex;

            	    		if( preMaxTextLength < jsonData.nodeList[i].nodeNm.length ) {
            	    			preMaxTextLength = jsonData.nodeList[i].nodeNm.length;
            	    		}

	            	    	var image     = $.sub.fn_getStatusImage(jsonData.nodeList[i]);
	            	    	var textBlock = new kendo.dataviz.diagram.TextBlock({ text: jsonData.nodeList[i].nodeNm, x: 0, y: imgH,fontSize: fontSize});
            	    		var shape     = new kendo.dataviz.diagram.Shape({x: shapeX, y: shapeY, width: imgW, height: imgH , stroke:{width:0}});
	            	    	shape.visual.append(textBlock);
	            	    	shape.visual.append(image);

	            	    	var nodeShape = diagram.addShape(shape);
           	    			nodeShape.status = jsonData.nodeList[i].status;
	            	    	myMap.put(jsonData.nodeList[i].nodeId, nodeShape);
	            	    }

    	    			//-----------------------------------------------------------------------
    	    			// Node 간 Link 설정
    	    			//-----------------------------------------------------------------------
	            	    for(var i=0; i< jsonData.linkList.length;i++){

	            	    	var fromNode = myMap.get(jsonData.linkList[i].fromNodeId);
	            	    	var toNode   = myMap.get(jsonData.linkList[i].toNodeId);

							if( !mint.common.isEmpty(fromNode) && !mint.common.isEmpty(toNode) ) {
	            	    		var connectorDashType = 'dot';
	            	    		if( !mint.common.isEmpty(fromNode.status) && !mint.common.isEmpty(toNode.status) ) {
	            	    			connectorDashType = 'solid';
	            	    		}
								//diagram.connect(fromNode, toNode, {stroke:{dashType:connectorDashType}} );
							}

	            	    }

    	    			//-----------------------------------------------------------------------
    	    			// Diagram 에 Scroll 추가 하기 위한 로직
    	    			//-----------------------------------------------------------------------
	            	    {
		                    var bbox = diagram.boundingBox();

		                    var minW    = 836;
		                    var minH    = 175;
		                    var resizeW = bbox.width  + bbox.x + 50 + ( preMaxTextLength <= 10 ? 0 : preMaxTextLength  * 4 );
		                    var resizeH = bbox.height + bbox.y + 50;

		                    if( resizeW < minW ) {
		                    	diagram.wrapper.width(minW);
		                    } else {
		                    	diagram.wrapper.width(resizeW);
		                    }

		                    if( resizeH < minH ) {
		                    	diagram.wrapper.height(minH);
		                    } else {
		                    	diagram.wrapper.height(resizeH);
		                    }

		                    diagram.resize();
	            	    }
					}
            	}
            	catch(e) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_setTrackingDiagram"});
            	}
            }, // end fn_setTrackingDiagram


          	//-----------------------------------------------------------------------
            // Description :: fn_setTrackingDiagram
            //-----------------------------------------------------------------------
            fn_getStatusColor : function(statusCode) {
            	try {

		           	var fill_color ="";
           	    	if(statusCode =='00'){
            	    	var color =  "#" + kendo.Color.fromBytes(102, 112, 244, 1).toHex();
            	    	fill_color =color;
           	    	}else if(statusCode =='99'){
         	    		var color =  "#" + kendo.Color.fromBytes(219, 41, 41, 1).toHex();
            	    	fill_color =color;
           	    	}else if(statusCode =='01'){
            	    	var color =  "#" + kendo.Color.fromBytes(202, 202, 202, 1).toHex();
            	    	fill_color = color;
           	    	}
		           	return fill_color;
            	}
            	catch(e) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_getStatusColor"});
            	}
            }, // end fn_getStatusColor

          	//-----------------------------------------------------------------------
            // Description :: fn_setTrackingDiagram
            //-----------------------------------------------------------------------
            fn_getStatusImage : function(nodeInfo) {
            	try {
            		var imgW = 32;
            		var imgH = 32;

            		var s_img = '../../img/tracking/node_success.png';
            		var d_img = '../../img/tracking/node_nostatus.png';
            		var e_img = '../../img/tracking/node_error.png';

		           	var statusImage ;
           	    	if(nodeInfo.status =='00' || nodeInfo.status =='0'  || nodeInfo.status =='01'){
           	    		if(nodeInfo.nodeGubun == 'S') {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/send_userdata_end32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else if(nodeInfo.nodeGubun == 'R'){
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/receive_userdata_end32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else if(nodeInfo.nodeGubun == 'H') {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/hub_end32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else if(nodeInfo.nodeGubun == 'Q') {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/reply_end32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: s_img,x: 0,y: 0,width: imgW,height: imgH});
           	    		}
           	    	}else if(nodeInfo.status =='99'){
           	    		if(nodeInfo.nodeGubun == 'S') {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/send_userdata_error32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else if(nodeInfo.nodeGubun == 'R'){
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/receive_userdata_error32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else if(nodeInfo.nodeGubun == 'H') {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/hub_error32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else if(nodeInfo.nodeGubun == 'Q') {
           	    			statusImage =  new kendo.dataviz.diagram.Image({source: '../../icon/reply_error32.bmp',x: 0,y: 0,width: imgW,height: imgH});
           	    		} else {
	           	    		statusImage =  new kendo.dataviz.diagram.Image({source: e_img,x: 0,y: 0,width: imgW,height: imgH});
           	    		}
           	    	}else{
           	    		statusImage =  new kendo.dataviz.diagram.Image({source: d_img,x: 0,y: 0,width: imgW,height: imgH});
           	    	}
		           	return statusImage;
            	}
            	catch(e) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_getStatusImage"});
            	}
            }, // end fn_getStatusImage


          	//-----------------------------------------------------------------------
            // Description :: fn_nodeClickEvent
            //-----------------------------------------------------------------------
            fn_nodeClickEvent : function(e) {
            	try {
					//TODO
                    if(e.item instanceof kendo.dataviz.diagram.Shape) {
                    } else {
                    }
            	}
            	catch(e) {
            		mint.common.errorLog(e, {"screenName" : screenName, "fnName" : "$.sub.fn_nodeClickEvent"});
            	}
            }, // end fn_nodeClickEvent
        }
    });


    //=======================================================================
    // (4) 이벤트 핸들러 정의
    //=======================================================================
	//-----------------------------------------------------------------------
	// Description :: 노드리스트 그리드 Double Click 이벤트 (상세 조회)
	//-----------------------------------------------------------------------
    $('#nodeListGrid').on("click", "tr.k-state-selected", function(e) {
        $.sub.fn_setMsg();
    });

	//-----------------------------------------------------------------------
	// Description :: 그리드 가로 스크롤바 생성
	//-----------------------------------------------------------------------
	$('.tracking-diagram').slimScroll({
		width:'100%',
		height:'178px',
		axis: 'both',
		size:'10px',
		railVisible: false,
		railOpacity: 0.3,
		opacity: 0.3,
		color: '#908FCD'
	});

	$('.btnClose').on('click', function() {
        window.close();
    });

    //=======================================================================
    // (5) 기타 Function 정의
    //=======================================================================

    //=======================================================================
    // (6) 화면 로딩시 실행할 항목 기술
    //=======================================================================
   	window.focus();
    $.sub.fn_init();
    mint.label.attachLabel(null);
});//end document.ready()
</script>
</body>
</html>
