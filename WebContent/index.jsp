<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<% 
  String sPageUrl = request.getParameter("Page");
  if(sPageUrl==null || sPageUrl.equals(""))
	  sPageUrl = "/inmethod/index.jsp";
%>
<jsp:include page="/inmethod/index.jsp"></jsp:include>
