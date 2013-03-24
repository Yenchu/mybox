<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
</head>
<body>
<iframe id="content-frame" src="${service}/website/${space}/html/index.html" width="100%" height="100%" frameborder="0"></iframe>
<script type="text/javascript">
function replaceRoot(href) {
	var c1 = href.charAt(0);
	var c2 = href.charAt(1);
	if (c1 == '/' && c2 != '/') {
		return '${service}/website/${space}/html' + href;
	} else {
		return false;
	}
}
$(function() {
	var height = window.innerHeight;
	$('#content-frame').css('height', height).load(function() {
		$(this).contents().find('[href]').each(function(index) {
			var href = $(this).attr('href');
			var newHref = replaceRoot(href);
			newHref && $(this).attr('href', newHref);
		});
		$(this).contents().find('[src]').each(function(index) {
			var src = $(this).attr('src');
			var newSrc = replaceRoot(src);
			newSrc && $(this).attr('src', newSrc);
		});
	});
});
</script>
</body>
</html>