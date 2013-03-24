<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="row-fluid">
<div class="span12">
<form:form commandName="account" id="userForm" class="form-horizontal" action="${appContext}/account/profile" method="post">
	<div class="control-group">
		<label class="control-label" for="name">Name</label>
		<div class="controls">
			<input type="text" id="displayName" name="displayName" value="${account.displayName}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label" for="quota">Quota</label>
		<div class="controls">
			<input type="text" id="quota" name="quota" value="${account.quota}"/>
		</div>
	</div>
	<div class="control-group">
		<div class="controls">
			<label class="checkbox inline">
				<form:checkbox path="role" value="1"/>User
			</label>
			<label class="checkbox inline">
				<form:checkbox path="role" value="2"/>Admin
			</label>
		</div>
	</div>
	<div class="form-actions">
		<button type="submit" class="btn btn-primary">Save</button>
		<button type="button" class="btn">Cancel</button>
	</div>
</form:form>
</div>
</div>
<script>
</script>