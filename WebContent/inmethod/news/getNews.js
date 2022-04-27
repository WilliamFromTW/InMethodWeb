
function nl2br (str, is_xhtml) {
    if (typeof str === 'undefined' || str === null) {
        return '';
    }
    var breakTag = (is_xhtml || typeof is_xhtml === 'undefined') ? '<br />' : '<br>';
    return (str + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1' + breakTag + '$2');
}

$(function() {

	    PassToBackEnd();


	function PassToBackEnd() {
		$("#Msg").html('<br><font color=red>FetchData</font><br>')
		$.when(
		$.getJSON(sFunctionBackEndUrl,{"FlowID":"getNews"}),		
		$.getJSON(sFunctionBackEndUrl,{"FlowID":"getNews2"})
		).done( function(d1,d2) {
		    var $table = $('#bootstrap-table');
		    var $tableheadtr = $('#bootstrap-table-head-tr');
		    var $tabletr = $('#bootstrap-table-tr');
			if (d1[0].STATUS == 'TRUE'){
			  $tableheadtr.html(d1[0].FIELD_NAME);
			  $table.bootstrapTable({data: d1[0].JSON,     
			  height: 300, 
			  onClickRow:function(row, $element, field){
				//alert(row.upload_path);
				if(  row.upload_path!==null && row.upload_path!== "undefined" && row.upload_path!==''){
                   $("#table-subject").html("<thead  class=\" table-secondary\"><tr><th><font color='red'>"+ row.subject  +"</font></th></tr></thead><tbody><tr><th><font color='blue'>"+ nl2br( row.message,true)+"<br/><br/>"+"<a href='downloadDocument?FileID="+row.upload_path.split('\\').pop().split('/').pop() +"''>download</a></th></tr></tbody>");
                 }
				else
               $("#table-subject").html("<thead  class=\" table-secondary\"><th><font color='red'>"+ row.subject  +"</font></th></tr></thead><tbody><tr><th><font color='blue'>"+ nl2br( row.message,true)+"</font></th></tr></tbody>");
             }
             }  
             )	
			  $table.bootstrapTable('load', d1[0].JSON);
			}
			var $table2 = $('#bootstrap-table2');
		    var $tableheadtr2 = $('#bootstrap-table-head-tr2');
		    var $tabletr2 = $('#bootstrap-table-tr2');
			if (d2[0].STATUS == 'TRUE'){
			  $tableheadtr2.html(d2[0].FIELD_NAME);
			  $table2.bootstrapTable({data: d2[0].JSON,    
			  height:300, 
			  onClickRow:function(row, $element, field){
				//alert(row.upload_path);
				if(  row.upload_path!==null && row.upload_path!== "undefined" && row.upload_path!==''){
                   $("#table-subject").html("<thead  class=\" table-secondary\"><tr><th><font color='red'>"+ row.subject  +"</font></th></tr></thead><tbody><tr><th><font color='blue'>"+ nl2br( row.message,true)+"<br/><br/>"+"<a href='downloadDocument?FileID="+row.upload_path.split('\\').pop().split('/').pop() +"''>download</a></th></tr></tbody>");
                 }
				else
               $("#table-subject").html("<thead  class=\" table-secondary\"><th><font color='red'>"+ row.subject  +"</font></th></tr></thead><tbody><tr><th><font color='blue'>"+ nl2br( row.message,true)+"</font></th></tr></tbody>");
             }
             }  
             )	
			  $table2.bootstrapTable('load', d2[0].JSON);
			}
			  
		});
	}
	;
});