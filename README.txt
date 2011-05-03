NIES on Struts: A webapp for searching GHIRL graphs

This file last changed:
Dec 2009 by Katie Rivard - krivard@andrew.cmu.edu

There are two default/test accounts:
 Harry Q. Bovik/insecure
 Administrator/invisible ribosome

Troubleshooting:
	If you:
	  $ ant javadoc
	and see the message:
	  [javadoc] javadoc: warning - Multiple sources of package comments found for package "nies"
	This means that somewhere you're copying package.html to the build directory.  Don't do this.
	Eclipse does this by default.  To tell it not to, open the project properties and:
	  Java compiler > Building > Output folder
	and add "package.html" to the comma-separated filter list.