<%@ taglib prefix="s" uri="/struts-tags" %>
<script language="javascript"><!--
function genesBack(pmid) {
	dijit.byId(pmid+'-genes').back()
}
function doFeedback(url, id) {
	var undo;
	if (url.match(/promote/)) {
		//alert("Promoting "+id);
		promoteResult(id);
		undo = unpromoteResult;
	} else if (url.match(/demote/)) {
		//alert("Demoting "+id);
		demoteResult(id);
		undo = undemoteResult;
	} else {
		//alert("Unmarking "+id);
		unpromoteResult(id);
		undemoteResult(id);
		undo = function() { alert("Couldn't remove mark. Very confused."); };
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
<!-- Displaying first <s:property value="#N_GENESUMMARY"/> genes, first <s:property value="#N_ATTRSUMMARY"/> attribute values, and no more than <s:property value="#N_MAXATTRVALUES"/> attribute values. -->