<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
</head>
<body class="nihilo">
<!-- Header -->
<s:action name="Header" executeResult="true"/>
<!-- /Header -->

<h2>View User:</h2>
<s:actionerror/>
<s:actionmessage/>
<s:form action="User_edit">
<s:hidden name="uid"/>
<s:hidden name="username"/>
<s:label key="username" value="%{username}"/>
<s:label key="email" value="%{email}"/>
<s:submit value="Edit"/>
</s:form>

</body>
</html>