<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${not empty entries}">
	<input type="hidden" name="space" value="${space}" />
	<input type="hidden" name="file" value="${file}" />
	<table class="table table-bordered table-striped">
		<c:forEach var="entry" items="${entries}" varStatus="status">
		<tr><td><input type="radio" name="rev" value="${entry.rev}" ${status.count==2?'checked':''}></td>
		<td>Version ${fn:length(entries)-status.index}${status.first?' (current)':''}${status.last?' (oldest)':''}</td>
		<td>${entry.modified}</td>
		<td>${entry.size}</td></tr>
		</c:forEach>
	</table>
</c:if>