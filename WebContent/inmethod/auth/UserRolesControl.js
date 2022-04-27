var myDivClassName="DivUserRolesControl";
var myDivStatusIDName="changeFunctionInfoStatus";
var sFunctionName = "UserRolesControlServlet";
var sFunctionUrl = "inmethod/auth/"+sFunctionName;

if(typeof FunctionDivTableIDName !== "undefined"  && FunctionDivTableIDName!=null && FunctionDivTableIDName!=""){
	myDivClassName = FunctionDivTableIDName; 
}

function showFunctionInfoMessage(message,color){
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

$(function(){

var lastPrevItem;

var jsonJsGridSelectOptions1;
var jsonJsGridSelectOptions2;

$.when(
		$.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID" : "getAllUsers"}),
		$.getJSON("inmethod/auth/RoleListControlServlet", {"FlowID" : "getJsGridSelectOptions"}),
		$.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID" : "getAuthorizedFunctionInfo","FunctionName":sFunctionName})			
).done( function(jsonJsGridSelectOptions1,jsonJsGridSelectOptions2,jsonAuthorized){
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
                        && (!filter.RoleName || client.RoleName.indexOf(filter.RoleName) > -1);
		            });
		        },

		        deleteItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doDelete",
                        "UserName":ItemData.UserName,
                        "RoleName":ItemData.RoleName
		        			}, function(d) {
		        				if(typeof d.DeleteResult !== "undefined"){
		        				  if( d.DeleteResult=="OK"){
                                   result.resolve(ItemData);
                                   showFunctionInfoMessage("訊息提示:刪除成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("delete failed"));
                                   showFunctionInfoMessage("訊息提示:刪除失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("delete failed"));
                                 showFunctionInfoMessage("訊息提示:刪除失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showFunctionInfoMessage("訊息提示:刪除失敗","red");
		             });
                    return result.promise();
		        },		        
		        insertItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doAdd",
                        "UserName":ItemData.UserName,
                        "RoleName":ItemData.RoleName
		        			}, function(d) {

		        				if(typeof d.AddResult !== "undefined"){
		        				  if( d.AddResult=="OK"){
                                   result.resolve(ItemData);
                                   showFunctionInfoMessage("訊息提示:新增成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("add failed"));
                                   showFunctionInfoMessage("訊息提示:新增失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("add failed"));
                                 showFunctionInfoMessage("訊息提示:新增失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showFunctionInfoMessage("訊息提示:新增失敗","red");
		             });
                    return result.promise();
		        },
		        updateItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doUpdate",
                        "UserName":ItemData.UserName,
                        "RoleName":ItemData.RoleName
		        			}, function(d) {

		        				if(typeof d.UpdateResult !== "undefined"){
		        				  if( d.UpdateResult=="OK"){
                                   result.resolve(ItemData);
                                   showFunctionInfoMessage("訊息提示:更新成功","green");
		        				  }
		        				  else{
                                   result.resolve(lastPrevItem);
                                   showFunctionInfoMessage("訊息提示:更新失敗","red");
		        				  }
		        				}else{
                                 result.resolve(lastPrevItem);
                                 showFunctionInfoMessage("訊息提示:更新失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.resolve(lastPrevItem);
                        showFunctionInfoMessage("訊息提示:更新失敗","red");
		             });
                    return result.promise();
		        }
		    },

		    fields: [
		        { name: "UserName",title: "使用者",css: "jsgrid", type: "select",width: 30,editing:false,
		        	items:jsonJsGridSelectOptions1[0],
        		    textField: "Name",        	
        		    valueField: "Value"
	            },
		        { name: "RoleName",title: "擁有之角色",css: "jsgrid", type: "select",width: 30,editing:false,
		        	items:jsonJsGridSelectOptions2[0],
        		    textField: "Name",        	
        		    valueField: "Value"
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