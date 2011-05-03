<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES: Browse Marked Documents</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  <script type="text/javascript">
  	dojo.require("dijit.TitlePane");
  	dojo.require("dijit.layout.TabContainer");
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dijit.layout.StackContainer");
  </script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2>Relevance Measures</h2>
<div>
<!-- 
<div style="float:right">
	<a href="<s:url includeParams="get"><s:param name="pagenum" value="%{pagenum-1}"/></s:url>">Prev</a> |
	page <s:property value="pagenum"/> of <s:property value="npages"/> | 
	<a href="<s:url includeParams="get"><s:param name="pagenum" value="%{pagenum+1}"/></s:url>">Next</a>
</div>
<s:actionerror/>
<s:actionmessage/>

<p><s:property value="nrelevancies"/> Relevancies found</p>
<table class="data colorA">
<thead><tr>
<th>Query ID: Terms (Params)</th>
<th>Timestamp</th>
<th>Document (Rank)</th>
</tr></thead>
<tbody>
<s:iterator value="relevancies">
<tr id="rel<s:property value="rid"/>">
	<td><s:property value="queryId"/>: <s:property value="query"/> (<s:property value="queryParams"/>)</td>
	<td><s:date name="timestamp"/></td>
	<td><s:property value="document"/> (<s:property value="rank"/>)</td>
</tr>
</s:iterator>
</tbody>
</table>
-->


<s:set name="rf" value="true"/>
<s:iterator value="tabs">
<div id="<s:property value="title"/>" dojoType="dijit.TitlePane" title="<s:property value="title"/>" open="false">
	<s:iterator value="results" status="parity">
	<div id="<s:property value="title"/> <s:property value="id"/>" dojoType="dijit.TitlePane" title="<s:property value="label"/>" open="false">
<tiles:insertTemplate template="/resultsSingleResult.jsp" flush="true"/>
	</div>
	</s:iterator>
</div>
</s:iterator>

</div>

<s:debug/>
</body>
</html>