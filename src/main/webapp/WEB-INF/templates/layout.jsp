<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>My Box</title>
<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.0/css/bootstrap-combined.min.css" rel="stylesheet">
<link href='//ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/themes/cupertino/jquery-ui.css' rel="stylesheet">
<link href="${asset}/font-awesome/css/font-awesome.min.css" rel="stylesheet">
<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
<link rel="shortcut icon" href="${asset}/img/favicon.png">
<link href="${asset}/css/main.css" rel="stylesheet">
<tilesx:useAttribute id="csses" name="csses" classname="java.util.List" ignore="true" />
<c:forEach var="css" items="${csses}">
<tiles:insertAttribute value="${css}" flush="true" />
</c:forEach>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script src="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.0/js/bootstrap.min.js"></script>
<!--[if IE]><script src="${asset}/js/Placeholders.min.js"></script><![endif]-->
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/jquery-ui.min.js"></script>
</head>
<body>
<div id="wrap">
	<tiles:insertAttribute name="header" />
	<br/>
	<tiles:insertAttribute name="body" />
	<br/>
	<div id="push"></div>
</div>
<div id="footer">
	<tiles:insertAttribute name="footer" />
</div>
<script src="${asset}/js/main.js"></script>
<tilesx:useAttribute id="jses" name="jses" classname="java.util.List" ignore="true" />
<c:forEach var="js" items="${jses}">
<tiles:insertAttribute value="${js}" flush="true" />
</c:forEach>
</body>
</html>