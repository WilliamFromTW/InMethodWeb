<%@page import="inmethod_custom.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/inmethod/global.jsp"%>

<html>
<head>
<%=getBaseHead(request, response)%>
<link rel="stylesheet" type="text/css"
	href="jsgrid/jsgrid-theme.min.css" />
<link rel="stylesheet" type="text/css" href="jsgrid/jsgrid.min.css" />

</head>
<style>
.navbar-default {
	background-color: #F0FFF0;
}

.navbar-default .navbar-nav>.open>a, .navbar-default .navbar-nav>.open>a:focus,
	.navbar-default .navbar-nav>.open>a:hover {
	background-color: #F0FFF0;
}
.dropdown-menu li {
position: relative;
}
.dropdown-menu .dropdown-submenu {
display: none;
position: absolute;
left: 50%;
top: -7px;
}
.dropdown-menu .dropdown-submenu-left {
right: 50%;
left: auto;
}
.dropdown-menu > li:hover > .dropdown-submenu {
display: block;
}

</style>


<body>
	<nav class="navbar navbar-expand-lg navbar-lite " style="background-color: #e3f2fd;"">
		<div class="container-fluid">
			<a class="navbar-brand" href="#"> <img src="images/brand.png"> <span
				class="navbar-text"><span class="align-middle"> <%=I18N.getValue(request,response,"SiteName")%></span></span></a>
			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#navbarNavDarkDropdown"
				aria-controls="navbarNavDarkDropdown" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			     <ul class="navbar-nav">
			  <li class="nav-item">
          <a class="nav-link" href="inmethod/index.jsp?Page=doc/getDoc.jsp"><%=I18N.getValue(request, response, "DocName") %></a>
        </li>
        </ul>
			<div class="collapse navbar-collapse" id="navbarNavDarkDropdown">

				<ul class="navbar-nav mr-auto" id="AuthorizedFunctionList">
				</ul>

			</div>
			<div class="collapse navbar-collapse" id="navbarNavDarkDropdown2">

				<%
				if (!isLogin(request, response)) {
				%>
	        <ul class="navbar-nav navbar-right mr-auto">
				<li class="nav-item"><span class="align-middle navbar-text"><%=I18N.getValue(request,response,"Account")%> : <input type="text" id="j_username" ></span>
				</li>
				<li class="nav-item"><span class="align-middle navbar-text">&nbsp;<%=I18N.getValue(request,response,"Password")%> : <input type="password" id="j_password"></span>
				</li>
				<li class="nav-item"><button type="button" id="Login" class="btn-primary"><%=I18N.getValue(request,response,"Login")%></button>
				</li>
			</ul>

			<%
			} else {
			%>
			<ul class="navbar-nav navbar-right mr-auto">
				<li class="nav-item"><span class="align-middle navbar-text"><%=getLoginUserName(request, response)%>&nbsp;</span>
				</li>
				<li class="nav-item"><button type="button" id="Logout" class="btn-primary"><%=I18N.getValue(request,response,"Logout")%></button>
				</li>
			</ul>
			<%
			} ;
			%>
		</div>
		</div>
	</nav>
	<div class="body-content" ></div>
	<div id="changeStatus" style="display: none;"></div>

<script>
  window.jQuery || document.write('<%=addSlashes(getJavaScript())%>')
</script>

<script type="text/javascript" src="jsgrid/jsgrid.min.js"></script>
<script src="inmethod/index_template.js"></script>


<script>//依照json數值排序
	var obj = jQuery.parseJSON('<%=getJsonAuthorizedFunctionInfoList(request, response)%>');
</script>
<div id='includeCustomScript'></div>
</body>
</html>
