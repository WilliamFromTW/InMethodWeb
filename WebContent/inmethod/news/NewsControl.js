var myDivClassName = "DivNewsControl";
var myDivStatusIDName = "changeNewsStatus";
var sFunctionName = "NewsControlServlet";
var sFunctionUrl = "inmethod/news/" + sFunctionName;

if (typeof FunctionDivTableIDName !== "undefined" && FunctionDivTableIDName != null && FunctionDivTableIDName != "") {
    myDivClassName = FunctionDivTableIDName;
}
function showNewsMessage(message, color) {
    if (typeof FunctionDivStatusIDName !== "undefined" && FunctionDivStatusIDName != null && FunctionDivStatusIDName != "") {

        showMessage(message, color);
    } else {
        if (color == "green") {
            $('#' + myDivStatusIDName).removeClass().addClass("alert alert-success");
            $('#' + myDivStatusIDName).html(message).slideDown(1000).fadeOut(3000);
        } else if (color == 'red') {
            $('#' + myDivStatusIDName).removeClass().addClass("alert alert-danger");
            $('#' + myDivStatusIDName).html(message).slideDown(1000).fadeOut(3000);

        }

    }
};
function br2nl (str, replaceMode) {   
	
  var replaceStr = (replaceMode) ? "\n" : '';
  // Includes <br>, <BR>, <br />, </br>
  return str.replace(/<\s*\/?br\s*[\/]?>/gi, replaceStr);
}

$(function () {

    var lastPrevItem;

    var MyDateField = function (config) {
        jsGrid.Field.call(this, config);
    };

    MyDateField.prototype = new jsGrid.Field({
        css: "jsgrid",
        sorter: function (date1, date2) {
            return parseInt(date1) - parseInt(date2);
        },

        itemTemplate: function (value) {
            //return new Date(value).toDateString();
            if (value == null || value == "" || value.trim() == "")
                return "";
            return value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6, 8); ;
        },

        insertTemplate: function (value) {
            return this._insertPicker = $("<input>").datepicker({
                dateFormat: 'yy/mm/dd',
                defaultDate: new Date()
            });
        },

        editTemplate: function (value) {
            if (value != null && value != "") {
                var d = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6, 8);
                return this._editPicker = $("<input>").datepicker({
                    dateFormat: 'yy/mm/dd'
                }).datepicker("setDate", new Date(d));
            } else
                return this._editPicker = $("<input>").datepicker({
                    dateFormat: 'yy/mm/dd'
                }).datepicker("setDate", "");
        },

        insertValue: function () {
            var d = this._insertPicker.datepicker("getDate");
            if (d == null)
                return "";
            var m1 = (d.getMonth() + 1);
            var m2;
            if (m1 < 10)
                m2 = "0" + m1;
            else
                m2 = "" + m1;

            var d1 = d.getDate();
            var d2;
            if (d1 < 10)
                d2 = "0" + d1;
            else
                d2 = "" + d1;

            return d.getFullYear() + m2 + d2;

            //    return this._insertPicker.datepicker("getDate").toISOString();//.slice(0,10).replace(/-/g,"");
        },

        editValue: function () {

            var d = this._editPicker.datepicker("getDate");
            if (d == null)
                return "";
            var m1 = (d.getMonth() + 1);
            var m2;
            if (m1 < 10)
                m2 = "0" + m1;
            else
                m2 = "" + m1;

            var d1 = d.getDate();
            var d2;
            if (d1 < 10)
                d2 = "0" + d1;
            else
                d2 = "" + d1;

            return d.getFullYear() + m2 + d2;
            //this._editPicker.datepicker("getDate");//.toISOString().slice(0,10).replace(/-/g,"");
        }
    });
    jsGrid.fields.myDateField = MyDateField;

	// 修改標題
	$(".function-title").html("News");
	let urlParams = new URLSearchParams(window.location.search);
    let sCatalogValue = urlParams.get("Catalog");
    if( !(sCatalogValue!==null && sCatalogValue!== "undefined"))
        sCatalogValue = "";
     else
        sFunctionName = sFunctionName +"?Catalog="+    sCatalogValue;
       $.when(
            $.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID": "getFileTypeSelection",  "Catalog": sCatalogValue}),
            $.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID": "getNewsCatalogSelection",  "Catalog": sCatalogValue}),
            $.getJSON("inmethod/auth/AuthenticationControlServlet", {"FlowID": "getAuthorizedFunctionInfo", "FunctionName": sFunctionName })
       ).done(function (fileTypeData,catalogData,jsonAuthorized) {
    console.log(catalogData[0]);
                 $.getJSON(sFunctionUrl, {"FlowID": "doQuery",  "Catalog": sCatalogValue }, function (d) {
	                 $('.' + myDivClassName).jsGrid({
                        width: "100%",
                        height: "90%",
                        inserting: JSON.parse(jsonAuthorized[0].Insert),
                        filtering: true,
                        editing: JSON.parse(jsonAuthorized[0].Update),
                        sorting: true,
                        autoload: true,
                        paging: true,
                        pageSize: 15,
                        pageButtonCount: 5,
                        pagerFormat: "Currnet: {pageIndex} &nbsp;&nbsp; {first} {prev} {pages} {next} {last} &nbsp;&nbsp; ALL: {pageCount} Page",
                        pagePrevText: "<",
                        pageNextText: ">",
                        pageFirstText: "<<",
                        pageLastText: ">>",
                        pageNavigatorNextText: "&#8230;",
                        pageNavigatorPrevText: "&#8230;",
                        noDataContent: "No Data",
                        updateOnResize: true,
                        data: d,
                        rowClick: function(args){
		                },
                deleteConfirm: function (item) {
                    return "Are you sure?";
                },
                onItemUpdating: function (grid,args) {
	                lastPrevItem = grid.previousItem;
                },
                onItemUpdated: function(item) {
	        window.location.reload();
                },
                onItemInserted: function (grid) {
	              window.location.reload();
                 },
                controller: {
                    loadData: function (filter) {
                        return $.grep(d, function (client) {	
                            return (!filter.Oid || client.Oid.indexOf(filter.Oid) > -1)
                             && (!filter.Catalog || client.Catalog.indexOf(filter.Catalog) > -1)
                             && (!filter.Subject || client.Subject.indexOf(filter.Subject) > -1)
                             && (!filter.Message || client.Message.indexOf(filter.Message) > -1)
                             && (!filter.UploadType || client.UploadType.indexOf(filter.UploadType) > -1)
                             && (!filter.UploadName || client.UploadName.indexOf(filter.UploadName) > -1)
                             && (!filter.UploadPath || client.UploadPath.indexOf(filter.UploadPath) > -1)
                             && (!filter.UpdateDt || client.UpdateDt.indexOf(filter.UpdateDt) > -1);
                        });
                    },

                    deleteItem: function (ItemData) {
                        var result = $.Deferred();

                        $.getJSON(sFunctionUrl, {
                            "FlowID": "doDelete",
                            "Oid": ItemData.Oid
                        }, function (d) {
                            if (typeof d.DeleteResult !== "undefined") {
                                if (d.DeleteResult == "OK") {
                                    result.resolve(ItemData);
                                    showNewsMessage("Delete Success", "green");
                                } else {
                                    result.reject(new Error("delete failed"));
                                    showNewsMessage("Delete Fail", "red");
                                }
                            } else {
                                result.reject(new Error("delete failed"));
                                showNewsMessage("Delete Fail", "red");
                            }
                        })
                        .fail(function (s) {
                            result.reject(new Error(s.responseText));
                            showNewsMessage("Delete Fail", "red");
                        });
                        return result.promise();
                    },
                    insertItem: function (ItemData) {
                        var formData = new FormData();
                        formData.append("FlowID", "doAdd");
                        formData.append("Oid", ItemData.Oid);
                        formData.append("Catalog", ItemData.Catalog);
                        formData.append("Subject", ItemData.Subject);
                        formData.append("Message", ItemData.Message);
                        formData.append("UploadType", ItemData.UploadType);
                        formData.append("UploadName", ItemData.UploadName);
                        formData.append("UploadPath", ItemData.UploadPath);
                        formData.append("UpdateDt", ItemData.UpdateDt);
                        var result = $.Deferred();
                        
                        if(   ItemData.UploadPath!== null  &&  ItemData.UploadPath !== undefined){
	                      
	                      if(  ! ( ItemData.UploadType!==null &&   ItemData.UploadType!==undefined &&  ItemData.UploadType!=='')
	                           || 
	                             ! ( ItemData.UploadName!==null &&   ItemData.UploadName!==undefined && ItemData.UploadName!=='' )
	                         ){
		                         result.reject(new Error("add failed"));
                                showNewsMessage("Insert Fail , file name or file type can not empty", "red");
                               return   result.promise();
	                      }
                        } else{
	                      if(  ( ItemData.UploadType!==null &&   ItemData.UploadType!==undefined &&  ItemData.UploadType!=='')
	                           || 
	                             ( ItemData.UploadName!==null &&   ItemData.UploadName!==undefined && ItemData.UploadName!=='' )
	                         ){
		                         result.reject(new Error("add failed"));
                                showNewsMessage("Insert Fail , file name or file type must be empty", "red");
                               return   result.promise();
	                      }
                        }
                        $.ajax({
                            method: "post",
                            type: "POST",
                            url: sFunctionUrl,
                            data: formData,
                            contentType: false,
                            processData: false,
                            success: function (data) {

                                result.resolve(formData);
                                showNewsMessage("Insert Success! Refresh whote page if modify data!", "green");
                       //         console.log(data)
                                return result.promise();
                            },
                            error: function (err) {
                                result.reject(new Error("add failed"));
                                showNewsMessage("Insert Fail", "red");
                                console.log(err);
                            }
                        });

                    },
   
                    updateItem: function (ItemData) {
                        var result = $.Deferred();

                        $.getJSON(sFunctionUrl, {
                            "FlowID": "doUpdate",
                            "Oid": ItemData.Oid,
                            "Catalog": ItemData.Catalog,
                            "Subject": ItemData.Subject,
                            "Message": ItemData.Message,
                            "UploadType": ItemData.UploadType,
                            "UploadName": ItemData.UploadName,
                            //"UploadPath": ItemData.UploadPath,
                            "UpdateDt": ItemData.UpdateDt
                        }, function (d) {

                            if (typeof d.UpdateResult !== "undefined") {
                                if (d.UpdateResult == "OK") {
                                    result.resolve(ItemData);
                                    showNewsMessage("Update Success", "green");
                                } else {
                                    result.resolve(lastPrevItem);
                                    showNewsMessage("Update Fail", "red");
                                }
                            } else {
                                result.resolve(lastPrevItem);
                                showNewsMessage("Update Fail", "red");
                            }
                        })
                        .fail(function (s) {
                            result.resolve(lastPrevItem);
                            showNewsMessage("Update Fail", "red");
                        });
                        return result.promise();
                    }
                },

                fields: [{
                        name: "Oid",
                        title: "OID",
                        css: "jsgrid",
                        type: "text",
                        width: 1,
                        editing: false,
                        inserting: false,
                        visible: false
                    }, {
                        name: "Catalog",
                        title: "Catalog",
                        css: "jsgrid",
                        type: "select",
                        width:8,
                        validate: "required",
                        items: catalogData[0] ,
                        textField: "Name",
                        valueField: "Value"
                    }, {
                        name: "Subject",
                        title: "Subject",
                        css: "jsgrid",
                        type: "text",
                        width: 30,
                        validate: "required"
                    }, {
                        name: "Message",
                        title: "Content",
                        css: "jsgrid",
                        type: "textarea",
                        width: 60,
                        validate: "required"             ,
                        editTemplate: function(value, item) { 
	
	                        return this._asdfa="<textarea id='textareaID'>"+ br2nl(value,true).trim()+  "</textarea>";
                        },
                        editValue: function(){
	                      return $("#textareaID").val() ;
                        },
                    }, {
                        name: "UpdateDt",
                        title: "Date",
                        css: "jsgrid",
                        type: "myDateField",
                        width: 6,
                        validate: "required"
                    }, 
                    {
                        name: "UploadType",
                        title: "File Type",
                        css: "jsgrid",
                        type: "select",
                        align: "left",
                        width: 10,
                        items:fileTypeData[0],
                        textField: "Name",
                        valueField: "Value"
                    }, {
                        name: "UploadName",
                        title: "File Name",
                        css: "jsgrid",
                        type: "text",
                        width: 20
                    }, {
                        name: "UploadPath",
                        title: "File",
                        css: "jsgrid",
                        type: "text",
                        width: 6,
                        editing: false,
                        filtering: false,
                        itemTemplate: function(value, item) {
                          var $text = $("<p>").text(item.MyField);
                      //    console.log(item);
                          try{
                            var $link = $("<a>").attr("href", 'downloadDocument?FileID='+item.UploadPath.split('\\').pop().split('/').pop() ).text("download");
                            return $("<div>").append($text).append($link);
                          }catch(e){
	                       
                          }
                        },
                        insertTemplate: function () {
                            var insertControl = this.insertControl = $("<input>").prop("type", "file");
                            return insertControl;
                        },
                        insertValue: function () {
                            return this.insertControl[0].files[0];
                        },
                    }, {
                        type: "control",
                        width: 6,
                        editButton: JSON.parse(jsonAuthorized[0].Update),
                        deleteButton: JSON.parse(jsonAuthorized[0].Delete)
                    }
                ]
            });

        }).fail(function (d) {
            if (d.responseJSON.NoLogin == "FAIL") {
                showMessage("No Login!", "red");

            } else if (d.responseJSON.NoPermission == "FAIL") {
                showMessage("No Permission!", "red");
            };
        });

    })
})

