<%@ taglib prefix="s" uri="/struts-tags" %>
<script language="javascript">
<!--
var hidden=true;
var toggleAdminLinks = function () {
	var elt = $("adminlinks");
	hidden = !hidden;
	if (hidden) {
		elt.hide();
	} else {
		elt.show();
	}
}
var urls = {'EnterQuery':'<s:url action="EnterQuery"><s:param name="rf" value="true"/></s:url>',
            'ModelBasedSearchPage':'<s:url action="ModelBasedSearchPage"><s:param name="rf" value="true"/></s:url>'}
var doSearch = function () {
	var option = $("searchselector").value.split(":");
	var to = urls[option[0]] + (option[1] != undefined ? option[1] : "");
	location.href = to;
}
var gotoSelectedUrl = function(selector) {
	if (selector.value != "") { location.href = selector.value; }
}
-->
</script>
<div id="header" class="colorB">
	<div style="float:left">
		<a href="<s:url value="/"/>">Home</a>
			| <a href="<s:url action="EnterQuery"><s:param name="rf" value="true"/><s:param name="searchform" value="Dispatch"/></s:url>">Search</a>
			<s:if test="ghirlProperties['pra.model'] != ''">| <a href="<s:url action="ModelBasedSearchPage"><s:param name="rf" value="true"/></s:url>">Model-Based Search</a></s:if>
			| <select id="searchselector" onChange="doSearch();">
				<option value="none" SELECTED>All search types...</option>
				<option value="EnterQuery:&searchform=Dispatch">Search(default)</option>
				<option value="ModelBasedSearchPage"<s:if test="ghirlConfig['pra.model'] == null"> disabled="disabled"</s:if>>Model-Based Search</option>
				<option value="EnterQuery:&searchform=Basic&searchAction=Search">Advanced Search</option>
				<option value="EnterQuery:&searchform=Basic&searchAction=MergedSearch">Merged Search</option>
				<option value="EnterQuery:&searchform=Basic&searchAction=Search-lazy">Lazy Search</option>
				<option value="EnterQuery:&searchform=Basic&searchAction=Search-laziest">Laziest Search</option>
			</select>
			<s:if test="%{loggedIn}"><!-- if we're logged in: -->
			| <a onclick="toggleAdminLinks();">Toggle Admin</a>
			<ul id="adminlinks" style="display:none;">
				<li>- <a href="<s:url action="UserList"/>">List Users</a></li>
				<li>- <a href="<s:url action="User_create"/>">Add User</a></li>
				<li>- <a href="<s:url action="RelevancyLog"/>">Relevance Data</a></li>
				<li>- <a href="<s:url action="AdminSettings"/>">Querendipity Settings</a></li>
			</ul>
		</s:if><!-- end logged-in section -->
		</div>
	<div style="float:right; text-align: right">
		<s:if test="%{loggedIn}"><!-- if we're logged in (again): -->
		<b><s:property value="username"/></b> 
			| <a href="<s:url action="User_edit">
						<s:param name="username" value="%{username}"/>
					</s:url>">Account</a> 
			| <select id="userinfoselector" onChange="gotoSelectedUrl(this);">
				<option value="">My Info...</option>
				<option value="<s:url action="Profile_view"/>"<s:if test="niesConfig['nies.readinghistory'] == null"> disabled="disabled"</s:if>>Profile</option>
				<option value="<s:url action="PositiveResults"/>"<s:if test="niesConfig['nies.positivedocuments.tab'] == null"> disabled="disabled"</s:if>>Starred Documents</option>
				<option value="<s:url action="UploadedFileList"/>"<s:if test="niesConfig['nies.uploads'] == null"> disabled="disabled"</s:if>>List Files</option>
				<option value="<s:url action="UploadData"/>"<s:if test="niesConfig['nies.uploads'] == null"> disabled="disabled"</s:if>>Upload File</option>
			</select>
			| <a href="<s:url action="Logout"/>">Log Out</a>
		</s:if><!-- end second logged-in section -->
		<s:else><!-- if we're not logged in: -->
		<a href="<s:url action="LoginForm"/>">Log In</a>
		</s:else><!-- end not-logged-in section -->
	</div>
	<br style="clear:both"/>
</div>
