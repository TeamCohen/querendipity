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

<div style="float:left; padding: 1em;">
<s:actionmessage/>
<s:form action="Login">
<s:textfield key="username"/>
<s:password key="password"/>
<s:submit/>
</s:form>
</div>

<div style="margin-left:23em; width:20em; padding: 1em; border-left: 2pt solid #bbb; background:#ddd">
Member List:
<ul><s:iterator value="users">
<li><a href="<s:url action="Profile_view"><s:param name="u" value="top"/></s:url>"><s:property/></a></li>
</s:iterator></ul>
</div>

</body>
</html>