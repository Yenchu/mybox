<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${not empty metadata}">
	<table class="table table-bordered table-striped">
		<tr><th>Name</th><th>Path</th></tr>
		<c:forEach var="entry" items="${metadata.contents}" varStatus="status">
		<tr>
		<td>${entry.name}</td>
		<td>${entry.path}</td>
		</tr>
		</c:forEach>
	</table>
</c:if>