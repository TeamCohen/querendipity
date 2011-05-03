<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES: <s:property value="task"/> Reading History</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
</head>
<body class="nihilo">
<!-- Header -->
<s:action name="Header" executeResult="true"/>
<!-- /Header -->

<h1>Reading History</h1>
<s:actionerror/>

<s:if test="task == 'edit'">
<form method="post" action="ReadingHistory_save">
<textarea id="readingHistory" name="readingHistory" cols="15" rows="10">
<s:property value="readingHistory"/>
</textarea>
<s:submit type="input" value="Save"/>
</form>
</s:if>
<s:else>
<p><a href="<s:url action="ReadingHistory_edit"/>">Edit</a></p>
<pre><s:property value="readingHistory"/></pre>
</s:else>

<s:debug/>

<s:include value="footer.jsp"/>
</body>
</html>
