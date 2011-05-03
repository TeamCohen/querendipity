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

import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;


public class NodeService {
  private static final Logger log = Logger.getLogger(NodeService.class);
  private final List<Node> nodes = new ArrayList<Node>();

  private static final Map<String,String> dataFiles = new HashMap<String,String>(3);
  static {
    dataFiles.put("pmid",  "pmids.all");
    dataFiles.put("author","authors.all");
    dataFiles.put("gene",  "genes.all");
  }

  public NodeService(String nodetype, String webappRoot) {
    if (!dataFiles.containsKey(nodetype)) log.error("NodeService created with invalid node type "+nodetype);
    try {
      FileInputStream fstream = new FileInputStream(new StringBuilder() //webappRoot)
                                                    .append(NiesConfig.getProperty("nies.metadataDirectory"))
                                                    .append(dataFiles.get(nodetype))
                                                    .toString());
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader  br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      //Read File Line By Line
      while ((strLine = br.readLine()) != null)   {
        // Print the content on the console
        nodes.add(new Node(nodetype, strLine));
      }
      //Close the input stream
      in.close();
    } catch (Exception e){//Catch exception if any
      nodes.add(new Node(nodetype, e.getMessage()));
      log.error(e);
    }
  }


  public List getByName(String name, boolean MATCH_ANYWHERE, boolean CASE_MATTERS) {
    if (!CASE_MATTERS) name = name.toLowerCase();
    List   l = new ArrayList();
    String nodename;
    int    loc;
    for (Node node : nodes) {
    	nodename = CASE_MATTERS ? node.getId() : node.getId().toLowerCase();
    	if (MATCH_ANYWHERE) {
    		loc = nodename.indexOf(name);
    		if      (loc > 0)              l.add(node);
    		else if (loc == 0)             l.add(0,node);
    	} else {
    		if (nodename.startsWith(name)) l.add(node);
    	}
    }
    return l;
  }
}
