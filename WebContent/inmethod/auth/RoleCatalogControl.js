var myDivClassName="DivRoleCatalogControl";
var myDivStatusIDName="changeRoleCatalogStatus";
var sFunctionName = "RoleCatalogControlServlet";
var sFunctionUrl = "inmethod/auth/"+sFunctionName;

if(typeof FunctionDivTableIDName !== "undefined"  && FunctionDivTableIDName!=null && FunctionDivTableIDName!=""){
	myDivClassName = FunctionDivTableIDName; 
}
function showRoleCatalogMessage(message,color){
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
$.when(
  $.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID" : "getAuthorizedFunctionInfo","FunctionName":sFunctionName})
).done( function(jsonAuthorized){
$.getJSON(sFunctionUrl, {
	"FlowID" : "doQuery"}, function(d) {
		$('.'+myDivClassName).jsGrid({
		    width: "100%",
		    height: "80%",
		    inserting: JSON.parse(jsonAuthorized.Insert),
		    filtering: true,
		    editing: JSON.parse(jsonAuthorized.Update),
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
                        return  (!filter.RoleCatalogId || client.RoleCatalogId.indexOf(filter.RoleCatalogId) > -1)
                        && (!filter.RoleCatalogDesc || client.RoleCatalogDesc.indexOf(filter.RoleCatalogDesc) > -1);
		            });
		        },

		        deleteItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doDelete",
                        "RoleCatalogId":ItemData.RoleCatalogId
		        			}, function(d) {
		        				if(typeof d.DeleteResult !== "undefined"){
		        				  if( d.DeleteResult=="OK"){
                                   result.resolve(ItemData);
                                   showRoleCatalogMessage("訊息提示:刪除成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("delete failed"));
                                   showRoleCatalogMessage("訊息提示:刪除失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("delete failed"));
                                 showRoleCatalogMessage("訊息提示:刪除失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showRoleCatalogMessage("訊息提示:刪除失敗","red");
		             });
                    return result.promise();
		        },		        
		        insertItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doAdd",
                        "RoleCatalogId":ItemData.RoleCatalogId,
                        "RoleCatalogDesc":ItemData.RoleCatalogDesc
		        			}, function(d) {

		        				if(typeof d.AddResult !== "undefined"){
		        				  if( d.AddResult=="OK"){
                                   result.resolve(ItemData);
                                   showRoleCatalogMessage("訊息提示:新增成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("add failed"));
                                   showRoleCatalogMessage("訊息提示:新增失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("add failed"));
                                 showRoleCatalogMessage("訊息提示:新增失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showRoleCatalogMessage("訊息提示:新增失敗","red");
		             });
                    return result.promise();
		        },
		        updateItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doUpdate",
                        "RoleCatalogId":ItemData.RoleCatalogId,
                        "RoleCatalogDesc":ItemData.RoleCatalogDesc
		        			}, function(d) {

		        				if(typeof d.UpdateResult !== "undefined"){
		        				  if( d.UpdateResult=="OK"){
                                   result.resolve(ItemData);
                                   showRoleCatalogMessage("訊息提示:更新成功","green");
		        				  }
		        				  else{
                                   result.resolve(lastPrevItem);
                                   showRoleCatalogMessage("訊息提示:更新失敗","red");
		        				  }
		        				}else{
                                 result.resolve(lastPrevItem);
                                 showRoleCatalogMessage("訊息提示:更新失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.resolve(lastPrevItem);
                        showRoleCatalogMessage("訊息提示:更新失敗","red");
		             });
                    return result.promise();
		        }
		    },

		    fields: [
		        { name: "RoleCatalogId",title: "角色分類",css: "jsgrid", type: "text",width: 30,editing:false,align: "center"},
		        { name: "RoleCatalogDesc",title: "角色分類名稱",css: "jsgrid", type: "text",width: 30,align: "center"},
		        { type: "control",width: 10,editButton: JSON.parse(jsonAuthorized.Update),deleteButton: JSON.parse(jsonAuthorized.Delete)}
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

