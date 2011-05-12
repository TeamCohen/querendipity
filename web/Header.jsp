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
var baseURL = "<s:url action="SearchPage"><s:param name="rf" value="true"/></s:url>&searchAction=";
var doSearch = function () {
	var fullURL = baseURL + $("searchselector").value;
	location.href = fullURL;
}
var gotoSelectedUrl = function(selector) {
	if (selector.value != "") { location.href = selector.value; }
}
-->
</script>
<div id="header" class="colorB">
	<div style="float:left">
		<a href="<s:url value="/"/>">Home</a>
			| <a href="<s:url action="ModelBasedSearchPage"><s:param name="rf" value="true"/></s:url>">Model-Based Search</a>
			| <select id="searchselector" onChange="doSearch();">
				<option value="none" SELECTED>Select search function...</option>
				<option value="Search">Search</option>
				<option value="MergedSearch">Merged Search</option>
				<option value="Search-lazy">Lazy Search</option>
				<option value="Search-laziest">Laziest Search</option>
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
				<option value="<s:url action="Profile_view"/>">Profile</option>
				<option value="<s:url action="PositiveResults"/>">Starred Documents</option>
			</select> 
			| <a href="<s:url action="Logout"/>">Log Out</a>
		</s:if><!-- end second logged-in section -->
		<s:else><!-- if we're not logged in: -->
		<a href="<s:url action="LoginForm"/>">Log In</a>
		</s:else><!-- end not-logged-in section -->
	</div>
	<br style="clear:both"/>
</div>
