
function getFormDataAsJson(sFormId) {

    var form = $('#' + sFormId);
    if (form != null) {
        var json = {};

        var inputs = $('input', form);

        for (i = 0; i < inputs.length; ++i) {
            var next = inputs[i];
            var key = $(next).attr('id');
            var val = $(next).val();

            if (val != null && key != null)
                json[key] = val;
        }

        var selects = $('select', form);

        for (i = 0; i < selects.length; ++i) {
            var next = selects[i];
            var key = $(next).attr('id');
            var val = $(next).val();

            if (val != null && key != null)
                json[key] = val;
        }
        console.log(json);
        return json;
    }
}

$(function() {

	$("#PassToBackEnd").click(function() {
	  var valid= true;
	  $('[required]').each(function() {
		//console.log( $(this).val() );
        if ($(this).is(':invalid') || !$(this).val()) valid = false;
      })
      if (!valid) {
	    alert("error please fill all fields!");
	    return;
      }
      else 
	    PassToBackEnd();
	});

	function PassToBackEnd() {
		$("#Msg").html('<br><font color=red>執行中</font><br>')
		
		$.getJSON(sFunctionBackEndUrl,getFormDataAsJson("myForm")).done( function(d) {
		    var $table = $('#bootstrap-table');
		    var $tabletr = $('#bootstrap-table-tr');
		    var $tabletrhtml = "";
		    $("#Msg").html(d);
			if (d.STATUS == 'TRUE'){
			  $("#Msg").html("<br><font color=blue>執行完畢-無異常狀況</font><br>");
              var bases = document.getElementsByTagName('base');
			  var baseHref = null;			  
			  if (bases.length > 0) {
			    baseHref = bases[0].href;
			  }
			  $tabletr.html(d.FIELD_NAME);
			  $table.bootstrapTable({
				data: d.JSON,
                onClickRow:function(row, $element, field){
	              alert(row.user_mail);
                }
              })	
			  $table.bootstrapTable('load', d.JSON);
			}  
			else
			  $("#Msg").html("<br><font color=red>執行完成-有異常狀況</font><br>");
		});
	}
	;
});