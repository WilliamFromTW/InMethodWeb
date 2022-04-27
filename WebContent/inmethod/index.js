var FunctionDivBodyContent = "body-content";//含 left,right
var FunctionDivClassName = "body-content";
var FunctionDivStatusIDName = "changeStatus";
var FunctionDivTableIDName = "body-content";
var FunctionDivTitle = "function-title";


$(function(){
	
	getAuthorizedFunctionList(obj);
	$("#Login").click(function(){
				login()
				});
	$("#j_password").keydown(function(e){
				if(e.keyCode == 13){
					login();
				}
			});
});

function login() {
	$.getJSON("inmethod/auth/AuthenticationControlServlet", {
		"FlowID" : "Login",
		j_username : $("#j_username").val(),
		j_password : $("#j_password").val()
			}, function(data) {
				if (data.Login == "OK") {
					location.href="inmethod/index.jsp"
					}
				}).fail(function(data){
					$("#Login").text("Fail Login").addClass( "failLogin");
					setTimeout(function () {$("#Login").removeClass( "failLogin").text("Sign in")}, 500);
				});
};
/*
 * @param message text for toast
 * @param color "red":show red color, "blue":show blue color
 * @defuatl color:green
 */
function showMessage(message,color){
	if( color=="red"){
		$( "#changeStatus" ).removeClass().addClass( "alert alert-danger" );
	}	
	else if(color=="green"){
	  $( "#changeStatus" ).removeClass().addClass( "alert alert-success" );
	}else{
		$( "#changeStatus" ).removeClass().addClass( "alert alert-info" );
	}
	$("#changeStatus").html(message).slideDown(1000).fadeOut(3000);
};
$("#Logout").click(function(e) {
	e.preventDefault();
	$.getJSON("inmethod/auth/AuthenticationControlServlet", {
		"FlowID" : "Logout"
			}, function(data) {
				if (data.Logout == "OK") {
					location.href="inmethod/index.jsp"
				} else {
					$("#LoginStatus").html(
					"<font color=red> Logout " + data.Logout
							+ "!</font>");
						};
				});
		});
function getAuthorizedFunctionList(obj){
	if( obj!=null){
		if( obj.NoPermission != null)
		  if( obj.NoPermission=='FAIL') return;
		 
	}else return; 
	  var sortedObj = sortJSON(obj,'FunctionGroup','123');
	  var htmlAuthorizedFunctionList = "";
	  var currentGroup = null;
	  var bNewGroup = true; 
	  var currentGroup2 = null;
	  var bNewGroup2 = true; 
	  var iGroupID = 0;
	  var aGroupArray = null;
	  for(var i=0;i<sortedObj.length;i++){
		aGroupArray = sortedObj[i].FunctionGroup.split("-");
		if( currentGroup==null || currentGroup!=aGroupArray[0] ){			
			currentGroup = aGroupArray[0];
			bNewGroup = true;
		}else{
			bNewGroup = false;
		}
		
		if(bNewGroup){
			iGroupID++;
			if( htmlAuthorizedFunctionList!="" ){
			  if( currentGroup2!=null )  
			  htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +"</ul></li></ul></li>";
			  else
			  htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +"</ul></li>";
			  currentGroup2 = null;
			}  
			htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +
			    "<li class=\"nav-item  dropdown\"><a class=\"nav-link dropdown-toggle\" href=\"#\" id=\"navbarDarkDropdownMenuLink"+iGroupID+
			    "\" role=\"button\"  aria-expanded=\"false\" data-bs-toggle=\"dropdown\" >" +currentGroup+ 
			    "</a><ul aria-labelledby=\"navbarDarkDropdownMenuLink"+iGroupID+ 
			    "\" class=\"dropdown-menu\">"
		}
		
		  if( aGroupArray[1]!=null){
			if( currentGroup2==null || currentGroup2!=aGroupArray[1] ){			
			  currentGroup2 = aGroupArray[1];
			  bNewGroup2 = true;
		    }else{
			  bNewGroup2 = false;
		    }
		    if(bNewGroup2){
			  if( !bNewGroup)
			    htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +"</ul></li>";
			    iGroupID++
			  htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +
		                             "<li class=\"nav-item  dropdown\"><a class=\"dropdown-toggle dropdown-item\"  role=\"button\" >" + aGroupArray[1] + "</a>"+
		                              "<ul class=\"dropdown-menu dropdown-submenu\">";
		    }
		      htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +
		                             "<li ><a class=\"dropdown-item\" href=\""+sortedObj[i].FunctionUrl+"\">" + sortedObj[i].FunctionDesc + "</a></li>"
		  }else{		 
            currentGroup2 = null;
		    htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +
		                             "<li ><a class=\"dropdown-item\" href=\""+sortedObj[i].FunctionUrl+"\">" + sortedObj[i].FunctionDesc + "</a></li>"
		  }
		
	  }
	  htmlAuthorizedFunctionList = htmlAuthorizedFunctionList +"</ul></li>";
	  $("#AuthorizedFunctionList").append(htmlAuthorizedFunctionList);
};