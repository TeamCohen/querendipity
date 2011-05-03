<%@ taglib uri="http://ajaxtags.sourceforge.net/tags/ajaxtags" prefix="ajax" %>

  <script type="text/javascript" src="ajaxtags/js/prototype.js"></script>
  <script type="text/javascript" src="ajaxtags/js/scriptaculous/scriptaculous.js"></script>
  <script type="text/javascript" src="ajaxtags/js/overlibmws/overlibmws.js"></script>
  <script type="text/javascript" src="ajaxtags/js/ajaxtags.js"></script>
  <link rel="stylesheet" type="text/css" href="ajaxtags/css/ajaxtags.css" />
  <link rel="stylesheet" type="text/css" href="ajaxtags/css/displaytag.css" />
  <link rel="stylesheet" href="css/nies.css" type="text/css"/>
  <style>
  .magic {overflow:auto}
  </style>
  
  <tr><td><label for="authors" class="label">Authors:</label></td><td><input id="authors"  class="text" name="authors" type="text"/>	<span id="indicator_author" style="display:none;"><img src="images/indicator.gif" /></span></td></tr>
  <tr><td><label for="genes"   class="label">Genes:  </label></td><td><input id="genes"    class="text" name="genes"   type="text"/> <span id="indicator_gene"   style="display:none;"><img src="images/indicator.gif" /></span></td></tr>
  <tr><td><label for="papers"  class="label">Papers: </label></td><td><input id="papers"   class="text" name="papers"  type="text"/> <span id="indicator_paper"  style="display:none;"><img src="images/indicator.gif" /></span></td></tr>
<ajax:autocomplete
	source="authors"
	target="blank"
	baseUrl="${pageContext.request.contextPath}/autocomplete.view"
	className="autocomplete magic"
	indicator="indicator_author"
	minimumCharacters="3"
	appendSeparator=" " />
<ajax:autocomplete
	source="genes"
	target="blank"
	baseUrl="${pageContext.request.contextPath}/autocomplete.view"
	className="autocomplete magic"
	indicator="indicator_gene"
	minimumCharacters="1"
	appendSeparator=" " />
