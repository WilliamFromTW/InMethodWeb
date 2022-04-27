<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/global.jsp"%>

<%
/*
<jsp:include page="/inmethod/index.jsp"></jsp:include>
*/
%>
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

<script>
  window.jQuery || document.write('<%=addSlashes(getJavaScript())%>')
</script>


<div class="passwordContents" ></div>
	

<script src="inmethod/auth/modifyPassword.js"></script>




<%
	}
%>

