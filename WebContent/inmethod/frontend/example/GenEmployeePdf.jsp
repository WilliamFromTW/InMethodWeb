<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page
	import="org.apache.commons.fileupload.*,  java.text.*,java.util.*,java.io.*, java.sql.*"%>
<%@ page import="inmethod.commons.util.*"%>
<%@ page import="inmethod.auth.FunctionInfo,inmethod.auth.AuthFactory"%>
<%@ include file="/global.jsp"%>


<HTML>
<HEAD>
<%=getBaseHead(request, response)%>
<jsp:include page="/inmethod/index_template.jsp"></jsp:include>
<%
   if (!isLogin(request, response)) {
%>
<script>
   window.open("inmethod/index.jsp", "_self", "", "");
</script>

<%
   } else {
%>
</HEAD>
<BODY>
	<div align="center">
		<div class="fh3 d-flex justify-content-center">帳號資料(下載Excel)</div>
		<form id="myForm"  class="w-25">
		
			<div class="form-group row">
				<input type="text" class="form-control" id="UserID" required
					placeholder="%代表全部" />
			</div>
			<div class="form-group  float-end">
				<button type="button" id="PassToBackEnd" class="btn btn-dark  ">確定</button>
			</div>
		</form>
		<div id='Msg' align="center" class="w-25"></div>
	</div>
</BODY>

<script>
  window.jQuery || document.write('<%=addSlashes(getJavaScript())%>')
  // 修改以下程式名稱
  var sFunctionName = "GenEmployeePdf";   // 程式基本資料ID
  var sFunctionJspUrl = "inmethod/frontend/example/GenEmployeePdf.jsp";
  var sFunctionJsUrl = "inmethod/frontend/example/GenEmployeePdf.js";
  var sFunctionBackEndUrl = "inmethod/backend/example/GenEmployeePdf";
  
  var script = document.createElement("script");  // create a script DOM node
  script.src = sFunctionJsUrl;  // set its src to the provided URL
  document.head.appendChild(script);  // add it to the end of the head section of the page (could change 'head' to 'body' to add it to the end of the body section instead)
</script>
<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid.min.css" />
<link type="text/css" rel="stylesheet"
	href="jsgrid/jsgrid-theme.min.css" />
<script type="text/javascript" src="jsgrid/jsgrid.min.js"></script>
<%
   }
%>

</HTML>