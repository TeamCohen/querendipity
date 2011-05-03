<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>NIES</title>
  <link rel="stylesheet" href="lib/dojo-release-1.3.1/dijit/themes/nihilo/nihilo.css">
  <link rel="stylesheet" href="css/nies.css" type="text/css"/>
  <script language="javascript" src="lib/dojo-release-1.3.1/dojo/dojo.js.uncompressed.js" djConfig="parseOnLoad: true"></script>
  <script language="javascript">
  </script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<p><s:property value="modelFile" escape="false"/></p>

<s:include value="footer.jsp"/>
</body>
</html>