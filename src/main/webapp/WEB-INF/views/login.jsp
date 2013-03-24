<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form id="login-form" class="form-signin" method="post">
	<h2>Please sign in</h2>
	<input type="text" name="username" class="input-block-level" placeholder="Username">
	<input type="password" name="password" class="input-block-level" placeholder="Password">
	<button type="submit" class="btn btn-primary">Sign in</button>
</form>
<!--[if IE]>
<script>
Placeholders.init();
</script>
<![endif]-->
<script type="text/javascript">
function login() {
	$.ajax({
		url: '${service}/login',
		data: $('#login-form').serialize(),
		type: 'POST'
	}).done(function(resps) {
		window.location = resps.serviceUrl;
	}).fail(function(jqXHR, textStatus, errorThrown) {
		var msg = jqXHR.status + ': ' + jqXHR.statusText;
		notify(msg, NotifyType.ERROR);
	});
}

$(function() {
	$('#login-form').validate({
		rules: {
			username: 'required',
			password: 'required'
        },
        submitHandler: function(form) {
			login();
		}
	});
});
</script>