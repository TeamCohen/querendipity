<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>Querendipity: Uploaded Files</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  <style>
	  @import "http://ajax.googleapis.com/ajax/libs/dojo/1.6.1/dojox/grid/resources/Grid.css";
	  @import "http://ajax.googleapis.com/ajax/libs/dojo/1.6.1/dojox/grid/resources/nihiloGrid.css";
  </style>
  <script language="javascript">
  dojo.require("dojox.grid.DataGrid");
  dojo.require("dojo.data.ItemFileReadStore");
  
  dojo.addOnLoad(function() {
  	var link = document.getElementById("upload-showhide");
  	var form = document.getElementById("uploadfileform");
  	form.visiblestate = {
  		'none':function() {
  			form.parentElement.style.height="6.5em";
  			form.style.display="block";
  		},
  		'block':function() {
  			form.style.display='none';
  			form.parentElement.style.height="1em";
  		}
  	};
  	link.onclick=function() {
  		form.visiblestate[form.style.display]();
  	};
  });
  
  var availableFilesData = {
  	identifier: 'filename',
  	label: 'filename',
  	items: [<s:iterator value="files.entrySet()"><s:iterator value="top.value">
		{
			user: "<s:property value="key"/>",
			filename: "<s:property value="name"/>",
			//view: '<a href="<s:url action="UploadedFileView"><s:param name="filename" value="user.username+'/'+name"/></s:url>">View</a>',
			view: '<s:url action="UploadedFileView"><s:param name="filename" value="user.username+'/'+name"/></s:url>',
		},</s:iterator></s:iterator>
	],
  };
  var availableFiles = new dojo.data.ItemFileReadStore({data:availableFilesData});
  function makeViewLink(url) {
  	return '<a href="' + url + '">View</a>';
  }
  </script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2>Uploaded Files</h2>
<s:actionmessage/>
<s:actionerror/>
<div style="margin=left:2em; padding:0.5em; background:#eee">
	<div><a href="#" id="upload-showhide">Add file</a></div> 
	
	<div id="uploadfileform" style="display:none; margin-top:1em; margin-left:2em; padding:0 5px 0 5px; background:#f6f8f8; float:left">
		<s:form action="UploadData" method="POST" enctype="multipart/form-data">
			<s:file name="file" label="File"/>
			<s:submit/>
		</s:form>
	</div>
</div>

<table dojoType="dojox.grid.DataGrid" store="availableFiles" selectionMode="none"
	style="margin-top:3em; width:60em;">
	<thead>
		<tr>
			<th field="user" width="10em">User</th>
			<th field="filename" width="20em">Filename</th>
			<th field="view" width="auto" formatter="makeViewLink">View</th>
		</tr>
	</thead>
</table>

<table style="visibility:hidden"><s:iterator value="files.entrySet()"><s:iterator value="top.value">
<tr>
	<td><s:property value="key"/></td>
	<td><s:property value="name"/></td>
</tr>
</s:iterator>
</s:iterator>
</table>

<s:debug/>

</body>
</html>