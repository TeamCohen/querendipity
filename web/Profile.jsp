<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES: <s:property value="task"/> Profile</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
  <style>
  	.twocolumn td {padding: 0 0.5em 0 0.5em; vertical-align: top}
  	.twocolumn .label {text-align:right}
  	.twocolumn .value {background:#eee; width:200px}
  	.twocolumn .old   {color:#999}
  	.fieldcaption {font-size:0.6em; color:#aaa; width:200px}
  </style>
</head>
<body class="nihilo">
<!-- Header -->
<s:action name="Header" executeResult="true"/>
<!-- /Header -->

<s:actionmessage/>

<h1><s:property value="profile.username"/>'s Profile</h1>
<s:actionerror/>

	<s:if test="task == 'edit'"><form method="post" action="Profile_save"></s:if>
		<table class="twocolumn">
			<tr>
				<td class="label">Publishing Names</td>
				<s:if test="task == 'edit'"><td class="edit">
					<input type="text" 
					       id="profile.publishAs" 
					       name="profile.publishAs" 
					       value="<s:property value="profile.publishAs"/>"
					       style="width:200px"/>
					<br/><div class="fieldcaption">You want a comma-separated list, e.g. Cohen_W, Cohen_WW</div>
				</td></s:if>
				<td class="value">
					<s:property value="profile.publishAs"/>
				</td>
			</tr>
			<s:if test="task == 'edit'"><tr>
				<td class="label" colspan="2">
					<s:submit type="input" value="Save" theme="simple"/>
				</td>
			</tr></s:if>
			<tr>
				<td class="label">Reading History</td>
				<s:if test="task == 'edit'"><td class="edit">
					<textarea id="readingHistory" 
					          name="readingHistory"
					          rows="10"
					          style="width:200px"><s:property value="readingHistory"/></textarea>
				</td></s:if>
				<td class="value">
					<pre style="margin-top:1em"><s:property value="readingHistory"/></pre>
				</td>
			</tr>
			<s:if test="task == 'edit'"><tr>
				<td class="label" colspan="2">
					<s:submit type="input" value="Save" theme="simple"/>
				</td></s:if>
			</tr>
		</table>
		
	<s:if test="task == 'edit'"></form></s:if>

<s:if test="task == 'view'"><p><a href="<s:url action="Profile_edit"/>">Edit</a></p></s:if>


<s:debug/>

<s:include value="footer.jsp"/>
</body>
</html>
