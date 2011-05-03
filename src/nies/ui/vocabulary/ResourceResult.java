package nies.ui.vocabulary;

import java.util.ArrayList;
import java.util.List;

public class ResourceResult {
	private String label;
	private List<TypeResult> attributes;
	private TypeDescription description;
	
	public ResourceResult(TypeDescription desc) { 
		this.description = desc;
	}
	public String getLabelStyle() { return this.description.getLabel().getStyle(); }
	public Delimiter getResourceDelimiter()  { return this.description.getResourceDelimiter(); }
	public Delimiter getAttributeDelimiter() { return this.description.getAttributeDelimiter(); }
	public TypeDescription getDescription() { return this.description; }
	public String getLabel() { return label; }
	public void setLabel(String l) { label = l; }
	
	public List<TypeResult> getAttributes() { return attributes; }
	public void addAttribute(TypeResult t) { 
		if (this.attributes == null) this.attributes = new ArrayList<TypeResult>();
		this.attributes.add(t);
	}
}
