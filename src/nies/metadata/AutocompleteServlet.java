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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
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


public class AutocompleteServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AutocompleteServlet.class);
	public static final String PARAM_AUTHORS="author",
	PARAM_PAPERS="paper",
	PARAM_GENES="gene",
	PARAM_TYPE="type";
	public static final String TYPE_JSON="json",
	TYPE_XML ="xml";
	private ServletContext servletContext;
	private BaseAjaxServlet ajaxServlet = new XmlAutocompleteServlet();
	private List<Node> list = null;

	public AutocompleteServlet() { super(); log.info("AutocompleteServlet logging properly.");}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String type = req.getParameter(PARAM_TYPE); if (type == null) type = TYPE_XML;

		boolean MATCH_ANYWHERE = false;
		boolean CASE_MATTERS = false;
		servletContext = getServletContext();

		String[][] nodeInfo = { 
				{req.getParameter(PARAM_AUTHORS),"author"},
				{req.getParameter(PARAM_PAPERS),   "pmid"},
				{req.getParameter(PARAM_GENES),    "gene"}};

		log.debug("handling Request: a="+nodeInfo[0][0]+", p="+nodeInfo[1][0]+", g="+nodeInfo[2][0]+", t="+req.getParameter(PARAM_TYPE)+"="+type);

		servletContext = getServletContext();

		String query=null;
		for (String[] params : nodeInfo) {
			if (params[0] != null) {
				query = params[1];
				NodeService service = (NodeService) servletContext.getAttribute(params[1]+"Service");
				if (service == null) {
					log.error("No service "+params[1]+"Service exists!");
					continue;
				}
				list = service.getByName(params[0], MATCH_ANYWHERE, CASE_MATTERS);
				if (list.isEmpty()) log.debug("No results");
				break;
			}
		}

		if (type.equals(TYPE_XML)) {
			try {
				ajaxServlet.service(req, resp);
			} catch (Exception e) {
				log.error("problem using ajax servlet:",e);
				return;
			}
		} else if (type.equals(TYPE_JSON)) {
			// {"items":[ {"author":"Woolford_J"}, {"pmid":"123456"}]}
			// {"items":[]}
			StringBuilder jsonbuilder = new StringBuilder("{\"items\":[");
			int i=0;
			for (Iterator<Node> it = list.iterator(); it.hasNext(); i++) {
				Node n = it.next();
				jsonbuilder
					.append("{\"").append(query).append("\":\"").append(n.getId()).append("\"}");
				if (it.hasNext()) jsonbuilder.append(", ");
			}
			jsonbuilder.append("]}");
			resp.setContentType("application/json");
			PrintWriter out;
			try {
				out = resp.getWriter();
				out.print(jsonbuilder.toString());
				if (list.size() < 20) log.debug(jsonbuilder.toString());
				out.flush();
			} catch (IOException e) {
				log.error("Couldn't get output writer for Autocomplete:",e);
			}
		} else log.warn("Weird type: '"+type+"'");
	}

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
	private class XmlAutocompleteServlet extends BaseAjaxServlet {
		/**
		 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public String getXmlContent(HttpServletRequest request, HttpServletResponse response)
		{
			if (list != null) {
				try {
					String theXmlString;
					theXmlString = new AjaxXmlBuilder().addItems(list, "id", "id").toString();
					log.debug(theXmlString);
					return theXmlString;
				} catch (Exception e) {
					log.error("Problem getting Ajax XML Builder:",e);
				}
			}
			return new AjaxXmlBuilder().toString();
		}
	}
}
