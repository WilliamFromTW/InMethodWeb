
//判斷排序123小到大 321大到小
function sortJSON(data, key, way) {
    return data.sort(function(a, b) {
        var x = a[key]; var y = b[key];
        if (way === '123' ) { return ((x < y) ? -1 : ((x > y) ? 1 : 0)); }
        if (way === '321') { return ((x > y) ? -1 : ((x < y) ? 1 : 0)); }
    });
}
//
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