<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>
		<s:set name="hasOffsite" value="%{href.length()>0}"/>
		<!-- href: [<s:property value="href"/>] hasOffsite: [<s:property value="#hasOffsite"/>] -->
		<s:url var="newSearch" action="Search" escapeAmp="%{false}">
			<s:param name="keywords" value="%{id}"/>
			<s:param name="rf" value="%{rf}"/>
			<s:param name="depth" value="%{depth}"/>
		</s:url><!-- chained search link: <s:property value="#newSearch"/> -->
		<s:url var="rfurl" action="RelevanceFeedback" escapeAmp="%{false}">
			<s:param name="queryTerms" value="%{queryTerms}"/><s:param name="queryParams" value="%{queryParams}"/>
			<s:param name="queryId" value="%{query.getQid()}"/><s:param name="document" value="%{id}"/>
			<s:param name="rank" value="%{rank}"/>
		</s:url><!-- background rf link: <s:property value="#rfurl"/> -->
		<s:url var="rfOffsite" action="RelevanceFeedback" escapeAmp="%{false}">
			<s:param name="queryTerms" value="%{queryTerms}"/><s:param name="queryParams" value="%{queryParams}"/>
			<s:param name="queryId" value="%{query.getQid()}"/><s:param name="document" value="%{id}"/>
			<s:param name="rank" value="%{rank}"/><s:param name="url"  value="%{href}"/>
		</s:url><!-- offsite rf link: <s:property value="#rfOffsite"/> -->
		<s:url var="rfOnsite" action="RelevanceFeedback" escapeAmp="%{false}">
			<s:param name="queryTerms" value="%{queryTerms}"/><s:param name="queryParams" value="%{queryParams}"/>
			<s:param name="queryId" value="%{query.getQid()}"/><s:param name="document" value="%{id}"/>
			<s:param name="rank" value="%{rank}"/><s:param name="url"  value="%{#newSearch}"/>
		</s:url><!-- onsite rf link: <s:property value="#rfOnsite"/> -->
		<div id="<s:property value="title"/>-<s:property value="id"/>" class="niesresult <s:if test="#parity.odd == true ">odd</s:if><s:else>even</s:else>">
			<div class="result-head">
				<div class="rank"><s:if test="rank > 0"><s:property value="rank"/></s:if></div>
				<s:if test="rf"><div class="relevanceFeedback">
					<!--  (<s:property value="relevanceMark"/>) --> 
				    <a onclick="doFeedback('<s:property value="#rfurl"/>&type=promote','<s:property value="title"/>-<s:property value="id"/>');"><img id="promote off <s:property value="title"/>-<s:property value="id"/>" src="images/promote-empty.png" alt="Mark relevant"      <s:if test="relevanceMark == 'promote'">style="display:none"</s:if>/></a>
				    <a onclick="doFeedback('<s:property value="#rfurl"/>&type=unmark', '<s:property value="title"/>-<s:property value="id"/>');"><img id="promote on <s:property value="title"/>-<s:property value="id"/>"  src="images/promote-full.png"  alt="Unmark relevant"    <s:if test="relevanceMark != 'promote'">style="display:none"</s:if>/></a>
					<a onclick="doFeedback('<s:property value="#rfurl"/>&type=demote', '<s:property value="title"/>-<s:property value="id"/>');"><img id="demote off <s:property value="title"/>-<s:property value="id"/>"  src="images/demote-empty.png"  alt="Mark nonrelevant"   <s:if test="relevanceMark == 'demote'">style="display:none"</s:if>/></a>
					<a onclick="doFeedback('<s:property value="#rfurl"/>&type=unmark', '<s:property value="title"/>-<s:property value="id"/>');"><img id="demote on <s:property value="title"/>-<s:property value="id"/>"   src="images/demote-full.png"   alt="Unmark nonrelevant" <s:if test="relevanceMark != 'demote'">style="display:none"</s:if>/></a>
				    <span id="indicator <s:property value="title"/>-<s:property value="id"/>" style="display:none;"><img src="images/indicator.gif" /></span>
				</div></s:if>
				<div class="score"><s:property value="score"/></div>
				<div class="title">
					<s:property value="label"/>
					<a class="link" href="<s:property value="#newSearch" escape="%{false}"/>" onmousedown="return clk('<s:property value="#rfOnsite" escape="%{false}"/>&type=click');">Search this</a>
					<s:iterator value="titleLinks"><a class="link" target="_blank" href="<s:property value="href" escape="%{false}"/>" onmousedown="return clk('<s:property value="#rfurl" escape="%{false}"/>&type=click');"><s:property value="anchorText"/><img src="images/new-window.png"/></a></s:iterator>
					<s:if test="#hasOffsite"><a class="link" href="<s:property value="href" escape="%{false}"/>" onmousedown="return clk('<s:property value="#rfOffsite" escape="%{false}"/>&type=click');">View offsite<img src="images/new-window.png"/></a></s:if>
				</div>
			</div>
<s:if test="displayType == 'pmpaper'"><!-- PmPaper (reasons interface) -->
			<s:if test="reasons != null"><div class="result-explanation">
				<!-- ajaxanchors begin -->
				<ajax:anchors target="result-explanation-details ${title}-${id}">
					<s:property escape="false" value="reasons"/>
				</ajax:anchors>
				<!-- ajaxanchors end -->
			</div>
		
			<div class="result-explanation-details" id="result-explanation-details <s:property value="title"/>-<s:property value="id"/>"></div></s:if>
			</s:if><s:else><!-- non-reason tabs -->
			<div class="nies result-cliptext">
				<table style="width:100%">
				<s:if test="%{#hasOffsite}"><tr><td class="cliptext-heading">Offsite link:</td><td class="niesclip"><a target="_blank" href="<s:property value="href" escape="%{false}"/>" onmousedown="return clk('<s:property value="#rfOffsite" escape="%{false}"/>&type=click');"><s:property value="href"/></a></td></tr></s:if>
<s:if test="displayType == 'paper'"><!--  PaperTab -->
					<tr><td class="cliptext-heading">Citation:</td> <td class="niesclip"><s:property value="citation"/></td></tr>
					<tr><td class="cliptext-heading">Authors:</td> 
						<td class="niesclip"><s:iterator value="authors" status="descParity">
							<a target="_blank" href="<s:property value="href"/>"><s:property value="text"/></a><s:if test="!#descParity.last">, </s:if>
						</s:iterator> </td>
					</tr>
					<tr><td class="cliptext-heading">Genes:</td> 
						<td class="niesclip"><div dojoType="dijit.layout.StackContainer" dolayout="false" id="<s:property value="pmid.text"/>-genes">
							<div dojoType="dijit.layout.ContentPane" title="summary" >
								<s:subset  source="genes" count="#N_GENESUMMARY"><s:iterator status="descParity">
									<a target="_blank" href="<s:property value="href"/>"><s:property value="text"/></a><s:if test="!#descParity.last">, </s:if>
								</s:iterator></s:subset><s:set name="nmoregenes" value="genes.size()-#N_GENESUMMARY"/>
							<s:if test="genes.size() <= #N_GENESUMMARY"></div>
							</s:if><s:else>
									<a onclick="dijit.byId('<s:property value="pmid.text"/>-genes').forward()" class="subtle"><s:property value="#nmoregenes"/> More...</a>
							</div>
							<div dojoType="dijit.layout.ContentPane" title="full" style="overflow:hidden">
								<s:iterator value="genes" status="descParity">
									<a target="_blank" href="<s:property value="href"/>"><s:property value="text"/></a><s:if test="!#descParity.last">, </s:if>
								</s:iterator>
									<a onclick="genesBack('<s:property value="pmid.text"/>')" class="subtle">Less...</a>
							</div>
							</s:else>
						</div></td>
					</tr>
					<tr><td class="cliptext-heading">Year:</td> <td class="niesclip"><s:property value="year"/></td></tr>
</s:if><s:elseif test="displayType == 'configurable'"><!--  ConfigurableTab -->
					<s:iterator value="attributes">
					<s:if test="value.size() > 0">
					<tr><td class="cliptext-heading"><s:property value="key"/></td>
					    <td class="cliptext-value"><div dojoType="dijit.layout.StackContainer" dolayout="false" id="<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>">
							<div dojoType="dijit.layout.ContentPane" title="summary" >
								<s:if test="prose"><s:if test="value.size() > 1"><ul></s:if><s:else><p class="prose" style="margin-top:0"></s:else></s:if>
								<s:subset  source="value" count="#N_ATTRSUMMARY"><s:iterator status="descParity">
									<s:url var="newAttrSearch" action="Search" escapeAmp="%{false}">
										<s:param name="keywords" value="href"/>
										<s:param name="rf"       value="%{rf}"/>
										<s:param name="depth"    value="%{depth}"/>
									</s:url>
									<s:if test="prose"><s:if test="value.size() > 1"><li class="prose"></s:if><s:property value="anchorText"/> <a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>">Search these keywords</a><s:if test="value.size() > 1"></li></s:if></s:if>
									<s:else><a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>"><s:property value="anchorText"/></a>
									<s:if test="!#descParity.last">, </s:if></s:else>
								</s:iterator></s:subset><s:set name="nmoreattrs" value="value.size()-#N_ATTRSUMMARY"/>
								<s:if test="prose"><s:if test="value.size() > 1"></ul></s:if><s:else></p></s:else></s:if>
							<s:if test="value.size() <= #N_ATTRSUMMARY"></div>
							</s:if><s:else>
									<s:if test="prose"><div class="prose-moreless"></s:if><a onclick="dijit.byId('<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>').forward()" class="subtle"><s:property value="#nmoreattrs"/> More...</a><s:if test="prose"></div></s:if>
							</div>
							<div dojoType="dijit.layout.ContentPane" title="full" style="overflow:hidden;display:none">
								<s:if test="prose"><s:if test="value.size() > 1"><ul></s:if><s:else><p class="prose" style="margin-top:0"></s:else></s:if>
								<s:subset source="value" count="maxnvalues_setting"><s:iterator status="descParity">
									<s:url var="newAttrSearch" action="Search" escapeAmp="%{false}">
										<s:param name="keywords" value="href"/>
										<s:param name="rf" value="%{rf}"/>
										<s:param name="depth" value="%{depth}"/>
									</s:url>
									<s:if test="prose"><s:if test="value.size() > 1"><li></s:if><s:property value="anchorText"/> <a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>">Search these keywords</a><s:if test="value.size() > 1"></li></s:if></s:if>
									<s:else><a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>"><s:property value="anchorText"/></a>
									<s:if test="!#descParity.last">, </s:if></s:else>
								</s:iterator></s:subset>
									<s:if test="value.size() > maxnvalues_setting">(truncating after <s:property value="maxnvalues_setting"/>)</s:if>
									<s:if test="prose"><s:if test="value.size() > 1"></ul></s:if><s:else></p></s:else></s:if>
									<s:if test="prose"><div class="prose-moreless"></s:if><a onclick="dijit.byId('<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>').back()" class="subtle">Less...</a><s:if test="prose"></div></s:if>
							</div>
							</s:else>
						</div></td>
					</tr>
					</s:if>
					</s:iterator>
</s:elseif><s:elseif test="displayType=='vocabulary'"><!--  VocabularyTab -->
				<div class="vocabtab"><s:property value="html" escape="false"/></div>
</s:elseif><s:elseif test="displayType=='explore'"><!--  ExploreTab -->
				<s:iterator value="forwardAttributes"><!-- begin attribute -->
				<tr><td style="background:#e3bba6;">
				      <div style="float:right; width:50%; text-align:left">|&rarr; <span class="cliptext-heading"><s:property value="key"/> (<s:property value="value.size"/>)</span> &rarr;</div>
				    </td>
				</tr>
				<tr><td style="background:white">
					<div dojoType="dijit.layout.StackContainer" dolayout="false" id="<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>">
					<div dojoType="dijit.layout.ContentPane" title="summary" >
					<ul><s:subset source="value" count="#N_ATTRSUMMARY"><s:iterator><s:url var="newAttrSearch" action="Search" escapeAmp="%{false}">
							<s:param name="keywords" value="href"/>
							<s:param name="rf"       value="%{rf}"/>
							<s:param name="depth"    value="%{depth}"/></s:url>
						<li><a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>"><s:property value="text"/></a></li>
					</s:iterator></s:subset><!-- end attribute value summary -->
					<s:set name="nmoreattrs" value="value.size()-#N_ATTRSUMMARY"/>
					<s:if test="value.size() <= #N_ATTRSUMMARY"></ul></div>
					</s:if><s:else>
						<li><a onclick="dijit.byId('<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>').forward()" class="subtle"><s:property value="#nmoreattrs"/> More...</a>
					</ul>
					</div>
					<div dojoType="dijit.layout.ContentPane" title="full" style="overflow:hidden">
					<ul><s:subset source="value" count="#N_MAXATTRVALUES"><s:iterator><s:url var="newAttrSearch" action="Search" escapeAmp="%{false}">
							<s:param name="keywords" value="href"/>
							<s:param name="rf"       value="%{rf}"/>
							<s:param name="depth"    value="%{depth}"/></s:url>
						<li><a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>"><s:property value="text"/></a></li>
					</s:iterator></s:subset><!-- end of full attribute list -->
						<s:if test="value.size() > #N_MAXATTRVALUES"><li>(truncating after <s:property value="#N_MAXATTRVALUES"/>)</li></s:if>
						<li><a onclick="dijit.byId('<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>').back()" class="subtle">Less...</a>
					</ul>
					</div></s:else>
					</div>
				</td></tr>
				</s:iterator><!-- end of forward attributes -->
				<s:iterator value="backwardAttributes"><!-- begin attribute -->
				<tr><td style="background:#c0ccf0;">
				      <div style="float:left; width:50%; text-align:right">&larr; <span class="cliptext-heading"><s:property value="key"/> (<s:property value="value.size"/>)</span> &larr;|</div>
				</td></tr>
				<tr><td style="background:white">
					<div dojoType="dijit.layout.StackContainer" dolayout="false" id="<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>">
					<div dojoType="dijit.layout.ContentPane" title="summary" >
					<s:subset source="value" count="#N_ATTRSUMMARY">
					<ul><s:iterator><s:url var="newAttrSearch" action="Search" escapeAmp="%{false}">
							<s:param name="keywords" value="href"/>
							<s:param name="rf"       value="%{rf}"/>
							<s:param name="depth"    value="%{depth}"/></s:url>
						<li><a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>"><s:property value="text"/></a></li>
					</s:iterator>
					</s:subset><s:set name="nmoreattrs" value="value.size()-#N_ATTRSUMMARY"/>
					<s:if test="value.size() <= #N_ATTRSUMMARY"></ul></div>
					</s:if><s:else>
						<li><a onclick="dijit.byId('<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>').forward()" class="subtle"><s:property value="#nmoreattrs"/> More...</a>
					</ul>
					</div>
					<div dojoType="dijit.layout.ContentPane" title="full" style="overflow:hidden">
					<ul><s:subset source="value" count="#N_MAXATTRVALUES"><s:iterator><s:url var="newAttrSearch" action="Search" escapeAmp="%{false}">
							<s:param name="keywords" value="href"/>
							<s:param name="rf"       value="%{rf}"/>
							<s:param name="depth"    value="%{depth}"/></s:url>
						<li><a class="attrLink" href="<s:property value="#newAttrSearch" escape="%{false}"/>"><s:property value="text"/></a></li>
					</s:iterator></s:subset><!-- end of full attribute list -->
					    <s:if test="value.size() > #N_MAXATTRVALUES"><li>(truncating after <s:property value="#N_MAXATTRVALUES"/>)</li></s:if>
						<li><a onclick="dijit.byId('<s:property value="title"/>-<s:property value="id"/>-<s:property value="key"/>').back()" class="subtle">Less...</a>
					</ul>
					</div></s:else>
					</div>	
				</td></tr>
				</s:iterator><!-- end of backward attributes -->
</s:elseif><s:else><!--  ALL OTHER TABS DISPLAY AS FOLLOWS: -->
					<!--  <tr><td class="cliptext-heading">Description:</td> <td>(<s:property value="title"/>) Lorem ipsum dolor sit amet nunc...</td></tr> -->
				</s:else>
				</table>
			</div><!-- end result-cliptext -->
		</s:else></div><!-- end niesresult -->