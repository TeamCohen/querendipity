<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
</head>

<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<ul>
<li>Url: <s:property value="url"/></li>
<li>Document: <s:property value="document"/></li>
<li>Type: <s:property value="type"/></li>
<li>Query: <s:property value="query"/></li>
</ul>
</body>
</html>