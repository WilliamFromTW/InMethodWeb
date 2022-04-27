<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="/global.jsp"%>

<%=getBaseHead(request, response)%>
<%
  String sPage = request.getParameter("Page");

%>

<jsp:include page="/inmethod/index_template.jsp"></jsp:include>

<script>
  window.jQuery || document.write('<%=addSlashes(getJavaScript())%>')
</script>
<div class="function-title  h3 d-flex justify-content-center" ></div>
<table class="DivNewsControl"> </table>
<div id="changeNewsStatus"  style="display: none;" ></div>

<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid.min.css" />
<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid-theme.min.css" />
<script type="text/javascript" src="jsgrid/jsgrid.min.js"></script>
<%
if( sPage==null || sPage.trim().equals("")){
%>
<jsp:include page="news/getNews.jsp"></jsp:include>
<%} else {%>
<jsp:include page="<%=sPage %>"></jsp:include>
<%}; %>