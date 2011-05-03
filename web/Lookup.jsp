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
<s:action name="Header" executeResult="true"/>

<h2>Task: <s:property value="task"/></h2>
<s:form>
<s:textfield key="username"/>
<s:submit action="User_edit" value="Edit"/>
<s:submit action="User_retrieve" value="Find"/>
</s:form>

</body>
</html>