var myDivClassName="DivEmployeeControl";
var myDivStatusIDName="changeEmployeeStatus";
var sFunctionName = "EmployeeControlServlet";
var sFunctionUrl = "inmethod/hr/"+sFunctionName;

if(typeof FunctionDivTableIDName !== "undefined"  && FunctionDivTableIDName!=null && FunctionDivTableIDName!=""){
	myDivClassName = FunctionDivTableIDName; 
}
function showEmployeeMessage(message,color){
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
		$.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID" : "getAuthorizedFunctionInfo","FunctionName":sFunctionName})
).done( function(catalogData,jsonJsGridSelectOptions1,jsonAuthorized){
	$(document).ready(function () {
		if( jsonAuthorized[0].Query=='false')
       showMessage("No Permission","red");
		else
$.getJSON(sFunctionUrl, {
	"FlowID" : "doQuery", "Catalog": sCatalogValue }, function(d) {
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
	        pagerFormat: "Current: {pageIndex} &nbsp;&nbsp; {first} {prev} {pages} {next} {last} &nbsp;&nbsp; ALL: {pageCount} Page",
	        pagePrevText: "<",
	        pageNextText: ">",
	        pageFirstText: "<<",
	        pageLastText: ">>",
	        pageNavigatorNextText: "&#8230;",
	        pageNavigatorPrevText: "&#8230;",
	        noDataContent: "No Data",
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
                        return  (!filter.UserId || client.UserId.indexOf(filter.UserId) > -1)
                        && (!filter.UserName || client.UserName.indexOf(filter.UserName) > -1)
                        && (!filter.UserEnglishName || client.UserEnglishName.indexOf(filter.UserEnglishName) > -1)
                        && (!filter.UserDeptId || client.UserDeptId.indexOf(filter.UserDeptId) > -1)
                        && (!filter.UserMail || client.UserMail.indexOf(filter.UserMail) > -1)
                        && (!filter.UserOnboradDate || client.UserOnboradDate.indexOf(filter.UserOnboradDate) > -1)
                        && (!filter.UserDepartureDate || client.UserDepartureDate.indexOf(filter.UserDepartureDate) > -1)
                        && (!filter.UserLanguage || client.UserLanguage.indexOf(filter.UserLanguage) > -1)
                        && (!filter.Catalog || client.Catalog.indexOf(filter.Catalog) > -1);
		            });
		        },

		        deleteItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doDelete",
                        "UserId":ItemData.UserId
		        			}, function(d) {
		        				if(typeof d.DeleteResult !== "undefined"){
		        				  if( d.DeleteResult=="OK"){
                                   result.resolve(ItemData);
                                   showEmployeeMessage("訊息提示:刪除成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("delete failed"));
                                   showEmployeeMessage("訊息提示:刪除失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("delete failed"));
                                 showEmployeeMessage("訊息提示:刪除失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showEmployeeMessage("訊息提示:刪除失敗","red");
		             });
                    return result.promise();
		        },		        
		        insertItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doAdd",
                        "UserId":ItemData.UserId,
                        "UserName":ItemData.UserName,
                        "UserEnglishName":ItemData.UserEnglishName,
                        "UserDeptId":ItemData.UserDeptId,
                        "UserMail":ItemData.UserMail,
                        "UserOnboradDate":ItemData.UserOnboradDate,
                        "UserDepartureDate":ItemData.UserDepartureDate,
                        "UserLanguage":ItemData.UserLanguage,
                        "Catalog":ItemData.Catalog
		        			}, function(d) {

		        				if(typeof d.AddResult !== "undefined"){
		        				  if( d.AddResult=="OK"){
                                   result.resolve(ItemData);
                                   showEmployeeMessage("訊息提示:新增成功","green");
		        				  }
		        				  else{
                                   result.reject(new Error("add failed"));
                                   showEmployeeMessage("訊息提示:新增失敗","red");
		        				  }
		        				}else{
                                 result.reject(new Error("add failed"));
                                 showEmployeeMessage("訊息提示:新增失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.reject(new Error(s.responseText));
                        showEmployeeMessage("訊息提示:新增失敗","red");
		             });
                    return result.promise();
		        },
		        updateItem: function(ItemData) {
                   var result = $.Deferred();

		        	$.getJSON(sFunctionUrl, {
		        		"FlowID" : "doUpdate",
                        "UserId":ItemData.UserId,
                        "UserName":ItemData.UserName,
                        "UserEnglishName":ItemData.UserEnglishName,
                        "UserDeptId":ItemData.UserDeptId,
                        "UserMail":ItemData.UserMail,
                        "UserOnboradDate":ItemData.UserOnboradDate,
                        "UserDepartureDate":ItemData.UserDepartureDate,
                        "UserLanguage":ItemData.UserLanguage,
                        "Catalog":ItemData.Catalog
		        			}, function(d) {

		        				if(typeof d.UpdateResult !== "undefined"){
		        				  if( d.UpdateResult=="OK"){
                                   result.resolve(ItemData);
                                   showEmployeeMessage("訊息提示:更新成功","green");
		        				  }
		        				  else{
                                   result.resolve(lastPrevItem);
                                   showEmployeeMessage("訊息提示:更新失敗","red");
		        				  }
		        				}else{
                                 result.resolve(lastPrevItem);
                                 showEmployeeMessage("訊息提示:更新失敗","red");
		        				}
		        		})
		             .fail(function(s) {
                        result.resolve(lastPrevItem);
                        showEmployeeMessage("訊息提示:更新失敗","red");
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
                        width:10,
                        validate: "required",
                        items: catalogData[0] ,
                        textField: "Name",
                        valueField: "Value"
                 },
		        { name: "UserId",title: "Employee ID",css: "jsgrid", type: "text",width: 20,editing:false,align: "left"},
		        { name: "UserName",title: "Name",css: "jsgrid", type: "text",width: 30,align: "left"},
		        { name: "UserEnglishName",title: "English Name",css: "jsgrid", type: "text",width: 30,align: "left"},
		        { name: "UserDeptId",title: "Department ID",css: "jsgrid", type: "select",width: 20,align: "left",
		        	items:jsonJsGridSelectOptions1[0],
        		    textField: "Name",        	
        		    valueField: "Value"		        	
		        },
		        { name: "UserMail",title: "E-Mail",css: "jsgrid", type: "text",width:40,align: "left"},
		        { name: "UserOnboradDate",title: "Onboard Date",css: "jsgrid", type: "text",width: 20,align: "right"},
		        { name: "UserDepartureDate",title: "Departure Date",css: "jsgrid", type: "text",width: 20,align: "right"},
		        { name: "UserLanguage",title: "Language",css: "jsgrid", type: "select",width: 20,align: "left",
    	            items:[
	                     {"Name":"","Value":""},
	                     {"Name":"中文(chinese)","Value":"1"},
	                     {"Name":"English","Value":"2"},
	                     {"Name":"Tiếng Việt(vietnamese)","Value":"3"},
                     ],
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
})});