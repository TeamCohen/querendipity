<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES Admin: Show Relevancies</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2>Relevance Measures</h2>
<div>

<div style="float:right">
	<a href="<s:url includeParams="get" action="RelevancyLog"><s:param name="pagenum" value="%{pagenum-1}"/></s:url>">Prev</a> |
	page <s:property value="pagenum"/> of <s:property value="npages"/> | 
	<a href="<s:url includeParams="get" action="RelevancyLog"><s:param name="pagenum" value="%{pagenum+1}"/></s:url>">Next</a>
</div>
<s:actionerror/>
<s:actionmessage/>

<p><s:property value="nrelevancies"/> Relevancies found</p>
<table class="data colorA">
<thead><tr>
<th>Relevance id</th>
<th>Timestamp</th>
<th>Type</th>
<th>Document (Rank)</th>
<th>Query ID: Terms (Params)</th>
<th>User ID</th></tr></thead>
<tbody>
<s:iterator value="relevancies">
<tr>
	<td><s:property value="rid"/></td>
	<td><s:date name="timestamp"/></td>
	<td><s:property value="type"/></td>
	<td><s:property value="document"/> (<s:property value="rank"/>)</td>
	<td><s:property value="queryId"/>: <s:property value="query"/> (<s:property value="queryParams"/>)</td>
	<td><s:property value="user"/></td>
</tr>
</s:iterator>
</tbody>
</table>
</div>
</body>
</html>