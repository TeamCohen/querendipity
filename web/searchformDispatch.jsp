<%@ taglib prefix="s" uri="/struts-tags" %>
<script language="javascript">
dojo.require("dijit.form.Select");
dojo.require("dijit.InlineEditBox");
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
	elt = dijit.byId('<s:property value="orderBy"/>_file');
	if (typeof(elt) !== 'undefined') {
		<s:if test="topic.length() > 0">// using topic
		elt.set('value','<s:property value="topic"/>');
		//console.log("onLoad setting <s:property value="orderBy"/>_file to <s:property value="topic"/>");
		</s:if><s:elseif test="file.length() > 0">// using file
		elt.set('value','<s:property value="file"/>');
		//console.log("onLoad setting <s:property value="orderBy"/>_file to <s:property value="file"/>");
		</s:elseif>
	} //else console.log("onLoad: dijit elt <s:property value="orderBy"/>_file not defined.");
}

dojo.addOnLoad(makeSelections);
dojo.addOnLoad(function() {dojo.style(dijit.byId('orderbydetails').domNode,{display:'block'});});
</script>
<style>
<!--
.searchform-caption {color:#999; font-size:8pt; height:2em; padding-top:4pt;}
.searchform-control {width:300px}
.searchform-controlcolumn {width:400px}
.inline {width:auto}
.help {float:right;}
.fieldvalue {font-weight: bold; font-family: Courier, Courier New, fixed-width}
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
		<td class="searchform-labels">Show:</td>
		<td class="searchform-controlcolumn">
			<div dojoType="dojox.form.DropDownStack" stackId="orderbydetails" stackPrefix="orderBy" 
				    id="orderBy" name="orderBy" class="searchform-control" value="">
				    <script type="dojo/connect" event="onChange">
				    	filefield = dojo.query('[name$='+this.getValue()+'_file]')[0];
				    	if (filefield != null) {
							console.debug("orderBy selection setting file");
				    		dojo.byId("file").setValue(filefield.getValue());
				    	} //else console.debug("orderBy selection setting file: no file field for orderBy "+this.getValue());
				    </script>
		    </div>
			<div dojoType="dijit.layout.StackContainer" id="orderbydetails" doLayout="false" style="display:none">
		
				<div dojoType="dijit.layout.ContentPane" title="<s:property value="niesConfig['nies.searcher.Search']"/>" id="orderByKeyword" class="searchform-sub">
					<!-- <div class="help"><span class="searchform-caption"><a href="#">What is this?</a></span></div> -->
					<input dojoType="dijit.form.TextBox" id="keywords" name="keywords" 
						class="searchform-control" placeHolder="search terms" value="<s:property value="keywords"/>"/>
					<input type="hidden" id="depth" name="depth" value="<s:property value="depth"/>"/>
					<input type="hidden" id="density" name="density" value="<s:property value="density"/>"/>
					<div class="searchform-caption">
						(depth
							<span class="fieldvalue" dojoType="dijit.InlineEditBox" editor="dijit.form.TextBox" width="<s:property value="%{depth.toString().length()*0.7}"/>em" title="depth">
								<script type="dojo/connect" event="onChange">
									console.debug("orderByKeyword setting depth");
									dojo.byId("depth").setValue(this.getValue());
								</script>
								<s:property value="depth"/>
							</span>,
						 density 
						 	<span class="fieldvalue" dojoType="dijit.InlineEditBox" editor="dijit.form.TextBox" width="<s:property value="%{density.toString().length()*0.7}"/>em" title="density">
						 		<script type="dojo/connect" event="onChange">
									console.debug("orderByKeyword setting density");
						 			dojo.byId("density").setValue(this.getValue());
						 		</script>
						 		<s:property value="density"/>
						 	</span>)
					</div>
				</div>
				
				<s:if test="niesConfig['nies.searcher.TopicIndex.walkfile'].length() > 0">
				<div dojoType="dijit.layout.ContentPane" title="<s:property value="niesConfig['nies.searcher.TopicIndex']"/>" id="orderByTopicIndex" class="searchform-sub">
					<input type="hidden" id="maxResults" name="maxResults" value="20"/>
					<input type="hidden" id="_TopicIndex_file" name="TopicIndex_file" 
						value="<s:property value="niesConfig['nies.searcher.TopicIndex.walkfile']"/>"/>
					<div class="searchform-caption">
						(default: <span id="TopicIndex_file" class="fieldvalue" 
										dojoType="dijit.InlineEditBox" editor="dijit.form.TextBox" 
										width="<s:property value="niesConfig['nies.searcher.TopicIndex.walkfile'].length()*0.7"/>em">
							<script type="dojo/connect" event="onChange">
								console.debug("TopicIndex setting file");
								dojo.byId("_TopicIndex_file").setValue(this.getValue());
								dojo.byId("file").setValue(this.getValue());
							</script>
							<s:property value="niesConfig['nies.searcher.TopicIndex.walkfile']"/>
							</span>)
					</div>
				</div>
				</s:if>
				
				<s:if test="niesConfig['nies.searcher.TopicDescription.ajaxStringsFile'].length() > 0">
				<div dojoType="dijit.layout.ContentPane" title="<s:property value="niesConfig['nies.searcher.TopicDescription']"/>" id="orderByTopic" class="searchform-sub">
					<!-- <div class="help"><span class="searchform-caption"><a href="#">What is this?</a></span></div> -->
					<div dojoType="dojo.data.ItemFileReadStore" url="ajaxstrings.view?file=<s:property value="niesConfig['nies.searcher.TopicDescription.ajaxStringsFile']"/>&type=json&label=field1&identifier=field0"
						jsId="topicList"></div>
					<div dojoType="dijit.form.Select" id="Topic_file" name="Topic_file" class="searchform-control" store="topicList" sortByLabel="false"/>
						<script type="dojo/connect" event="onChange">
							if (dijit.byId("orderBy").getValue() === "Topic") {
								console.debug("TopicDescription setting file");
								var field = dojo.query('[name$=Topic_file]')[0];
								dojo.byId("file").setValue(field.getValue());
							} else console.log("Squashing spurious TopicDescription onChange() message");
						</script>
					</div>
					<span class="searchform-caption"></span>
				</div>
				</s:if>
				
				<s:if test="ghirlConfig['pra.model'].length() > 0">
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
				</s:if><s:else><!-- pra.model = "<s:property value="ghirlConfig['pra.model']"/>" --></s:else>
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
		<td colspan="2" class="clear" align="right">
			<input type="hidden" id="file" name="file" value="<s:property value="file"/>"></input>
			<s:submit/>
		</td>
	</tr>
</table>

</s:form>
