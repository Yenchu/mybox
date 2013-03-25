<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="container-fluid">
	<div class="row-fluid">
		<div class="span1"></div>
		<div class="span10">
			<tiles:insertAttribute name="content" />
		</div>
		<div class="span1"></div>
	</div>
</div>