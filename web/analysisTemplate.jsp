<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES: <s:property value="query.queryString"/></title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>

<!--  <script language="javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dojo/dojo.xd.js" djConfig="parseOnLoad: true"></script> -->
  <script language="javascript"><!--
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.StackContainer");
//dojo.require("dojo.fx");
//dojo.require("dojo._base.fx");
//dojo.require("dojo._base.query");
  --></script>
<tiles:insertTemplate template="/resultsGlobalTabSettings.jsp" flush="true"/>
</head>
<body class="nihilo">
<!-- Header -->
<s:action name="Header" executeResult="true"/>

<!-- Search Bar -->
<div>
<img src="images/nies-logo.gif" style="width:15em; float:left"/>
<div style="float:left; margin-left:1.5em;  padding: 0.5em; margin-bottom:0">
<tiles:insertTemplate template="/SearchFormOnly.jsp" flush="true"/>
</div>
</div>

<!-- main content -->
<div style="width:70.5em; clear:both">

<!-- query node -->
<div id="querynode" style="margin-left:3em">
	<s:push value="analyzedResultTab"><!-- push analyzed result tab -->
		<s:push value="analyzedResult"><!-- push analyzed result -->
		<h4 style="margin:0;padding:0;margin-bottom:0.2em">Queried Gene:</h4>
		<tiles:insertTemplate template="/resultsSingleResult.jsp" flush="true"/>
		</s:push><!-- /result -->
	</s:push><!-- /tab -->
</div>

<!-- Results summary -->
<p style="margin-bottom:.5em">
<b><s:property value="nresults"/></b> results for 
<b><s:property value="query.queryString"/></b> (<b><s:property value="runtime"/></b> secs).</p>

<!--Tab deck -->
<div id="tabContainer" dojoType="dijit.layout.TabContainer" style="width:100%" doLayout="false">
<s:iterator value="tabs" status="tabstatus">
	<s:set name="selected" value="%{tab == title}"/><!-- boolean selected = <s:property value="%{selected.toString()}"/> -->
	<tiles:insertAttribute name="tabPane"/>
</s:iterator>
</div>

<!-- /main content -->
</div>
 
<!-- Footer -->
<s:include value="footer.jsp"/>
</body>
</html>