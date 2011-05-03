package nies.metadata;

import org.apache.log4j.Logger;

import edu.cmu.lti.algorithm.container.MapSS;

public class EntityCollection {
  private static final Logger log = Logger.getLogger(EntityCollection.class);
  
  //static HashMap mEntInfo = new HashMap();
  static MapSS mEntInfo = null;//new MapSS();

  
  private void init(String webappRoot) {
    log.info("PaperCollection initializing...");
    log.debug("webapp root: "+webappRoot);
    String dir = NiesConfig.getProperty("nies.metadataDirectory");
    String fn = NiesConfig.getProperty("nies.entityCollection");
    
    mEntInfo=MapSS.fromFile(dir+fn, "\t");
  }

  public EntityCollection(String webappRoot) {
    this.init(webappRoot);
  }
  public String getInfor(String name) {
    return (String) mEntInfo.get(name);
  }  
}
