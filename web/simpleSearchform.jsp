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
  
  <tr><td><label for="keywords" class="label">Keywords:</label></td><td><input id="keywords"  class="text" name="keywords" type="text"/> </td></tr>
  <tr><td><label for="genes"    class="label">Genes:   </label></td><td><input id="genes"     class="text" name="genes"    type="text"/> <span id="indicator_gene"   style="display:none;"><img src="images/indicator.gif" /></span></td></tr>
<ajax:autocomplete
	source="genes"
	target="blank"
	baseUrl="${pageContext.request.contextPath}/autocomplete.view"
	className="autocomplete magic"
	indicator="indicator_gene"
	minimumCharacters="1"
	appendSeparator=" " />