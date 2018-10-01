<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../TagLib.jsp"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<title>我的读者卡</title>
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
</style>
</head>
<body>
	<form method="post" id="myform" name="myform" >
		<input type="hidden" name="openid" value="${openid}">
		<div id="baseinfo" class="tabcontent" style="height:30%;width:908px;margin:0 auto">
			<img border="0" style="float:left" src="${ctx}/images/table_top.jpg">
			<br>
			<div class="title" style="margin:8px 0;font-size:35px;font-weight:bold;color:#15589f;">读者信息</div>
			<table width="90%" cellspacing="2" cellpadding="4" border="0">
				<tbody>
				<tr>
					<td valign="middle" bgcolor="#f4fcff" align="left" style="font-size:30px;">读者卡号:</td>
					<td valign="middle" bgcolor="#f4fcff" align="left" colspan="2" style="font-size:30px;">
						<input type="text" name="cardno" style="height:30px;width:70%;">
					</td>
				</tr>
				
				<tr>
					<td bgcolor="#FFFFFF" align="left" style="font-size:30px;">密码:</td>
					<td valign="middle" bgcolor="#FFFFFF" align="left" colspan="2" style="font-size:30px;">
						<input type="text" name="password" style="height:30px;width:70%;">
					</td>
				</tr>
				
				<tr>
					<td style="text-align:center;" colspan="3">
						<input id="b1" type="button" value="登录" class="logbtn" onclick="f1()"/>
					</td>
				</tr>
				</tbody>
			</table>
			<br>
			<img border="0" style="float:left" src="${ctx}/images/table_bottom.jpg">
		</div>
	</form>
	
<script>
	function f1() {
		document.getElementById('myform').action='${ctx}/client/bind';
		document.getElementById("myform").submit();
	}
</script>
</body>
</html>