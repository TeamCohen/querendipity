<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div dojoType="dijit.layout.ContentPane" 
	id="<s:property value="title"/>Pane" 
	title="<s:property value="title"/>
		(<s:if test="nresults >= 0"><s:property value="nresults"/></s:if><s:elseif test="nshowing >= maxResults">~<s:property value="nresultsEstimate"/></s:elseif><s:else><s:property value="nshowing"/>+</s:else>)" 
	selected="${selected}" style="display:none">
<s:if test="displayType == 'external'">
	<iframe src="<s:property value="url"/>" width="100%" height="100%"><p>Your browser won't let us include this content directly, but you can choose to <a href="<s:property value="url"/>" target="_blank">open it in a new window</a>.</p></iframe>
</s:if><s:else>
<tiles:insertTemplate template="resultsTabBody.jsp" flush="true"/>
	</s:else>
</div>