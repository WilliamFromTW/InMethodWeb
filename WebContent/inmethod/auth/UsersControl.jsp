<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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

<table class="DivUsersControl"> </table>
<div id="changeUsersStatus"  style="display: none;" ></div>
<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid.min.css" />
<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid-theme.min.css" />
<script type="text/javascript" src="jsgrid/jsgrid.min.js"></script>

<script src="inmethod/auth/UsersControl.js"></script>

<%
   }
%>
