<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax"%>
  <style>
  .magic {overflow:auto}
  .niesMsg {font-size:8pt}
  </style>
  <script type="text/javascript">
  dojo.require("dijit.InlineEditBox");
  dojo.require("dijit.form.NumberSpinner");
  dojo.require("dojo.parser");
  var nstepsCutoffPoints = [{
  	over:1000,
  	msg:"<span id=\"nstepsMsg\">Slow search, more complete sample</span>"
  }, {
  	over: 300,
  	msg:"<span id=\"nstepsMsg\">Reasonable speed, partial sample</span>"
  }, {
  	over:0,
  	msg:"<span id=\"nstepsMsg\">Fast search, sparse sample</span>"
  }];
  var depthCutoffPoints = [{
  	over:10,
  	msg:"<span id=\"depthMsg\">Very slow search, risking unrelated results</span>"
  }, {
  	over:6,
  	msg:"<span id=\"depthMsg\">Slow search, going farther afield</span>"
  }, {
  	over: 3,
  	msg:"<span id=\"depthMsg\">Reasonable speed, near vicinity of keywords</span>"
  }, {
  	over:0,
  	msg:"<span id=\"depthMsg\">Fast search, directly related to keywords</span>"
  }];
  var setMessage = function(self, cutoffPoints, msgBoxId) {
  	var v = self.getValue();
  	if (v.replace) { // the first run, getValue() returns a string.
  		v = parseInt(v.replace(",",""));
  	}
	var tempMsgs=dojo.filter(cutoffPoints,function(temp) {
		return v > temp.over;
	});
	var tempMsg = tempMsgs[0].msg;
	
	dojo.place(tempMsg, msgBoxId,"replace");
  };
  
  dojo.addOnLoad(function() {
  	setMessage(dojo.byId("depth"),depthCutoffPoints,"depthMsg");
  	setMessage(dojo.byId("NUMSTEPS"),nstepsCutoffPoints,"nstepsMsg");
  });
  </script>

<form class="niesform" action="<s:property value="searchAction"/>.action" method="get" id="Search" name="Search">
<input type="hidden" name="searchAction" id="searchAction" value="<s:property value="searchAction"/>"/>
<input type="hidden" name="maxResults"   id="maxResults"   value="<s:property value="maxResults"/>"/>
<input type="hidden" name="rf"           id="rf"           value="<s:property value="rf"/>"/>
<table style="font-size:100%">
  <tr><td valign="top"><label for="keywords" class="label">Keywords:</label></td>
      <td style="width:25em"><input id="keywords"  class="text" name="keywords" type="text" value="<s:property value="keywords"/>"/> </td>
  </tr>
<!-- Extra domain-specific fields -->
<s:iterator value="extraSearchFields">
<s:if test="top == 'genes'">
  <tr><td valign="top"><label for="genes"    class="label">Genes:   </label></td>
      <td><input id="genes"     class="text" name="genes"    type="text"/> <span id="indicator_gene"   style="display:none;"><img src="images/indicator.gif" /></span></td>
  </tr>
<ajax:autocomplete
	source="genes"
	target="blank"
	baseUrl="${pageContext.request.contextPath}/autocomplete.view"
	className="autocomplete magic"
	indicator="indicator_gene"
	minimumCharacters="1"
	appendSeparator=" " />
</s:if><s:else>
<!--  extra search field "<s:property/>" not available -->
</s:else></s:iterator>
<s:if test="searchAction == 'MergedSearch'">
  <tr><td valign="top"><label for="file" class="label">File:</label></td>
      <td valign="top"><input id="file" class="text" name="file" type="text" value="<s:property value="file"/>"/>
                       <s:fielderror><s:param>file</s:param></s:fielderror></td>
  </tr>
</s:if>
  <!--  Back to common fields again: -->
  <tr><td valign="top"><label for="depth" class="label">Search Depth:</label></td>
  	  <td>
  	  <div name="depth" id="depth" value="<s:property value="depth"/>"
  						dojoType="dijit.form.NumberSpinner" smallDelta="1" intermediateChanges="true" constraints="{min:1}">
					   <script type="dojo/connect" event="onChange">
					   		setMessage(this, depthCutoffPoints, "depthMsg");
			  	       </script>
  				   </div><br/>
  				   <span id="depthMsg" class="niesMsg"/></td>
  </tr>
  <tr><td valign="top"><label for="nsteps" class="label">Walk density:</label></td>
      <td>
	  	  <div name="NUMSTEPS" id="NUMSTEPS" value="<s:property value="NUMSTEPS"/>"
	  	  		 dojoType="dijit.form.NumberSpinner" smallDelta="10" intermediateChanges="true" constraints="{min:1}">
	  	       <script type="dojo/connect" event="onChange">
						   		setMessage(this, nstepsCutoffPoints, "nstepsMsg");
	  	       </script>
	  	  </div><br/>
	  	  <div class="niesright"><input type="submit" id="Search_0" value="Submit"/></div>
	  	  <span id="nstepsMsg" class="niesMsg"/>
	
	  </td>
  </tr>
  <tr class="hdivider"><td>New features (beta):</td>
      <td><input type="submit" id="NodeAnalysis" name="action:NodeAnalysis" value="Analyze Gene"/></td>
  </tr>
</table>
</form>
