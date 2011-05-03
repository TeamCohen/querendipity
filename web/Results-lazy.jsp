<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<tiles:insertTemplate template="/resultsTemplate.jsp" flush="true">
	<tiles:putAttribute name="tabPane" value="/resultsLazyTabPane.jsp"/>
</tiles:insertTemplate>