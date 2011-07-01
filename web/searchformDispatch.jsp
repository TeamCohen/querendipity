<%@ taglib prefix="s" uri="/struts-tags" %>
<script>
dojo.require("dijit.form.Select");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.FilteringSelect");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.layout.StackContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dojox.form.DropDownStack");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojox.data.QueryReadStore");
dojo.require("dojox.data.XmlStore");

function makeSelections() {
	var elt;
	elt = dijit.byId('orderBy'); elt.set('value','<s:property value="orderBy"/>');
	elt = dijit.byId('file'); elt.set('value','<s:property value="topic"/>');
}
var topicList = new dojo.data.ItemFileReadStore({url:'ajaxstrings.view?file=topics&type=json&label=field1&identifier=field0'});

dojo.addOnLoad(makeSelections);
</script>
<style>
<!--
.searchform-caption {color:#999; font-size:8pt}
.searchform-control {width:300px}
.searchform-controlcolumn {width:400px}
.inline {width:auto}
.help {float:right;}
table {border-collapse:collapse; }
td {border-bottom: 1pt solid #ddd; vertical-align: top; padding: 2pt; margin:0}
td.searchform-labels { border:none}
td.clear {border:none}
-->
</style>

<s:form theme="simple" action="SearchDispatcher">
<input type="hidden" name="searchform" id="searchform" value="Dispatch"/>
<input type="hidden" name="rf" id="rf" value="<s:property value="rf"/>"/>

<table>
	<tr>
		<td class="searchform-labels">Show me:</td>
		<td class="searchform-controlcolumn">
			<div dojoType="dojox.form.DropDownStack" stackId="orderbydetails" stackPrefix="orderBy" 
				    id="orderBy" name="orderBy" class="searchform-control" value="">
		    </div>
		</td>
	</tr>
	<tr id="subform">
		<td class="searchform-labels">&nbsp;</td>
		<td><div dojoType="dijit.layout.StackContainer" id="orderbydetails" doLayout="false">
		
			<div dojoType="dijit.layout.ContentPane" title="Results near a keyword" id="orderByKeyword" class="searchform-sub">
				<!-- <div class="help"><span class="searchform-caption"><a href="#">What is this?</a></span></div> -->
				<input dojoType="dijit.form.TextBox" id="keywords" name="keywords" 
					class="searchform-control" placeHolder="search terms" value="<s:property value="keywords"/>"/>
				<input type="hidden" id="depth" name="depth" value="<s:property value="depth"/>"/>
				<input type="hidden" id="density" name="density" value="<s:property value="density"/>"/>
				<div class="searchform-caption">(depth <b><s:property value="depth"/></b>,
					 density <b><s:property value="density"/></b>.
					<!-- <a href="#">Change...</a> -->)</div>
			</div>
			
			<div dojoType="dijit.layout.ContentPane" title="Topics" id="orderByTopic" class="searchform-sub">
				<!-- <div class="help"><span class="searchform-caption"><a href="#">What is this?</a></span></div> -->
				<div dojoType="dijit.form.Select" id="file" name="file" 
					class="searchform-control" store="topicList" sortByLabel="false"/></div>
				<span class="searchform-caption"></span>
			</div>
			
			<s:if test="ghirlProperties['pra.model'] != ''">
			<div dojoType="dijit.layout.ContentPane" title="Reading recommendations" id="orderByReadingRex" class="searchform-sub">
				
				<div class="help"><span class="searchform-caption"><a href="#">What is this?</a></span></div>
				<div dojoType="dijit.layout.StackContainer" id="readingrexdetails" doLayout="false">
					<div dojoType="dijit.layout.ContentPane" title="Static" id="orderByReadingRexForm-static">
						<input type="hidden" id="RRstandingAuthor" name="RRstandingAuthor" value="Woolford_J"/>
						For <b>Woolford_J</b> (14 seeds) <span class="searchform-caption"><a href="javascript://void(0)" onClick="dijit.byId('readingrexdetails').forward()">Change...</a></span>
					</div>
					<div dojoType="dijit.layout.ContentPane" title="Editable" id="orderByReadingRexForm-editable">
						For <select dojoType="dijit.form.Select" id="RRstandingAuthor" name="RRstandingAuthor" class="searchform-control inline">
								<option>me: Woolford_J</option>
								<option>Edit my info...</option>
								<option disabled="disabled">------------</option>
								<option>Jelena</option>
								<option>Frank</option></select>  (<span id="reading-rex-seeds">14</span> seeds) 
					</div>
				</div>
			</div>
			
			<div dojoType="dijit.layout.ContentPane" title="Citing recommendations" id="orderByCiteRex" class="searchform-sub">
				<div class="help"><span class="searchform-caption"><a href="#">What is this?</a></span></div>
				<div dojoType="dijit.layout.StackContainer" id="citerexdetails" doLayout="false">
					<div dojoType="dijit.layout.ContentPane" title="Static" id="orderByCiteRexForm-static">
						For <b>Woolford_J</b> <span class="searchform-caption"><a href="javascript://void(0)" onClick="dijit.byId('citerexdetails').forward()">Change...</a></span>
					</div>
					<div dojoType="dijit.layout.ContentPane" title="Editable" id="orderByCiteRexForm-editable">
						<div dojoType="dojox.data.QueryReadStore" jsId="graphAuthors" url="autocomplete.view?type=json"></div>
						For <div dojoType="dijit.form.ComboBox" value="Woolford_J" store="graphAuthors" searchAttr="author"
									id="CRstandingAuthor" name="CRstandingAuthor" class="searchform-control inline"></div>
					</div>
				</div>
			</div>
			</s:if>
			
		</div><!-- end stack -->
	</td>
	</tr>
	<!-- Filtering not yet implemented
	<tr id="add-filter">
		<td class="searchform-labels"><a href="#">+ Add Filter</a></td>
		<td>&nbsp;</td>
	</tr>
	<tr id="filter-1">
		<td class="searchform-labels">Filtering on:</td>
		<td><input dojoType="dijit.form.TextBox" id="filterKeyword" name="filterKeyword" class="searchform-control"/>
			<div class="searchform-caption">(depth <b>2</b>, density <b>1000</b>. <a href="#">Change...</a>)</div>
		</td>
	</tr>
	-->
	
	<tr>
		<td colspan="2" class="clear" align="right"><s:submit/></td>
	</tr>
</table>

</s:form>