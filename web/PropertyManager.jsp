<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES Admin Settings</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
  <style>
  .propertymanager td,th {font-family:Verdana,sans-serif;font-size:10pt;padding-right:1em}
  </style>
</head>
<body class="nihilo">

<s:action name="Header" executeResult="true"/>

<s:form theme="simple" action="SaveProperties" method="post">
<h1>NIES Properties</h1>
<table class="propertymanager">
<tr>
	<th align="right">Current Value</th>
	<th>New Value</th>
	<th align="left">Property</th></tr>
<!-- iterate over property entries --><s:sort comparator="keysort" source="niesProperties.entrySet()"><s:iterator><tr>
	<td style="color:gray;text-align:right"><s:property value="value"/></td>
	<td><input size="40" type="text" name="niesProperties['<s:property value="key"/>']" value="<s:property value="value"/>"/></td>
	<td><label for="<s:property value="key"/>"><s:property value="key"/></label></td>
</tr></s:iterator></s:sort><!-- /iterate over property entries -->
</table>

<label for="niesAppend">nies.properties Additions:</label><br/>
<textarea name="niesAppend" rows="3" cols="40"></textarea>

<h1>GHIRL Properties</h1>
<table class="propertymanager">
<tr>
	<th align="right">Current Value</th>
	<th>New Value</th>
	<th align="left">Property</th></tr>
<!-- iterate over property entries --><s:sort comparator="keysort" source="ghirlProperties.entrySet()"><s:iterator><s:if test="key.startsWith('ghirl')">
<tr>
	<td style="color:gray;text-align:right"><s:property value="value"/></td>
	<td><input size="40" type="text" name="ghirlProperties['<s:property value="key"/>']" value="<s:property value="value"/>"/></td>
	<td><label for="<s:property value="key"/>"><s:property value="key"/></label></td>
</tr></s:if></s:iterator></s:sort><!-- /iterate over property entries -->
</table>

<label for="ghirlAppend">ghirl.properties Additions:</label><br/>
<textarea name="ghirlAppend" rows="3" cols="40"></textarea>

<s:submit/>
</s:form>

<s:include value="footer.jsp"/>
</body>
</html>