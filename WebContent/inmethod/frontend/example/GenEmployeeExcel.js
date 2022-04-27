
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
		$.getJSON(sFunctionBackEndUrl,getFormDataAsJson("myForm"), function(data) {
			if (data.STATUS == 'TRUE'){
			  $("#Msg").html("<br><font color=red>執行完畢-無異常狀況</font><br>"+data.MSG);
              var bases = document.getElementsByTagName('base');
			  var baseHref = null;			  
			  if (bases.length > 0) {
			    baseHref = bases[0].href;
			  }
			  window.open(baseHref + "downloadDocument?FileID="+data.URL,"_blank");			  
			}  
			else if( data.NoPermission=='FAIL'){
			    $("#Msg").html("<br><font color=red>無權限 <a href='#'>回到首頁</a></font>");				
			}else
			  $("#Msg").html("<br><font color=red>執行完成-有異常狀況</font><br>"+data.MSG);
		});
	}
	;
});