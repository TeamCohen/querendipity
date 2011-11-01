<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>Querendipity: View Uploaded File</title>
  <tiles:insertTemplate template="/head.jsp" flush="true"/>
  <style>
	  @import "http://ajax.googleapis.com/ajax/libs/dojo/1.6.1/dojox/grid/resources/Grid.css";
	  @import "http://ajax.googleapis.com/ajax/libs/dojo/1.6.1/dojox/grid/resources/nihiloGrid.css";
	  .gbrowse-header {opacity:0; background: #ffd; padding: 0.5em}
	  .gbrowse-selection td {padding-left:1em; color:#888; font-size:9pt}
  </style>
  <script language="javascript">
  dojo.require("dojox.grid.DataGrid");
  dojo.require("dojo.data.ItemFileReadStore");
  dojo.require("dojo.html");
  dojo.require("dijit.form.TextBox");
  dojo.require("dijit.InlineEditBox");
  var data = {
  	identifier: 'number',
  	label: 'number',
  	items: [<s:iterator value="data" var="row" status="rowstatus">
		{
			number: <s:property value="#rowstatus.count"/>,<s:iterator value="cols" var="c" status="colstatus">
			'<s:property value="headers[#c-1]"/>': "<s:property value="#row[#c-1].replaceAll('&quot;','')"/>",</s:iterator>
		},</s:iterator>
	],
  };
  var data_dojostore = new dojo.data.ItemFileReadStore({data:data});
  function addTooltip(val) {
  	return '<span title="'+val+'">'+val+'</span>';
  }
  dojo.addOnLoad(function() {
  	dojo.connect(dojo.byId("cols"),"onkeydown", function(evt) {
		if (evt.keyCode == dojo.keys.ENTER) { 	
			var clist = evt.target.value;
			if (!clist)
				clist = evt.target.outerText;
			if (!clist) {
				console.log("Error: couldn't find the columns list in the elemnent" + evt.target);
				console.log(evt);
				return;	
			}
			while(clist.indexOf(" ")>=0) clist = clist.replace(" ","");
			var url = "<s:url action="UploadedFileView"><s:param name="filename" value="filename"/></s:url>"+"&cols="+clist;
			console.log(url);
			window.location=url;
		}
	}); 
	dojo.connect(fileGrid,"onRowClick", function(evt) {
		console.log("clicked row "+evt.rowIndex);
		var link = dojo.byId("gbrowse-link");
		if (link.lastRow === evt.rowIndex) return;
		link.lastRow = evt.rowIndex;
		var url = translate(data.items[evt.rowIndex].chr[0], data.items[evt.rowIndex].exclusion_junctions[0],data.items[evt.rowIndex].inclusion_junctions[0]);
		dojo.fadeOut({node:"gbrowse-link",
			onEnd: function() {
				dojo.html.set(dojo.byId("gbrowse-link"),'<a href="'+url+'">GBrowse for row '+(evt.rowIndex+1)+'</a>'
					+ '<div class="gbrowse-selection"><table><tr>'
					+ "<td>"+data.items[evt.rowIndex].as_event_type[0]+"</td>"
					+ "<td>"+data.items[evt.rowIndex].gene_name[0]+"</td>"
					+ "<td>"+data.items[evt.rowIndex].exclusion_junctions[0]+"</td>"
					+ "<td>"+data.items[evt.rowIndex].inclusion_junctions[0]+"</td>"
					+ "</tr></table></div>"
					);
				dojo.fadeIn({node:"gbrowse-link"}).play();
			}}).play();
		dojo.fadeOut({node:"gbrowse-frame",
			onEnd: function() {
				dojo.byId("gbrowse-frame").src = url;
				dojo.fadeIn({node:"gbrowse-frame"}).play();
			}}).play();
	});
  });
  </script>
  <script language="javascript" src="js/gbrowse.js"></script>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<h2>Uploaded File: <s:property value="filename"/></h2>
<s:actionmessage/>
<s:actionerror/>

<div style="margin-left:2em">
<s:if test="cols.length != maxcols">
<p>Showing columns 
	<span dojoType="dijit.InlineEditBox" 
		  editor="dijit.form.TextBox" 
		  width="<s:property value="cols.length*1.5"/>em"
		  id="cols"><s:property value="cols"/>
	</span>. <a href="<s:url action="UploadedFileView"><s:param name="filename" value="filename"/></s:url>">Show all columns</a></p>
</s:if>
<s:else>
<p>Showing all columns. Show only columns <input dojoType="dijit.form.TextBox" id="cols" name="cols" placeHolder="example: 1,2,3,4"/> 
</p>
</s:else>
<p>Click a row to view junction regions in GBrowse.</p>
</div>

<table dojoType="dojox.grid.DataGrid" 
	store="data_dojostore" 
	selectionMode="single" 
	autoHeight="12" 
	rowSelector="2em"
	style="margin:2em 1em 3em 1em; width:1000;"
	jsId="fileGrid"
	xcanSort="return false;">
	<thead><s:set var="perc" value="95 / cols.size()"/>
		<tr>
			<th field="number" width="3em">#</th><s:iterator value="cols" var="c" status="colstatus">
			<th field="<s:property value="headers[#c-1]"/>" 
				formatter="addTooltip"
				xwidth=<s:property value="#perc"/>><s:property value="#c"/>: <s:property value="headers[#c-1]"/></th></s:iterator>
		</tr>
	</thead>
</table>

<div id="gbrowse-link" class="gbrowse-header"></div> 

<iframe id="gbrowse-frame" style="opacity: 0; width:1000; height:600; margin: 2em 1em 1em 1em;"></iframe>

<s:debug/>

</body>
</html>