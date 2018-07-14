var error = "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span> ";

function showOverlay() {
	$("body").append("<div id='overlay'></div>");
	
	$("#overlay").css({
		"position": "fixed",
		"top": "0",
		"left": "0",
		"background-color": "#000000",
		"width": "100%",
		"height": "100%",
		"opacity": "0.8",
		"z-index": "10000"
	});
}

function parseError(res) {	
	var pos = res.indexOf("<b>message</b>");
	if (pos > 0) {
		res = res.substring(pos+14);
	}
	var pos = res.indexOf("</u");
	if (pos > 0) {
		res = res.substring(0,pos);
	}
	res = res.replace("<u>","");
	return res;
}

function post(url,returnValues,redirectUrls) {
	$("#error").html("");
	showOverlay();
	$.post(url,$("#form").serialize())
		.done(function(data) {
			if (data == "invalid_session") {
				document.location.href = "login.jsp";
				return;
			}
			
			for (var i = 0; i < returnValues.length; i++) {
				if (data == returnValues[i]) {
					if (redirectUrls[i].startsWith("$")) {
						alert(redirectUrls[i].substring(1));
						$("#overlay").remove();
						return;
					} else {
						document.location.href = redirectUrls[i];
						return;
					}					
				}
			}
			if ($("#error").length) {
				$("#error").html(error + data);
			} else {
				alert("Error: " + data)
			}
			$("#overlay").remove();
		})
		.fail(function(xhr) {
			var res = parseError(xhr.responseText)
			$("#error").html(error + res);
			$("#overlay").remove();
		});
}

function postMultipart(url,returnValues,redirectUrls) {
	$("#error").html("");
	var data = new FormData($('#form')[0]);	
	$.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: url,
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
        	for (var i = 0; i < returnValues.length; i++) {
				if (data == returnValues[i]) {
					document.location.href = redirectUrls[i];
					return;
				}
			}
			if (data == "invalid_session") {
				document.location.href = "login.jsp";
				return;
			}
			$("#error").html(error + data);
			$("#overlay").remove();
        },
        error: function (e) {        	
			var res = parseError(e.responseText)
			$("#error").html(error + res);
			$("#overlay").remove();			
        }
    });
}

function formatAmount(origAmount) {
	origAmount = origAmount.replace(/,/g, "")
	amount = ""+Math.round(origAmount);
	var mod = Math.round(origAmount*100)%100;
	var formated = "." + mod;
	if (mod<10) {
		formated+="0";
	}
	if (amount.length <= 3) {
		return amount;
	}
	
	while (amount.length > 3) {
		formated = ","+amount.substring(amount.length-3)+formated;
		amount = amount.substring(0,amount.length-3)
	}
	return amount+formated;	
}

$("#amount").focusout(function() {
	$("#amount").val(formatAmount($("#amount").val()));
});

$("#bamount").focusout(function() {
	$("#bamount").val(formatAmount($("#bamount").val()));
});
