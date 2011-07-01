<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!-- Page list -->
	<!-- Action: searcher <s:property value="searcher"/> or lastAction <s:property value="#session['lastAction']"/> or context <s:property value="#context[@com.opensymphony.xwork2.ActionContext@ACTION_NAME]"/> -->
	<!-- Page: <s:property value="page"/> -->
	<!-- Starti: <s:property value="starti"/> -->
	<!-- Endi: <s:property value="endi"/> -->
	<div><p class="niesleft">Results <s:property value="starti"/>-<s:property value="endi"/> of 
		<s:if test="nresults >= 0"><s:property value="nresults"/></s:if>
		<s:elseif test="nshowing >= maxResults">about <s:property value="nresultsEstimate"/></s:elseif>
		<s:else>at least <s:property value="nshowing"/>. <br/>We estimate <s:property value="nresultsEstimate"/> results total; increase the <a href="<s:url action="AdminSettings"/>">time limit</a> to allow them to render.</s:else></p>
	<s:set name="lowerBound" value="%{page-5}"/><s:set name="upperBound" value="%{page+5}"/>
	<s:if test="#lowerBound < 1"><s:set name="lowerBound" value="1"/></s:if>
	<s:if test="#upperBound < 1 || #upperBound > npages"><s:set name="upperBound" value="%{npages}"/></s:if>
	<!-- counter first: <s:property value="#lowerBound"/> -->
	<!-- counter last: <s:property value="#upperBound"/> -->
	<s:bean name="org.apache.struts2.util.Counter" var="pagelist"><s:param name="first" value="%{#lowerBound}"/><s:param name="last" value="%{#upperBound}"/></s:bean>
	<p class="niesright">
	Page 
	<s:iterator value="#pagelist">
	<s:if test="#pagelist.current-1 == page"><s:property/></s:if>
	<s:else><a href="<s:url includeParams="all"><s:param name="pagechange" value=""/><s:param name="pages" value="pages"/><s:param name="tab" value="title"/></s:url>&pagechange=<s:property value="#tabstatus.count"/>,<s:property value="#pagelist.current-1"/>"><s:property/></a></s:else>
	<s:if test="#pagelist.current-1 < #upperBound"> | </s:if>
	</s:iterator>
	of <s:property value="npages"/>
	</p>
	<br style="clear:both"/>
	</div>
<!-- Search Results -->
	<s:iterator value="results" status="parity">	
<tiles:insertTemplate template="/resultsSingleResult.jsp" flush="true"/>
	</s:iterator>

<!-- Page list -->
	<s:bean name="org.apache.struts2.util.Counter" var="pagelist"><s:param name="first" value="%{#lowerBound}"/><s:param name="last" value="%{#upperBound}"/></s:bean>
	<p class="niesright">
	Page 
	<s:iterator value="#pagelist">
	<s:if test="#pagelist.current-1 == page"><s:property/></s:if>
	<s:else><a href="<s:url action="Search" includeParams="get"><s:param name="pagechange" value=""/><s:param name="pages" value="pages"/><s:param name="tab" value="title"/></s:url>&pagechange=<s:property value="#tabstatus.count"/>,<s:property value="#pagelist.current-1"/>"><s:property/></a></s:else>
	<s:if test="#pagelist.current-1 < #upperBound"> | </s:if>
	</s:iterator>
	of <s:property value="npages"/>
	</p>
	<br style="clear:both"/>