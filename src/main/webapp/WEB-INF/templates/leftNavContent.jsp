<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="container-fluid">
	<div class="row-fluid">
		<div class="span2">
			<tiles:insertAttribute name="leftNav" />
		</div>
		<div class="span10">
			<tiles:insertAttribute name="content" />
		</div>
	</div>
</div>