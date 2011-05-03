package nies.ui.vocabulary;

import java.util.ArrayList;
import java.util.List;

public class TypeResult {
	private List<ResourceResult> resources;
	private TypeDescription description;
	
	public TypeResult(TypeDescription desc) { 
		this.description = desc;
		this.resources = new ArrayList<ResourceResult>();
	}
	public String getTitle() { return this.description.getTitle(); }
	public Delimiter getResourceDelimiter() { return this.description.getResourceDelimiter(); }
	public List<ResourceResult> getResources() { return resources; }
	public TypeDescription getDescription() { return this.description; }
	public void addResource(ResourceResult r) {
		if (this.resources == null) this.resources = new ArrayList<ResourceResult>();
		resources.add(r);
	}
}
