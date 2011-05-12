<%@ taglib prefix="s" uri="/struts-tags" %><%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html>
<head>
  <title>NIES</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  <s:head/>
</head>
<body>

<s:action name="Header" executeResult="true"/>

<center>
<img src="images/nies-logo.gif"/>
<div style="margin-top:2em">

<form action="ModelBasedSearch.action" method="get" id="Search" name="Search">
<input type="hidden" name="maxResults" id="maxResults" value="<s:property value="maxResults"/>"/>
<input type="hidden" name="rf"         id="rf"         value="<s:property value="rf"/>"/>
<input type="hidden" name="model"      id="model"      value="<s:property value="model"/>"/>
<!-- <input type="hidden" name="standingUser" id="standingUser" value="<s:property value="user.publishAs"/>"/> -->


<table style="font-size:100%">
  <!-- Standing User -->
  <tr>
    <td>Currently searching near <s:property value="user.publishAs"/>.</td>
  <tr>
  	<td><label for="standingUser" class="label" >Search near a different author:</label></td>
    <td>
      <input id="standingUser"  class="text" name="standingUser" type="text" VALUE="<s:property value="user.publishAs"/>" />
	  <span id="indicator_user"   style="display:none;"><img src="images/indicator.gif" /></span>
	</td>
  </tr>

<tr><td></td><td>
<input type="checkbox" name="usingHistory" value="true" checked="checked" />
<!--
<s:checkbox name="usingHistory" fieldValue="true" value="usingHistory"/>
-->
Use the reading history of the current user </s>
</td></tr>


<ajax:autocomplete
	source="standingUser"
	target="blank"
	parameters="authors={standingUser}"
	baseUrl="${pageContext.request.contextPath}/autocomplete.view"
	className="autocomplete magic"
	indicator="indicator_user"
	minimumCharacters="1"
	appendSeparator=" " />

<!--
  <td><a href="data/yeast2.authors">complete list</a> </td></tr>
-->

  <tr><td><label for="year" class="label">Year:</label></td>
  <td><input id="year"  class="text" name="year" type="text" value="<s:property value="year"/>"/></td></tr>
  

  <tr><td><label for="keywords" class="label">Keywords(optional):</label></td>
  <td><input id="keywords"  class="text" name="keywords" type="text"/> </td></tr>

  <!-- Extra domain-specific fields -->
<s:iterator value="extraSearchFields">
<s:if test="top == 'genes'">
  <tr><td><label for="genes"    class="label">Genes:   </label></td>
  <td><input id="genes"     class="text" name="genes"    type="text"/> 
  <span id="indicator_gene"   style="display:none;"><img src="images/indicator.gif" /></span></td></tr>
<ajax:autocomplete
	source="genes"
	target="blank"
	baseUrl="${pageContext.request.contextPath}/autocomplete.view"
	className="autocomplete magic"
	indicator="indicator_gene"
	minimumCharacters="1"
	appendSeparator=" " />
</s:if>
<s:else>
<!--  extra search field "<s:property/>" not available -->
</s:else></s:iterator>
  <!--  Back to common fields again: -->
  
  
  <tr><td><label for="depth" class="label">Search Depth:</label></td>
  	  <td>
  	  <div class="niesleft">
  	  <select name="depth" id="depth" style="margin-right: 2em">
  	  	<option value="1" SELECTED>1 - Narrow</option>
  	  	<option value="2">2</option>
  	  	<option value="3">3</option>
  	  	<option value="4">4 - Medium</option>
  	  	<option value="5">5</option>
  	  	<option value="6">6</option>
  	  	<option value="7">7</option>
  	  	<option value="8">8</option>
  	  	<option value="9">9</option>
  	  	<option value="10">10 - Broad</option>
  	  </select>
  	  </div>
  	  <div class="niesright">
  	   <input type="submit" id="Search_0" value="Submit"/>
  	  </div>
  	  <br style="clear:both;"/></td></tr>
</table>
</form>
</div>
</center>

<s:include value="footer.jsp"/>
</body>
</html>