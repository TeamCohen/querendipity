<%@ taglib prefix="s" uri="/struts-tags" %>
<div dojoType="dijit.layout.ContentPane" 
	 id="<s:property value="title"/>Pane" 
	 title="<s:property value="title"/> (<s:property value="nresults"/>)" 
	 selected="${selected}"
	 href="<s:url action="SearchTab" includeParams="get"><s:param name="tabi" value="#tabstatus.index"/></s:url>">
</div>