var NotifyType = {INFO:'alert-info', SUCCESS:'alert-success', WARNING:'', ERROR:'alert-error'};

function notify(message, type, order) {
	type = type || 'alert-info';
	order = order || 0; // used when there are several notifies displaying simultaneously
	var notify = '<div class="alert ' + type + '" style="position:absolute;"><a class="close" data-dismiss="alert">×</a><span>'
		+ message + '</span></div>';
	var $notify = $(notify).appendTo(document.body);
	var x = $(window).width() / 2 - $notify.outerWidth() / 2,  y = (20 * (order + 1)) + ($notify.outerHeight() * order);
	$notify.offset({top:y, left:x});
	setTimeout(function() {$notify.fadeOut(1500, 'swing', function(){$(this).remove();});}, 3000);
}

function info(message, order) {
	notify(message, NotifyType.INFO, order);
}

function success(message, order) {
	notify(message, NotifyType.SUCCESS, order);
}

function warning(message, order) {
	notify(message, NotifyType.WARNING, order);
}

function error(message, order) {
	notify(message, NotifyType.ERROR, order);
}

function ajaxRequestFail(jqXHR, textStatus, errorThrown) {
	var msg = jqXHR.status + ': ' + jqXHR.statusText;
	notify(msg, NotifyType.ERROR);
}

function log(msg) {
	if (window.console && console.log) {
		console.log(msg);
	}
}
