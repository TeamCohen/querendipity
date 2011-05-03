<%@ taglib prefix="s" uri="/struts-tags" %>
  <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dijit/themes/nihilo/nihilo.css">
  <link rel="stylesheet" href="css/nies.css" type="text/css"/>
  <script language="javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dojo/dojo.xd.js" djConfig="parseOnLoad: true"></script>
  <script language="javascript"><!--
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.StackContainer");
dojo.require("dojo.fx");
dojo.require("dojo._base.fx");
dojo.require("dojo._base.query");
function genesBack(pmid) {
	dijit.byId(pmid+'-genes').back()
}
function doFeedback(url, id) {
	var undo;
	if (url.match(/promote/)) {
		//alert("Promoting "+id);
		promoteResult(id);
		undo = unpromoteResult;
	} else {
		//alert("Demoting "+id);
		demoteResult(id);
		undo = undemoteResult;
	}
	var indicator = $('indicator '+id);
	indicator.show();
	new Ajax.Request(url, {
		  method: 'get',
		  onFailure: function(transport) {
		  //alert(transport);
			  undo();
		  },
		  onSuccess: function(transport) {
			  //alert(transport);
		  },
		  onComplete: function(transport) {
		      indicator.hide();
		      //alert("Response received.");
		  }
		});
	//alert("Request created.");
}
// Below: These are LAME.  There must be a better way.  Find it, Fix it.
function promoteResult(id) {
	$("promote on "+id).show();
	$("promote off "+id).hide();
	$("demote on "+id).hide();
	$("demote off "+id).show();
}
function unpromoteResult(id) {
	$("promote on "+id).hide();
	$("promote off "+id).show();
}
function demoteResult(id) {
	$("promote on "+id).hide();
	$("promote off "+id).show();
	$("demote on "+id).show();
	$("demote off "+id).hide();
}
function undemoteResult(id) {
	$("demote on "+id).hide();
	$("demote off "+id).show();
}
  --></script>
<s:set name="N_GENESUMMARY" value="10"/>
<s:set name="N_ATTRSUMMARY" value="5"/>
<s:push value="selected">

</s:push>
