<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES Admin Settings</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
</head>
<body class="nihilo">

<s:action name="Header" executeResult="true"/>
<h2>Machine Learning Experimental Settings</h2>
Current graph:<ul>
<li>dbDir = <s:property value="dbDir"/>
<li>name = <s:property value="graphName"/>
<li>store = <s:property value="dbStore"/>
</ul>

<p><a href="<s:url action="PropertyManager"/>">Edit nies and ghirl .properties</a></p>

<h3>Search Options</h3>
<s:actionmessage/>
<s:form action="SaveAdminSettings">
<s:textfield key="defaultQuery"/>
<s:checkbox key="usingDefaultQuery"/>
<s:textfield key="stopEdgeStringList"/>
<s:checkbox key="usingStopEdges"/>
<s:textfield key="tabTimeout"/>
<s:submit name="Save"/>
</s:form>

<h3>Pregenerated Result Sets</h3>
Note: Result set paths are currently relative to the classloader, in $tomcat/webapps/$this-webapp/WEB-INF/classes.
<s:form action="Display">
<s:textfield key="file"/>
<s:submit name="Display Pregenerated Results"/>
</s:form>

<h3>Alternate Search Interfaces</h3>
<ul>
<li><a href="<s:url action="Search-lazy"/>">Lazy Search</a> - Only current tab is rendered.  All tabs are filtered and counted.
<li><a href="<s:url action="Search-laziest"/>">Laziest Search</a> - Only current tab is rendered. No other tabs are filtered or counted.
</ul>
</body>
</html>