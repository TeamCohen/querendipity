<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html>
<head>
  <title>NIES Error</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
</head>
<body class="nihilo">

<s:include value="Header.jsp"/>

<h2>Error</h2>
<p><s:text name="error.webserver.apology"/> <s:text name="error.webserver.reallybad"/> <s:text name="error.webserver.contactmsg"/>  </p>

<h3>Error Messages</h3>

<s:actionerror />
<p><s:property value="exception.message"/></p>

</body>
</html>