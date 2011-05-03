package nies.metadata;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Node implements Serializable {

  private String id;
  private String name;

  public Node(String name, String id) {
    this.name = name;
    this.id   = id;
  }

  public String getName() { return this.name; }
  public String getId()   { return this.id;   }

  public String toString() {
    // Why do we put the attribute twice? -katie
    return new ToStringBuilder(this).append(name, id).append(name, id).toString();
  }

}
