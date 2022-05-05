var myDivClassName="DivUsersControl";
var myDivStatusIDName="changeUsersStatus";
var sFunctionName = "UsersControlServlet";
var sFunctionUrl = "inmethod/auth/"+sFunctionName;

if(typeof FunctionDivTableIDName !== "undefined"  && FunctionDivTableIDName!=null && FunctionDivTableIDName!=""){
	myDivClassName = FunctionDivTableIDName; 
}
function showUsersMessage(message,color){
  if(typeof FunctionDivStatusIDName !== "undefined"  && FunctionDivStatusIDName!=null && FunctionDivStatusIDName!=""){

    showMessage(message,color);
  }
  else{
	  if( color=="green"){
	    $('#'+myDivStatusIDName).removeClass().addClass("alert alert-success");
	    $('#'+myDivStatusIDName).html(message).slideDown(1000).fadeOut(3000);
	  }else if(color=='red'){
	    $('#'+myDivStatusIDName).removeClass().addClass("alert alert-danger");
	    $('#'+myDivStatusIDName).html(message).slideDown(1000).fadeOut(3000);

	  }

  }
};

var jsonJsGridSelectOptions1;

$.when(
		$.getJSON("inmethod/hr/EmployeeControlServlet", {"FlowID" : "getJsGridSelectOptions"}),
		$.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID" : "getAuthorizedFunctionInfo","FunctionName":sFunctionName})			
).done( function(jsonJsGridSelectOptions1,jsonAuthorized){
$(function(){

var lastPrevItem;

$.getJSON(sFunctionUrl, {
	"FlowID" : "doQuery"}, function(d) {
		$('.'+myDivClassName).jsGrid({
		    width: "100%",
		    height: "80%",
		    inserting: JSON.parse(jsonAuthorized[0].Insert),
		    filtering: true,
		    editing: JSON.parse(jsonAuthorized[0].Update),
		    sorting: true,
	        autoload: true,
	        paging: true,
	        pageSize: 15,
	        pageButtonCount: 5,
	        pagerFormat: "目前頁面: {pageIndex} &nbsp;&nbsp; {first} {prev} {pages} {next} {last} &nbsp;&nbsp; 全部: {pageCount} 頁",
	        pagePrevText: "<",
	        pageNextText: ">",
	        pageFirstText: "<<",
	        pageLastText: ">>",
	        pageNavigatorNextText: "&#8230;",
	        pageNavigatorPrevText: "&#8230;",
	        noDataContent: "無資料",
	        updateOnResize: true,
		    data: d,
           deleteConfirm: function(item) { 
             return "Are you sure?";
           }, 
           onItemInserted: function(grid){
       	     $('.'+myDivClassName).jsGrid("refresh");
           },
           onItemUpdating: function(grid){
             lastPrevItem = grid.previousItem;
           },
		    controller: {
		        loadData: function(filter) {
		            return $.grep(d, function(client) {
                        return  (!filter.UserName || client.UserName.indexOf(filter.UserName) > -1)
                        && (!filter.UserPass || client.UserPass.indexOf(filter.UserPass) > -1)
                        && (!filter.UserValidate || client.UserValidate.indexOf(filter.UserValidate) > -1)
                        && (!filter.UserDesc || client.UserDesc.indexOf(filter.UserDesc) > -1);
		            });
		        },

		        deleteItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doDelete",
                        "UserName":ItemData.UserName
		        			}, function(d) {
		        				if(typeof d.DeleteResult !== "undefined"){
		        				  if( d.DeleteResult=="OK"){
                                   result.resolve(ItemData);
                                   showUsersMessage("訊息提示:刪除成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("delete failed"));
                                   showUsersMessage("訊息提示:刪除失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("delete failed"));
                                 showUsersMessage("訊息提示:刪除失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showUsersMessage("訊息提示:刪除失敗","red");
		             });
                    return result.promise();
		        },		        
		        insertItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doAdd",
                        "UserName":ItemData.UserName,
                        "UserPass":ItemData.UserPass,
                        "UserValidate":ItemData.UserValidate,
                        "UserDesc":ItemData.UserDesc
		        			}, function(d) {

		        				if(typeof d.AddResult !== "undefined"){
		        				  if( d.AddResult=="OK"){
                                   result.resolve(ItemData);
                                   showUsersMessage("訊息提示:新增成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("add failed"));
                                   showUsersMessage("訊息提示:新增失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("add failed"));
                                 showUsersMessage("訊息提示:新增失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showUsersMessage("訊息提示:新增失敗","red");
		             });
                    return result.promise();
		        },
		        updateItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doUpdate",
                        "UserName":ItemData.UserName,
                        "UserPass":ItemData.UserPass,
                        "UserValidate":ItemData.UserValidate,
                        "UserDesc":ItemData.UserDesc
		        			}, function(d) {

		        				if(typeof d.UpdateResult !== "undefined"){
		        				  if( d.UpdateResult=="OK"){
                                   result.resolve(ItemData);
                                   showUsersMessage("訊息提示:更新成功","green");
		        				  }
		        				  else{
                                   result.resolve(lastPrevItem);
                                   showUsersMessage("訊息提示:更新失敗","red");
		        				  }
		        				}else{
                                 result.resolve(lastPrevItem);
                                 showUsersMessage("訊息提示:更新失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.resolve(lastPrevItem);
                        showUsersMessage("訊息提示:更新失敗","red");
		             });
                    return result.promise();
		        }
		    },
		    fields: [
		        { name: "UserName",title: "帳號",css: "jsgrid", type: "select",width: 30,editing:false,align: "left",
		        	items:jsonJsGridSelectOptions1[0],
        		    textField: "Name",        	
        		    valueField: "Value"
		        },
		        { name: "UserPass",title: "密碼",css: "jsgrid", type: "text",width: 30,align: "center"},
		        { name: "UserDesc",title: "備註",css: "jsgrid", type: "text",width: 30,align: "left"},
		        { name: "UserValidate",title: "是否有效",css: "jsgrid", type: "select",width: 20,
        		    items: [
	        		         { Name: "否", Id: "N" },
	        		         { Name: "是", Id: "Y" }
	        		    ],
	        		    valueField: "Id",
	        		    textField: "Name",
	        		    filtering: false
	        	},	        
		        { type: "control",width: 10,editButton: JSON.parse(jsonAuthorized[0].Update),deleteButton: JSON.parse(jsonAuthorized[0].Delete)}
		    ]
		});

	}
).fail(function(d){
    if(d.responseJSON.NoLogin=="FAIL"){
       showMessage("No Login!","red");

    }else if(d.responseJSON.NoPermission=="FAIL"){
       showMessage("No Permission!","red");
    };
});			

})
})