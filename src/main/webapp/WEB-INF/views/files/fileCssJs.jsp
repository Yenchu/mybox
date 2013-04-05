<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link href="${asset}/dynatree/skin/ui.dynatree.css" rel="stylesheet">
<link href="${asset}/mytable/mytable.css" rel="stylesheet">
<style>
/* Custom table head */
thead {
  background-color: #f5f5f5;
}
/* Custom upload alert style */
.alert-warning {
  color: #c09853;
  background-color: #fcf8e3;
}
/* File upload input button style */
.fileinput-button {
  position: relative;
  overflow: hidden;
  float: left;
  margin-right: 4px;
}
.fileinput-button input {
  position: absolute;
  top: 0;
  right: 0;
  margin: 0;
  opacity: 0;
  filter: alpha(opacity=0);
  transform: translate(-300px, 0) scale(4);
  font-size: 23px;
  direction: ltr;
  cursor: pointer;
}
/* Fix for IE 6: */
* html .fileinput-button {
  line-height: 24px;
  margin: 1px -3px 0 0;
}
/* Fix for IE 7: */
* + html .fileinput-button {
  padding: 2px 15px;
  margin: 1px 0 0 0;
}
</style>
<script src="//ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>
<script src="${asset}/dynatree/jquery.dynatree.min.js"></script>
<script src="${asset}/js/bootstrap-contextmenu.js"></script>
<script src="${asset}/mytable/mytable.js"></script>