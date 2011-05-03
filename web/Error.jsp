<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES Error</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2>Error</h2>
<p><s:text name="error.webserver.apology"/><s:text name="error.webserver.contactmsg"/>  </p>

<h3>Error Messages</h3>

<s:actionerror />
<p><s:property value="exception.message"/></p>

<hr/>

<h3>Technical Details</h3>
<p><s:property value="exceptionStack"/></p>

<s:debug/>
</body>
</html>