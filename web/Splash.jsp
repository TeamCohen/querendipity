<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
  <title>Querendipity</title>
<tiles:insertTemplate template="/head.jsp" flush="true"/>
</head>
<body class="nihilo">
<s:action name="Header" executeResult="true"/>

<center><img src="images/logo-big.png" style="margin:3em 0 4em 0"/></center>
<p>Welcome to Querendipity! 
This app demonstrates how graph-based models can be used to improve the performance 
of information retrieval systems in contexts which have traditionally been dominated by methods
requiring domain knowledge.  Querendipity pulls data from places like PubMed, SGD, and FlyBase, and integrates them in the 
first draft of a context-free search system designed to serve genetics researchers.<p>

<p>If you would like to use this search system and help our research (as machine learning and information
retrieval scientists), please email <a href="mailto:wcohen.NOSPAM@cs.cmu.edu">William Cohen</a> 
and we will set you up with an account.  Keep in mind that this is very much an alpha system, 
and may be buggy; still; if you encounter technical difficulties, please email 
<a href="krivard@andrew.cmu.edu">Katie Rivard</a> with details about your problem.</p>

<s:include value="footer.jsp"/>
</body>
</html>