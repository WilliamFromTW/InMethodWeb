<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page
	import="inmethod.*,org.apache.commons.fileupload.*,  java.text.*,java.util.*,java.io.*, java.sql.*"%>
<%@ page import="inmethod.commons.util.*"%>
<%@ page import="inmethod.auth.FunctionInfo,inmethod.auth.AuthFactory"%>
<%@ include file="/inmethod/global.jsp"%>

<HTML>
<HEAD>
<%=getBaseHead(request, response)%>

</HEAD>
<BODY>
<div class="container" >
<div class=" row  clickable-row">
<div class="col-sm-7">
<div class="h5 d-flex   justify-content-center" id="general-news"><font color='red'><%=I18N.getValue(request,response,"general-news")%></font></div>
		<div class="table-responsive-sm"  id="table-responsive">
			<table id="bootstrap-table" data-toggle="bootstrap-table"
				class="text-wrap table table-striped table-hover    table-sm " 
				  	data-page-size='5'  data-search="true"  data-show-search-clear-button="true"
				data-pagination="true">
				<thead class=" table-primary">
					<tr id="bootstrap-table-head-tr"></tr>
				</thead>
				<tbody>
					<tr id="bootstrap-table-tr">

					</tr>
					</tbody>
			</table>
		</div>
		
		</div>
		<div class="col-sm-5">
        <div class="h6 justify-content-center" id="news-content">&nbsp;<br/><br/></div>
		<div class="table-responsive"  ></div>
					<table  id="table-subject"" data-toggle="bootstrap-table3" 	 class="text-wrap table">
					</table>
		</div>
<div class="col-sm-7">
<div class="h5 d-flex  justify-content-center" id="other-news"><font color='red'><%=I18N.getValue(request,response,"other-news")%> </font></div>
		<div class="table-responsive-sm"  id="table-responsive2">
			<table id="bootstrap-table2" data-toggle="bootstrap-table2"
				class="text-wrap table table-striped  table-hover  table-bordered table-sm" 
			  	data-page-size='5'  data-search="true"  
				data-pagination="true">
					<thead class=" table-primary">
					<tr id="bootstrap-table-head-tr2"></tr>
				</thead>
				<tbody>
					<tr id="bootstrap-table-tr2">

					</tr>
					</tbody>
			</table>
		</div>
		
		</div>
		
		</div>
	
</div>
		
</BODY>

<script>
  window.jQuery || document.write('<%=addSlashes(getJavaScript())%>')
  
  // 修改以下程式名稱  
  var sFunctionName = "getNews";   // 程式基本資料ID
  var sFunctionJspUrl = "inmethod/news/getNews.jsp";
  var sFunctionJsUrl = "inmethod/news/getNews.js";
  var sFunctionBackEndUrl = "inmethod/news/NewsControlServlet";
  
  var script = document.createElement("script");  // create a script DOM node
  script.src = sFunctionJsUrl;  // set its src to the provided URL
  document.head.appendChild(script);  // add it to the end of the head section of the page (could change 'head' to 'body' to add it to the end of the body section instead)
</script>

<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid.min.css" />
<link type="text/css" rel="stylesheet" href="jsgrid/jsgrid-theme.min.css" />
<link type="text/css" rel="stylesheet" href="css/table.css" />
<script type="text/javascript" src="jsgrid/jsgrid.min.js"></script>
</HTML>