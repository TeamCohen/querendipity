/**
 * Copyright 2005 Darren L. Spurgeon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nies.metadata;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;

// import org.ajaxtags.demo.AuthorService;
// import org.ajaxtags.demo.GeneService;
// import org.ajaxtags.demo.PmidService;
//import org.ajaxtags.helpers.AjaxXmlBuilder;
//import org.ajaxtags.servlets.BaseAjaxServlet;

import net.sourceforge.ajaxtags.servlets.BaseAjaxServlet;
import net.sourceforge.ajaxtags.xml.AjaxXmlBuilder;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * An example servlet that responds to an ajax:autocomplete tag action. This servlet would be
 * referenced by the baseUrl attribute of the JSP tag.
 * <p>
 * This servlet should generate XML in the following format:
 * </p>
 * <code><![CDATA[<?xml version="1.0"?>
 * <list>
 *   <item value="Item1">First Item</item>
 *   <item value="Item2">Second Item</item>
 *   <item value="Item3">Third Item</item>
 * </list>]]></code>
 * 
 * @author Darren L. Spurgeon
 * @version $Revision: 1.7 $ $Date: 2005/09/16 00:08:39 $
 */
public class AutocompleteServlet extends BaseAjaxServlet {

  private ServletContext servletContext;
  private static final Logger log = Logger.getLogger(AutocompleteServlet.class);

  public AutocompleteServlet() { super(); log.info("AutocompleteServlet logging properly."); }
  /**
   * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public String getXmlContent(HttpServletRequest request, HttpServletResponse response)
     {

    boolean MATCH_ANYWHERE = true;
    boolean CASE_MATTERS = false;
    servletContext = getServletContext();

    String[][] nodeInfo = { {request.getParameter("authors"),"author"},
                            {request.getParameter("papers"),   "pmid"},
                            {request.getParameter("genes"),    "gene"}};
    
    log.debug("handling Request: "+nodeInfo[0][0]+", "+nodeInfo[1][0]+", "+nodeInfo[2][0]);

    servletContext = getServletContext();

    for (String[] params : nodeInfo) {
      if (params[0] != null) {
        try {
          NodeService service = (NodeService) servletContext.getAttribute(params[1]+"Service");
          if (service == null) {
        	  log.error("No service "+params[1]+"Service exists!");
        	  return "Server error";
          }
          List list = service.getByName(params[0], MATCH_ANYWHERE, CASE_MATTERS);
          String theXmlString = new AjaxXmlBuilder().addItems(list, "id", "id").toString();
          return theXmlString;
        } catch (Exception e) { log.error("Problem in Autocomplete: ",e); }
      }
    }

    log.info("no response: |" + request + "|");
    return "NO RESULTS";
  }
}
