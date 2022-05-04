<%@ page  import="javax.servlet.http.*,javax.mail.*,java.util.*,java.io.*" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="/inmethod/global.jsp"%>

<%

  Vector<String> aTo = new Vector<String>();
  aTo.add("peter.liu@hlmt.com.tw");
  
  Vector<String> aCc = new Vector<String>();
  // aCc can be null
  aCc.add("william.chen@hlmt.com.tw");
  
  
  sendmail("william.chen@hlmt.com.tw", aTo, aCc,"標題:InMethod email testing 許功蓋 ","內容:許功蓋");
%>