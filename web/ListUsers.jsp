<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES Admin: Show Users</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2><s:property value="nusers"/> Users:</h2>
<s:actionmessage/>
<table class="settings colorA">
<thead><tr><th></th><th>User id</th><th>Username</th><th>Email</th><th>Password hash</th><th></th></tr></thead>
<tbody>
<s:iterator value="users">
<tr>
	<td> <a href="<s:url action="User_edit"><s:param name="username" value="value.username"/></s:url>">Edit</a> </td>
	<td><s:property value="key"/></td>
	<td><s:property value="value.username"/></td>
	<td><s:property value="value.email"/></td>
	<td><s:property value="value.passHash"/></td>
	<td valign="middle"> <form action="User_delete.action" method="post" id="Delete-<s:property value="value.username"/>" name="Delete-<s:property value="value.username"/>" style="margin-bottom:0pt">
		<input type="hidden" id="username" name="username" value="<s:property value="value.username"/>"/><input type="submit" id="Delete-<s:property value="value.username"/>_submit" value="Delete"/></form>
	</td>
</tr>
</s:iterator>
</tbody>
</table>
</body>
</html>