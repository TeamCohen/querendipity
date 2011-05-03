<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>NIES: <s:property value="task"/> User</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  </script>
</head>
<body class="nihilo">
<!-- Header -->
<s:action name="Header" executeResult="true"/>
<!-- /Header -->

<s:set var="saveaction" value="%{'User_save'+task}"/>

<h2><s:property value="task"/> User</h2>
	<s:if test="task == 'Edit'">
<s:form action="User_delete">
<s:hidden name="username"/>
<s:submit type="input" value="Delete User"/>
</s:form>
	</s:if>
<s:form action="%{saveaction}">
<s:actionerror/>
<s:hidden name="task"/>
  <s:if test="task == 'Edit'">
<s:hidden name="uid"/>
  </s:if>
<s:textfield key="username"/>
<s:textfield key="email"/>
<s:password  key="newpassword"/>
<s:password key="newpassword2"/>
<s:submit type="input" value="Save Changes"/>
</s:form>
</body>
</html>