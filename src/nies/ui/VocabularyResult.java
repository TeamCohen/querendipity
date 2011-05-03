package nies.ui;

import org.apache.log4j.Logger;

import nies.ui.vocabulary.Delimiter;
import nies.ui.vocabulary.ResourceResult;
import nies.ui.vocabulary.TypeDescription;
import nies.ui.vocabulary.TypeResult;

public class VocabularyResult extends Result {
	private static final Logger logger = Logger.getLogger(VocabularyResult.class);
	private static final int MAX_RECURSION=10;
	private static final String start_div="<div class=\"",
	                            end_div="\">",
	                            end_div_n="\">\n",
	                            close_div_n="</div>\n";
	private ResourceResult source;
	private int depth=0;
	public VocabularyResult() {}
	public VocabularyResult(Result template) {
		setId(template.getId());
		setLabel(template.getLabel());
		setRank(template.getRank());
		setScore(template.getScore());
	}
	public void setSource(ResourceResult s) { 
		source = s; 
		setLabel(source.getLabel());
	}
	public String getHtml() {
		depth = 0;
		logger.info("Serializaing...");
		String h=null;
		try {
			h = appendHtmlHead(source, new StringBuilder()).toString();
			logger.info("Serialized: "+h);
		} catch(RuntimeException e) {
			logger.error("Trouble serializing: ",e);
			throw e;
		}
		return h;
	}
	/**
	 * Open resource div
	 * @param resource
	 * @param builder
	 */
	private void resourceHead(ResourceResult resource, StringBuilder builder) {
		// <div class="resourceclass resourcedelimiter">
		builder.append(start_div);
		builder.append(resource.getDescription().getResourceclass());
		builder.append(" ");
		builder.append(resource.getResourceDelimiter().value());
		builder.append(end_div_n);
	}
	/** 
	 * Insert attributes and close resource div
	 * @param resource
	 * @param builder
	 */
	private void resourceTail(ResourceResult resource, StringBuilder builder) {
		if (resource.getAttributes() != null) {
			for(TypeResult type : resource.getAttributes()) {
				appendHtml(type, resource.getAttributeDelimiter(), builder);
				depth--;
			}
		}
		// /div>
		builder.append(close_div_n);
		logger.debug("Recursion level (resource leave): "+depth);
		logger.debug(builder.length()+" after recursion level "+depth);
	}
	
	/** 
	 * Skip the label this time around.
	 * @param resource
	 * @param builder
	 * @return
	 */
	public StringBuilder appendHtmlHead(ResourceResult resource, StringBuilder builder) {
		depth++;
		resourceHead(resource, builder);
		resourceTail(resource, builder);
		return builder;
	}
	/** 
	 * Include labels.
	 * @param resource
	 * @param builder
	 * @return
	 */
	public StringBuilder appendHtml(ResourceResult resource, StringBuilder builder) {
		depth++;
		resourceHead(resource, builder);
		if (resource.getLabel() != null) {
			// <div class="labelclass resourcedelimiter labelstyle">label</div>
			builder.append(start_div);
			builder.append(resource.getDescription().getLabelclass());
			builder.append(" ");
			builder.append(resource.getResourceDelimiter().value());
			builder.append(" ");
			if (resource.getLabelStyle() != null)
				builder.append(resource.getLabelStyle());
			builder.append(end_div); 
			builder.append(resource.getLabel());
			builder.append(close_div_n);
		}

		logger.debug("Recursion level (resource enter): "+depth);
		if (depth > MAX_RECURSION) {
			logger.error("Exceeded "+MAX_RECURSION+" levels of recursion!");
			// </div>
			builder.append(close_div_n);
			return builder; // escape
		}
		resourceTail(resource, builder);
		return builder;
	}
	public void appendHtml(TypeResult type, Delimiter attributeDelimiter, StringBuilder builder) {
		depth++;

		logger.debug("Recursion level (type enter): "+depth);
		
		// <div class="typeclass attrdelimiter">
		builder.append(start_div);
		builder.append(type.getDescription().getTypeclass());
		builder.append(" ");
		builder.append(attributeDelimiter.value());
		builder.append(end_div_n);
		if (type.getTitle() != null) {
			// <div class="titleclass">title</div>
			builder.append(start_div);
			builder.append(type.getDescription().getTitleclass());
			builder.append(end_div);
			builder.append(type.getTitle());
			builder.append(close_div_n);
		}
		
		if (type.getResources() != null) {
			for(ResourceResult resource : type.getResources()) {
				appendHtml(resource, builder);
				depth--;
			}
		}
		// </div>
		builder.append(close_div_n);
		logger.debug("Recursion level (type leave): "+depth);
	}
}