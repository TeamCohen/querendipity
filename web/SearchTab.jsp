<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<s:push value="selectedTab">
<tiles:insertTemplate template="/resultsGlobalTabSettings.jsp" flush="true"/>
<tiles:insertTemplate template="resultsTabBody.jsp" flush="true"/>
</s:push>