<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES: <s:property value="query.queryString"/></title>
  
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  <script language="javascript"><!--
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.StackContainer");
dojo.require("dojo.fx");
dojo.require("dojo._base.fx");
dojo.require("dojo._base.query");
function genesBack(pmid) {
	dijit.byId(pmid+'-genes').back()
}
function doFeedback(url, id) {
	var undo;
	if (url.match(/promote/)) {
		//alert("Promoting "+id);
		promoteResult(id);
		undo = unmarkResult;
	} else if (url.match(/demote/)) {
		//alert("Demoting "+id);
		demoteResult(id);
		undo = unmarkResult;
	} else {
		unmarkResult(id);
		undo = unmarkResult;
	}
	var indicator = $('indicator '+id);
	indicator.show();
	new Ajax.Request(url, {
		  method: 'get',
		  onFailure: function(transport) {
		  //alert(transport);
			  undo();
		  },
		  onSuccess: function(transport) {
			  //alert(transport);
		  },
		  onComplete: function(transport) {
		      indicator.hide();
		      //alert("Response received.");
		  }
		});
	//alert("Request created.");
}
// Below: These are LAME.  There must be a better way.  Find it, Fix it.
function promoteResult(id) {
	$("promote on "+id).show();
	$("promote off "+id).hide();
	$("demote on "+id).hide();
	$("demote off "+id).show();
}
function unmarkResult(id) {
	$("promote on "+id).hide();
	$("promote off "+id).show();
	$("demote on "+id).hide();
	$("demote off "+id).show();
}
function unpromoteResult(id) {
	$("promote on "+id).hide();
	$("promote off "+id).show();
}
function demoteResult(id) {
	$("promote on "+id).hide();
	$("promote off "+id).show();
	$("demote on "+id).show();
	$("demote off "+id).hide();
}
function undemoteResult(id) {
	$("demote on "+id).hide();
	$("demote off "+id).show();
}

function clk(href) {
	location.href=href;
	return true;
}
  --></script>
</head>
<body class="nihilo">
<s:set name="N_GENESUMMARY" value="10"/>
<s:set name="N_ATTRSUMMARY" value="5"/>
<s:action name="Header" executeResult="true"/>

<div>

<img src="images/nies-logo.gif" style="width:15em; float:left"/>
<div style="float:left; margin-left:1.5em;  padding: 0.5em; margin-bottom:0.5em" xstyle="border-bottom:1pt solid #dddddd; background:#f6f6fc; border-right:1pt solid #dddddd; border-top:1pt solid #eeeeee; border-left:1pt solid #eeeeee;">
<tiles:insertTemplate template="/SearchFormOnly.jsp" flush="true"/>
</div>
<br style="clear:both"/>
</div>



<p style="margin-top:0;margin-bottom:.1em">
	<b><s:property value="nresults"/></b> results for 
	<b><s:property value="query.queryString"/></b> (<b><s:property value="runtime"/></b> secs).
	<a href="<s:url action="ModelFile"/>">View model</a>
	 
</p>
<p style="margin-top:0;margin-bottom:.5em">
Help us improve our algorithm by marking the results you like <img src="images/promote-empty.png"/> and the results you don't like <img src="images/demote-empty.png"/>.
</p>


<div id="tabContainer" dojoType="dijit.layout.TabContainer" style="width:100%" doLayout="false">
<s:iterator value="tabs" status="tabstatus">
<s:set name="selected" value="%{tab == title}"/>
<div dojoType="dijit.layout.ContentPane" id="<s:property value="title"/>Pane" title="<s:property value="title"/> (<s:property value="nresults"/>)" selected="${selected}">
	
	<div><p class="niesleft">Results <s:property value="starti"/>-<s:property value="endi"/> of <s:property value="nresults"/></p>
	<s:set name="lowerBound" value="%{page-5}"/><s:set name="upperBound" value="%{page+5}"/>
	<s:if test="#lowerBound < 1"><s:set name="lowerBound" value="1"/></s:if>
	<s:if test="#upperBound > npages"><s:set name="upperBound" value="%{npages}"/></s:if>
	<s:bean name="org.apache.struts2.util.Counter" var="pagelist"><s:param name="first" value="%{#lowerBound}"/><s:param name="last" value="%{#upperBound}"/></s:bean>
	<p class="niesright">
	Page 
	<s:iterator value="#pagelist">
	<s:if test="#pagelist.current-1 == page"><s:property/></s:if>
	<s:else><a href="<s:url action="ModelBasedSearch" includeParams="get"><s:param name="pagechange" value=""/><s:param name="pages" value="pages"/><s:param name="tab" value="title"/></s:url>&pagechange=<s:property value="#tabstatus.count"/>,<s:property value="#pagelist.current-1"/>"><s:property/></a></s:else>
	<s:if test="#pagelist.current-1 < #upperBound"> | </s:if>
	</s:iterator>
	of <s:property value="npages"/>
	</p>
	<br style="clear:both"/>
	</div>
	
	
	
	
<s:iterator value="results" status="parity">
	
<!--	<s:set name="hasOffsite" value="%{href.length()>0}"/>-->

	<s:url var="rfOffsite" action="RelevanceFeedback" escapeAmp="%{false}">
		<s:param name="queryTerms" value="%{queryTerms}"/>
		<s:param name="queryParams" value="%{queryParams}"/>
		<s:param name="queryId" value="%{query.getQid()}"/>
		<s:param name="document" value="%{id}"/>
		<s:param name="rank" value="%{rank}"/>
		<s:param name="url"  value="%{href}"/>
		<s:param name="rank" value="%{rank}"/>
	</s:url>
	<s:url var="newSearch" action="ModelBasedSearch" escapeAmp="%{false}">
		<s:param name="keywords" value="%{id}"/>
		<s:param name="rf" value="%{rf}"/>
		<s:param name="depth" value="%{depth}"/>
	</s:url>
	<s:url var="rfOnsite" action="RelevanceFeedback" escapeAmp="%{false}">
		<s:param name="queryTerms" value="%{queryTerms}"/>
		<s:param name="queryParams" value="%{queryParams}"/>
		<s:param name="queryId" value="%{query.getQid()}"/>
		<s:param name="document" value="%{id}"/>
		<s:param name="rank" value="%{rank}"/>
		<s:param name="url"  value="%{#newSearch}"/>
		<s:param name="rank" value="%{rank}"/>
	</s:url>
	<s:url var="rfurl" action="RelevanceFeedback" escapeAmp="%{false}">
		<s:param name="queryTerms" value="%{queryTerms}"/>
		<s:param name="queryParams" value="%{queryParams}"/>
		<s:param name="queryId" value="%{query.getQid()}"/>
		<s:param name="document" value="%{id}"/>
		<s:param name="rank" value="%{rank}"/>
	</s:url>
	
<div id="<s:property value="id"/>" class="niesresult <s:if test="#parity.odd == true ">odd</s:if><s:else>even</s:else>">
	

	<div class="result-title">
			<s:if test="rf">
				<!--  (<s:property value="relevanceMark"/>) --> 
				<a onclick="doFeedback('<s:property value="#rfurl"/>&type=demote', '<s:property value="id"/>');"><img id="demote off <s:property  value="id"/>"  src="images/demote-empty.png"  alt="Mark nonrelevant"   <s:if test="relevanceMark == 'demote'">style="display:none"</s:if>/></a>
				<a onclick="doFeedback('<s:property value="#rfurl"/>&type=unmark', '<s:property value="id"/>');"><img id="demote on <s:property   value="id"/>"  src="images/demote-full.png"   alt="Unmark nonrelevant" <s:if test="relevanceMark != 'demote'">style="display:none"</s:if>/></a>
			    <a onclick="doFeedback('<s:property value="#rfurl"/>&type=promote','<s:property value="id"/>');"><img id="promote off <s:property value="id"/>"  src="images/promote-empty.png" alt="Mark relevant"      <s:if test="relevanceMark == 'promote'">style="display:none"</s:if>/></a>
			    <a onclick="doFeedback('<s:property value="#rfurl"/>&type=unmark', '<s:property value="id"/>');"><img id="promote on <s:property  value="id"/>"  src="images/promote-full.png"  alt="Unmark relevant"    <s:if test="relevanceMark != 'promote'">style="display:none"</s:if>/></a>
			    <span id="indicator <s:property value="id"/>" style="display:none;"><img src="images/indicator.gif" /></span>
			</s:if>
			<!-- citation: -->
			<s:property escape="false" value="citation"/>
			<!-- /citation -->
	</div>

	<div class="result-explanation">
		<!-- ajaxanchors begin -->
		<ajax:anchors target="result-explanation-details ${id}">
			<!-- ajaxanchors inside before score -->
			<s:property escape="false" value="score"/>
			<!-- ajaxanchors inside after score -->
		</ajax:anchors>
		<!-- ajaxanchors end -->
	</div>

	<div class="result-explanation-details" id="result-explanation-details <s:property value="id"/>"></div>
</div>
	
</s:iterator>
	
	<s:bean name="org.apache.struts2.util.Counter" var="pagelist"><s:param name="first" value="%{#lowerBound}"/><s:param name="last" value="%{#upperBound}"/></s:bean>
	<p class="niesright">
	Page 
	<s:iterator value="#pagelist">
	<s:if test="#pagelist.current-1 == page"><s:property/></s:if>
	<s:else><a href="<s:url action="ModelBasedSearch" includeParams="get"><s:param name="pagechange" value=""/><s:param name="pages" value="pages"/><s:param name="tab" value="title"/></s:url>&pagechange=<s:property value="#tabstatus.count"/>,<s:property value="#pagelist.current-1"/>"><s:property/></a></s:else>
	<s:if test="#pagelist.current-1 < #upperBound"> | </s:if>
	</s:iterator>
	of <s:property value="npages"/>
	</p>
	<br style="clear:both"/>
	
</div></s:iterator>
</div>

<s:include value="footer.jsp"/>
</body>
</html>