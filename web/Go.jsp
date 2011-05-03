<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><script>
if('<s:property value="url"/>' == '') {
	location.href = '<s:url action="Splash"/>';
} else {
	location.href = '<s:property value="url" escape="%{false}"/>';
}
</script>
</head>
<body>
<s:actionmessage/>
</body>
</html>