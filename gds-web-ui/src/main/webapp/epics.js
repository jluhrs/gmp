$(document).ready(function(){
    $.stream("epics", {
                open: function() {
                    $("#textfield").focus();
                },
                message: function(event) {
                    $("#textfield").val(event.data);
                },
                error: function() {
                    $("<p />").addClass("message").text("Error").appendTo("#content");
                },
                close: function() {
                    $("#textfield").attr("disabled", "disabled");
                }
            });
 });