<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../TagLib.jsp"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<title>已登录</title>
<script type="text/javascript" src="${ctx}/js/jquery-1.8.3.js"></script>
<style type="text/css">
.logbtn {
	cursor: pointer;
	width: 90px;
	height: 50px;
	color: #fff;
	font-size: 30px;
	background-color: #008094;
	border: 0px;
	margin: 15px 0px 0 40px;
}

.logbtn:hover {
	background-position: 0px -48px;
}

#t1 td {
	height: 45px;
	font-size:30px;
}

table #t1 {
	text-align: center;
	color: #222;
	font: 14px/20px Arial, "宋体";
	background-color: #fff;
}
ul,li,ol,dt,dd { list-style:none; }
 .myoptline { margin:0px; border-top:1px solid #e5e5e5; padding:5px 0px; position:relative; }
  .myoptline:hover { background-color:#f5f5f5; }
     .myoptline li { padding:2px 12px; font-size:30px;}
     .date { color:#999; font-size:12px;}
	 .myoptline h3 { margin-bottom:5px;}
 	.myoptline li.moreoptbox { position:relative; padding:0px; height:5px;}
     .moreopt, .showbottom, .hidebottom  { position:absolute; display:block; right:5px; top:-30px; width:48px; height:48px; background:url(../images/moreopt.png) no-repeat center  center; background-size:24px 24px; text-indent:-9999px; overflow:hidden; } 
	 .moreoptbox  .moreopt:hover,span.moreopt:active { background-image:url(../images/moreopt-h.png);}
	   .showbottom { background-image:url(../images/icon_downArrow.png); background-size:18px 10px; }
	   .hidebottom { background-image:url(../images/icon_topArrow3.png); background-size:18px 10px; }
       .hidebottom:hover, .showbottom:hover { background-color:#e5e5e5; border-radius:50px;}
	 
 	 .myoptline li.findsame { height:20px; position:relative; }
 	 .hideoptbox { position:absolute; z-index:999; top:18px; width:100%; height:48px; background-color:#f8f8f8; border:1px solid #e8e8e8; display:none; }
	 .hideoptbox span { border-left:1px solid #e5e5e5; width:50%; height:48px; line-height:48px; display:block; float:left; text-align:center; margin-left:-1px; color:#666; cursor:pointer; }
	  .optthree span { width:33.3%; }
	 .hideoptbox span:hover,.hideoptbox span:active { background-color:#a2d4fc; color:#2a83d3;}
	 .tongji { background-color:#F8f8f8; border-width:1px 0px; line-height:40px; padding: 30px 10px 0px 15px; color:#0000FF; position:relative; font-weight:bold; font-size:30px;}
</style>
</head>
<body>
	<form method="post" id="myform" name="myform" >
		<input type="hidden" name="openid" value="${openid}">
		<input type="hidden" name="cardno" value="${cardno}">
		<div id="baseinfo" class="tabcontent" style="width:908px;margin:0 auto">
			<img border="0" style="float:left" src="${ctx}/images/table_top.jpg">
			<br>
			<br>
			<div class="title" style="margin:8px 0;font-size:35px;font-weight:bold;color:#15589f;">读者信息</div>
			<table width="90%" cellspacing="2" cellpadding="4" border="0">
				<tbody>
				<tr>
					<td valign="middle" bgcolor="#f4fcff" align="left" style="font-size:30px;">读者卡号:</td>
					<td valign="middle" bgcolor="#f4fcff" align="left" colspan="2">
						<input type="text" name="cardno" style="height:30px;width:70%" value="${cardno}" disabled="disabled">
					</td>
				</tr>
				
				<tr>
					<td style="text-align:center" colspan="3">
						<input id="b1" type="button" value="退出" class="logbtn" onclick="f1()"/>
					</td>
				</tr>
				</tbody>
			</table>
			<br>
			<img border="0" style="float:left" src="${ctx}/images/table_bottom.jpg">
		</div>
	</form>
	<div width="80%" style="width:90%;margin-left:auto;margin-right:auto">
	<!-- <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="center" valign="middle">  -->
				<table id="t1" bordercolor="#a0c6e5" border="1" style="border:1;border-collapse:collapse;width:100%;">
					<tr>
						<td width="30%" align="center">姓名：</td>
						<td width="70%" align="center">${name}</td>
					</tr>
					
					<tr>
						<td width="30%" align="center">读者类型：</td>
						<td width="70%" align="center">${cardtype}</td>
					</tr>
					
					<tr>
						<td width="30%" align="center">余额：</td>
						<td width="70%" align="center">${sum}</td>
					</tr>
				</table>
				
			<!-- </td>
		</tr>
	</table> -->
	<div class="tongji">当前借阅 </div>
	<div id="curdata">
	
	<!--  <ul class="myoptline">
<li><h3>危险关系</h3>
<li class="textsize12 textgray">作者： 伦纳德 </li>
<li class="textsize12 textgray">索书号：1712.45  </li>
<li class="textsize12 textgray">到期时间：20170829  </li>
</ul>
 
<ul class="myoptline">
<li><h3>曾许诺 </h3>
<li class="textsize12 textgray">作者： 桐华  </li>
<li class="textsize12 textgray">索书号：1712.45  </li> 
<li class="textsize12 textgray">到期时间：20170829  </li>
</ul>
 -->

</div>
	</div>
<script>
	function f1() {
		document.getElementById('myform').action='${ctx}/client/unbind';
		document.getElementById("myform").submit();
	}
	$(function(){
		$.ajax({
			url:"${ctx}/client/curload",
			type:'GET',
			async:false,
			data: {"cardno":"${cardno}","rand":Math.random()},
			success:function(data){
				$("#curdata").empty();
				var obj = $.parseJSON(data); 
				if(obj.length==0){
					$(".tongji").html("暂无外借图书");
				}else{
					$(".tongji").html("当前借阅"+obj.length+"本");
				}
				var str = "";
				for ( var item in obj) {
					str += "<ul class='myoptline'><li><h3>"+obj[item].title+" </h3><li class='textsize12 textgray'>作者： "+obj[item].author+" </li><li class='textsize12 textgray'>索书号："+obj[item].ssh+"</li> <li class='textsize12 textgray'>到期时间："+obj[item].dutedate+"</li>	</ul>";
				}
				$("#curdata").append(str)
			},
			error:function(data){
				alert(JSON.stringify(data))
			}
		});
	});
</script>
</body>
</html>