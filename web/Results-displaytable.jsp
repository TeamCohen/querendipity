<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html>
<head>
  <title>NIES</title>
  <link rel="stylesheet" href="lib/dojo-release-1.3.1/dijit/themes/nihilo/nihilo.css">
  <script language="javascript" src="lib/dojo-release-1.3.1/dojo/dojo.js.uncompressed.js" djConfig="parseOnLoad: true, isDebug: true"></script>
  <script language="javascript">
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
</script>
<style>
.niescol {vertical-align:top}
.odd     {background: #ddeedd}
th       {background: #99aa99}
body     {font-family:Verdana; font-size:10pt}
table    {font-size:10pt}
</style>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<p><s:property value="nresults"/> results for <s:property value="query"/> (<s:property value="runtime"/> secs).</p>


<div id="tabContainer" dojoType="dijit.layout.TabContainer" style="width:90%; height: 90%">
<s:iterator value="tabs">
<div dojoType="dijit.layout.ContentPane" title="<s:property value="title"/>" selected="<s:property value="isDefault"/>">
<s:set var="results" value="results" scope="request"/>


<display:table name="results" requestURI="" pagesize="10" style="width:90%">
	<s:param name="id" value="{top.title}Result"/>
    <display:column property="rank"           title=""         class="niescol"/>
    <display:column property="result"         title="Result"   class="niescol" href="Go.action" paramId="url" paramProperty="href"/>
<s:if test="title == 'Paper'">
    <display:column property="citation"       title="Citation" class="niescol"/>
    <display:column property="authorsString"  title="Authors"  class="niescol" style="width:20em"/>
    <display:column property="genesString"    title="Genes"    class="niescol" style="width:15em"/>
</s:if>
    <display:column property="score"          title="Score"    class="niescol"/>

    <display:setProperty name="paging.banner.item_name"  value="Result"/>
    <display:setProperty name="paging.banner.items_name" value="Results"/>
    <display:setProperty name="paging.banner.some_items_found">
     <span class="pagebanner">{1} {2}-{3} of {0} for <s:property value="query"/> (<s:property value="runtime"/> secs)</span>
    </display:setProperty>
    <display:setProperty name="paging.banner.all_items_found">
     <span class="pagebanner">{1} 1-{0} of {0} for <s:property value="query"/> (<s:property value="runtime"/> secs)</span>
    </display:setProperty>
    <display:setProperty name="paging.banner.full">
     <span class="pagelinks"><b><a href="{2}">Previous</a></b> {0} <b><a href="{3}">Next</a></b></span>
    </display:setProperty>
    <display:setProperty name="paging.banner.first">
     <span class="pagelinks"><span style="color:white">Previous</span> {0} <b><a href="{3}">Next</a></b></span>
    </display:setProperty>
    <display:setProperty name="paging.banner.last">
     <span class="pagelinks"><b><a href="{2}">Previous</a></b> {0} <span style="color:white">Next</span></span>
    </display:setProperty>
    <display:setProperty name="paging.banner.onepage" value=""/>
    <display:setProperty name="paging.banner.page.selected"><span class="pagelinks">{0}</span></display:setProperty>
    <display:setProperty name="paging.banner.page.separator" value=" - "/>
</display:table>

</div>
</s:iterator>
</div>

</body>
</html>