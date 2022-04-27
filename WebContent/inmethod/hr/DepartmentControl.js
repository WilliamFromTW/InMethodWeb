var myDivClassName="DivDepartmentControl";
var myDivStatusIDName="changeDepartmentStatus";
var sFunctionName = "DepartmentControlServlet";
var sFunctionUrl = "inmethod/hr/"+sFunctionName;

if(typeof FunctionDivTableIDName !== "undefined"  && FunctionDivTableIDName!=null && FunctionDivTableIDName!=""){
	myDivClassName = FunctionDivTableIDName; 
}
function showDepartmentMessage(message,color){
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
let urlParams = new URLSearchParams(window.location.search);
let sCatalogValue = urlParams.get("Catalog");
//alert(sCatalogValue);
if( !(sCatalogValue!==null && sCatalogValue!== "undefined"))
   sCatalogValue = "";
else
   sFunctionName = sFunctionName +"?Catalog="+    sCatalogValue;


$.when(
        $.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID": "getCatalogSelection",  "Catalog": sCatalogValue}),
		$.getJSON("inmethod/hr/DepartmentControlServlet", {"FlowID" : "getJsGridSelectOptions",  "Catalog": sCatalogValue}),
		$.getJSON("inmethod/hr/EmployeeControlServlet", {"FlowID" : "getJsGridSelectOptions",  "Catalog": sCatalogValue}),
		$.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID" : "getAuthorizedFunctionInfo","FunctionName":sFunctionName})
).done( function(catalogData,jsonJsGridSelectOptions1,jsonJsGridSelectOptions2,jsonAuthorized){
$.getJSON(sFunctionUrl, {
	"FlowID" : "doQuery", "Catalog": sCatalogValue}, function(d) {
		$('.'+myDivClassName).jsGrid({
		    width: "100%",
		    height: "80%",
		    inserting: JSON.parse(jsonAuthorized[0].Insert),
		    filtering: true,
		    editing: JSON.parse(jsonAuthorized[0].Update),
		    sorting: true,
	        autoload: true,
	        paging: true,
	        pageSize: 10,
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
           deleteConfirm: function(item) { 
             return "Are you sure?";
           }, 
           onItemUpdating: function(grid){
             lastPrevItem = grid.previousItem;
           },
		    controller: {
		        loadData: function(filter) {
		            return $.grep(d, function(client) {
                        return  (!filter.DeptId || client.DeptId.indexOf(filter.DeptId) > -1)
                        && (!filter.DeptName || client.DeptName.indexOf(filter.DeptName) > -1)
                        && (!filter.DeptLeaderId || client.DeptLeaderId.indexOf(filter.DeptLeaderId) > -1)
                        && (!filter.ParentdeptId || client.ParentdeptId.indexOf(filter.ParentdeptId) > -1)
                        && (!filter.DeptValidate || client.DeptValidate.indexOf(filter.DeptValidate) > -1)
                        && (!filter.Catalog || client.Catalog.indexOf(filter.Catalog) > -1);
		            });
		        },

		        deleteItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doDelete",
                        "DeptId":ItemData.DeptId
		        			}, function(d) {
		        				if(typeof d.DeleteResult !== "undefined"){
		        				  if( d.DeleteResult=="OK"){
                                   result.resolve(ItemData);
                                   showDepartmentMessage("訊息提示:刪除成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("delete failed"));
                                   showDepartmentMessage("訊息提示:刪除失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("delete failed"));
                                 showDepartmentMessage("訊息提示:刪除失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showDepartmentMessage("訊息提示:刪除失敗","red");
		             });
                    return result.promise();
		        },		        
		        insertItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doAdd",
                        "DeptId":ItemData.DeptId,
                        "DeptName":ItemData.DeptName,
                        "DeptLeaderId":ItemData.DeptLeaderId,
                        "ParentdeptId":ItemData.ParentdeptId,
                        "DeptValidate":ItemData.DeptValidate,
                        "Catalog":ItemData.Catalog
		        			}, function(d) {

		        				if(typeof d.AddResult !== "undefined"){
		        				  if( d.AddResult=="OK"){
                                   result.resolve(ItemData);
                                   showDepartmentMessage("訊息提示:新增成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("add failed"));
                                   showDepartmentMessage("訊息提示:新增失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("add failed"));
                                 showDepartmentMessage("訊息提示:新增失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showDepartmentMessage("訊息提示:新增失敗","red");
		             });
                    return result.promise();
		        },
		        updateItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doUpdate",
                        "DeptId":ItemData.DeptId,
                        "DeptName":ItemData.DeptName,
                        "DeptLeaderId":ItemData.DeptLeaderId,
                        "ParentdeptId":ItemData.ParentdeptId,
                        "DeptValidate":ItemData.DeptValidate,
                        "Catalog":ItemData.Catalog
		        			}, function(d) {

		        				if(typeof d.UpdateResult !== "undefined"){
		        				  if( d.UpdateResult=="OK"){
                                   result.resolve(ItemData);
                                   showDepartmentMessage("訊息提示:更新成功","green");
		        				  }
		        				  else{
                                   result.resolve(lastPrevItem);
                                   showDepartmentMessage("訊息提示:更新失敗","red");
		        				  }
		        				}else{
                                 result.resolve(lastPrevItem);
                                 showDepartmentMessage("訊息提示:更新失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.resolve(lastPrevItem);
                        showDepartmentMessage("訊息提示:更新失敗","red");
		             });
                    return result.promise();
		        }
		    },

		    fields: [
			   {
                        name: "Catalog",
                        title: "Catalog",
                        css: "jsgrid",
                        type: "select",
                        width:20,
                        validate: "required",
                        items: catalogData[0] ,
                        textField: "Name",
                        valueField: "Value"
                 },			
		        { name: "DeptId",title: "部門代號(Dept. ID)",css: "jsgrid", type: "text",width: 30,editing:false,align: "center",validate: "required",},
		        { name: "DeptName",title: "部門名稱(Dept. Name)",css: "jsgrid", type: "text",width: 30,align: "center",validate: "required"},
		        { name: "DeptLeaderId",title: "部門Leader_ID",css: "jsgrid", type: "select",width: 30,
		        	items:jsonJsGridSelectOptions2[0],
        		    textField: "Name",        	
        		    valueField: "Value"
		        	
		        },
		        { name: "ParentdeptId",title: "上層部門代號(Leader Dept. Name)",css: "jsgrid", type: "select",width: 30,
		        	
		        	items:jsonJsGridSelectOptions1[0],
        		    textField: "Name",        	
        		    valueField: "Value"
		        },
		        { name: "DeptValidate",title: "有效(Validate)",css: "jsgrid", type: "select",width: 30,
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