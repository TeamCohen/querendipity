//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.01.27 at 01:36:27 PM EST 
//


package nies.ui.vocabulary;

import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.graph.PathSearcher;
import ghirl.util.Distribution;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AttributeAssertion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AttributeAssertion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}selector" minOccurs="0"/>
 *         &lt;element ref="{}filter" minOccurs="0"/>
 *         &lt;element name="assertion" type="{}Assertion"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "AttributeAssertion", propOrder = {
    "selectorPath",
    "filterString",
    "assertion"
})
public class AttributeAssertion extends Transformer {

	@XmlElement
    protected String selectorPath;
	@XmlElement
	protected String filterString;
    @XmlElement(required = true)
    protected Assertion assertion;

	public boolean accepts(GraphId node) {
		Distribution d = this.transform(node);
		if (d.iterator().hasNext()) {
			// then d has stuff in it
			if (this.assertion == Assertion.FULL) return true;
			return false;
		}
		if (this.assertion == Assertion.EMPTY) return true;
		return false;
	}
    
	public String getSelectorPath() { return null; }
    /**
     * Sets the value of the selector property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSelectorPath(String value) {
        this.selector = new PathSearcher(value);
        if (this.graph != null) selector.setGraph(graph);
    }

    public String getFilterString() { return null; }
    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterString(String value) {
        this.filter = new NodeFilter(value);
    }

    /**
     * Gets the value of the assertion property.
     * 
     * @return
     *     possible object is
     *     {@link Assertion }
     *     
     */
    public Assertion getAssertion() {
        return assertion;
    }

    /**
     * Sets the value of the assertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Assertion }
     *     
     */
    public void setAssertion(Assertion value) {
        this.assertion = value;
    }

}
