<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<html>
<head>
  <title>NIES</title>
  <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dijit/themes/nihilo/nihilo.css">
  <link rel="stylesheet" href="css/nies.css" type="text/css"/>
  <script language="javascript">
  </script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2>Login</h2>
<s:actionmessage/>
<s:form action="Login">
<s:textfield key="username"/>
<s:password key="password"/>
<s:submit/>
</s:form>
</body>
</html>