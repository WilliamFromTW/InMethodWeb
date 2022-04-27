var myDivName="passwordContents";

if(typeof FunctionDivClassName !== "undefined"  && FunctionDivClassName!=null && FunctionDivClassName!=""){
	myDivName = FunctionDivClassName; 
}


$(function() {
	$("."+myDivName).html("" +
       "<div  style=\"width:400px;margin-top: 50px;padding-right: 15px;padding-left: 15px;margin-right: auto;margin-left: auto;\">"+
       "<h2>Change Password</h2>"+
       "<input type=\"password\" id=\"c_password\" class=\"form-control password \"placeholder=\"CurrentPassword\" autofocus=\"\">"+
       "<p></p>"+
       "<input type=\"password\" id=\"n_password\" class=\"form-control password \"placeholder=\"NewPassword\" >"+
       "<p></p>"+
       "<input type=\"password\" id=\"r_password\" class=\"form-control password \"placeholder=\"RePassword\" >"+
       "<P></P>"+
       "<button type=\"button\" id=\"changePassword\" class=\"btn btn-lg btn-primary btn-block\" >Confirm</button>"+
		"<p></p></div>");	
			
	$("#changePassword").click(function() {
		$("#changeStatus").css({"width":"370px","margin":"auto"});
		var c_password = $("#c_password").val()
		var n_password = $("#n_password").val()
		var r_password = $("#r_password").val()
		if(c_password=="" || n_password=="" || r_password==""){
			showMessage("訊息提示:欄位不得為空白","red")
		return false
		}else if(n_password != r_password){
			showMessage("訊息提示:新密碼與確認密碼必須相同","red")
		return false}
		
		$.getJSON("inmethod/auth/AuthenticationControlServlet", {
			"FlowID" : "Update",
			OldPassword : $("#c_password").val(),
			NewPassword : $("#n_password").val()
			}, function(data) {
			if (data.Update == "OK") {
				showMessage("訊息提示:修改成功","green")
				} else {
				showMessage("訊息提示:舊密碼不符","red")
						}
					});
		
		});
});

