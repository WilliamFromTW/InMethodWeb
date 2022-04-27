<%@ page import="javax.servlet.http.*,javax.mail.*,java.util.*,java.io.*"  language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="/global.jsp"%>
<%
/*
<jsp:include page="/inmethod/index.jsp"></jsp:include>
*/
%>
<%=getBaseHead(request, response)%>
<jsp:include page="/inmethod/index.jsp"></jsp:include>



<%
   if (!isLogin(request, response)) {
%>
<script>
   window.open("inmethod/login.jsp", "_self", "", "");
</script>

<%
   } else {
%>



<%=getJavaScript()%>
<table class="DivFunctionInfoControl"> </table>
<div id="changeFunctionInfoStatus"  style="display: none;" ></div>

<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid.min.css" />
<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid-theme.min.css" />
<script type="text/javascript" src="jsgrid/jsgrid.min.js"></script>

<script src="inmethod/test/template.js"></script>

<%
   }
%>
