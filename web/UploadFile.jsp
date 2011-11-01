<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>Querendipity: Upload File</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>


<s:actionmessage/>
<s:actionerror/>
<s:form action="UploadData" method="POST" enctype="multipart/form-data">
	<s:file name="file" label="File"/>
	<s:submit/>
</s:form>

</body>
</html>