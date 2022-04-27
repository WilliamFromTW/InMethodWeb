var myDivClassName="DivRoleAuthorizedPermissionControl";
var myDivStatusIDName="changeFunctionInfoStatus";
var sFunctionName = "RoleAuthorizedPermissionControlServlet";
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
		$.getJSON("inmethod/auth/RoleListControlServlet", {"FlowID" : "getJsGridSelectOptions"}),
		$.getJSON("inmethod/auth/FunctionInfoControlServlet", {"FlowID" : "getJsGridSelectOptions"}),
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
          onItemInserted: function(grid){
        	     $('.'+myDivClassName).jsGrid("refresh");
           },
           onItemUpdating: function(grid){
             lastPrevItem = grid.previousItem;
           },
		    controller: {
		        loadData: function(filter) {
		            return $.grep(d, function(client) {
                        return  (!filter.FunctionRole || client.FunctionRole.indexOf(filter.FunctionRole) > -1)
                        && (!filter.FunctionName || client.FunctionName.indexOf(filter.FunctionName) > -1)
                        && (!filter.FunctionGroup || client.FunctionGroup.indexOf(filter.FunctionGroup) > -1)
                        && (!filter.FunctionDelete || client.FunctionDelete.indexOf(filter.FunctionDelete) > -1)
                        && (!filter.FunctionInsert || client.FunctionInsert.indexOf(filter.FunctionInsert) > -1)
                        && (!filter.FunctionUpdate || client.FunctionUpdate.indexOf(filter.FunctionUpdate) > -1)
                        && (!filter.FunctionQuery || client.FunctionQuery.indexOf(filter.FunctionQuery) > -1)
                        && (!filter.FunctionVisible || client.FunctionVisible.indexOf(filter.FunctionVisible) > -1);
		            });
		        },

		        deleteItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doDelete",
                        "FunctionName":ItemData.FunctionName,
                        "FunctionRole":ItemData.FunctionRole
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
                        "FunctionRole":ItemData.FunctionRole,
                        "FunctionName":ItemData.FunctionName,
                        "FunctionGroup":ItemData.FunctionGroup,
                        "FunctionDelete":ItemData.FunctionDelete,
                        "FunctionInsert":ItemData.FunctionInsert,
                        "FunctionUpdate":ItemData.FunctionUpdate,
                        "FunctionQuery":ItemData.FunctionQuery,
                        "FunctionVisible":ItemData.FunctionVisible
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
                        "FunctionRole":ItemData.FunctionRole,
                        "FunctionName":ItemData.FunctionName,
                        "FunctionGroup":ItemData.FunctionGroup,
                        "FunctionDelete":ItemData.FunctionDelete,
                        "FunctionInsert":ItemData.FunctionInsert,
                        "FunctionUpdate":ItemData.FunctionUpdate,
                        "FunctionQuery":ItemData.FunctionQuery,
                        "FunctionVisible":ItemData.FunctionVisible
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
		        { name: "FunctionRole", type: "select",css: "jsgrid",width: 20,editing:false,title:"角色",
		        	items:jsonJsGridSelectOptions1[0],
        		    textField: "Name",        	
        		    valueField: "Value"
	            },
		        { name: "FunctionName", type: "select",css: "jsgrid",width: 50,editing:false,title:"程式",
		        	items:jsonJsGridSelectOptions2[0],
        		    textField: "Name",        	
        		    valueField: "Value"
		        },
		        { name: "FunctionGroup", type: "text",css: "jsgrid",width: 25,title:"群組名稱",align: "center"},
		        { name: "FunctionDelete", type: "select",css: "jsgrid",width: 20,title:"刪除",
        		    items: [
	        		         { Name: "否", Id: "N" },
	        		         { Name: "是", Id: "Y" }
	        		    ],
	        		    valueField: "Id",
	        		    textField: "Name",
	        		    filtering: false
		        },
		        { name: "FunctionInsert", type: "select",css: "jsgrid",width: 20,title:"新增",
        		    items: [
	        		         { Name: "否", Id: "N" },
	        		         { Name: "是", Id: "Y" }
	        		    ],
	        		    valueField: "Id",
	        		    textField: "Name",
	        		    filtering: false
		        	
		        },
		        { name: "FunctionUpdate", type: "select",css: "jsgrid",width: 20,title:"修改",
        		    items: [
	        		         { Name: "否", Id: "N" },
	        		         { Name: "是", Id: "Y" }
	        		    ],
	        		    valueField: "Id",
	        		    textField: "Name",
	        		    filtering: false
	            },
		        { name: "FunctionQuery", type: "select",css: "jsgrid",width: 20,title:"查詢",
        		    items: [
	        		         { Name: "否", Id: "N" },
	        		         { Name: "是", Id: "Y" }
	        		    ],
	        		    valueField: "Id",
	        		    textField: "Name",
	        		    filtering: false
		        	
		        },
		        { name: "FunctionVisible", type: "select",css: "jsgrid",width: 20, title:"顯示",
		        		    items: [
		        		         { Name: "否", Id: "N" },
		        		         { Name: "是", Id: "Y" }
		        		    ],
		        		    valueField: "Id",
		        		    textField: "Name",
		        		    filtering: false
		        },
		        { type: "control",width: 5,editButton: JSON.parse(jsonAuthorized[0].Update),deleteButton: JSON.parse(jsonAuthorized[0].Delete)}
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

