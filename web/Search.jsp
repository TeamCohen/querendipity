<%@ taglib prefix="s" uri="/struts-tags" %><%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html>
<head>
  <title>NIES</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  <s:head/>
</head>
<body class="nihilo">

<s:action name="Header" executeResult="true"/>

<center><img src="images/nies-logo.gif"/></center>
<div style="margin-top:2em; margin-left:6em;">
<tiles:insertTemplate template="SearchFormOnly.jsp" flush="true"/>
</div>


<s:include value="footer.jsp"/>
</body>
</html>