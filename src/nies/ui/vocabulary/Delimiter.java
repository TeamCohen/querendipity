//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.01.27 at 01:36:27 PM EST 
//


package nies.ui.vocabulary;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Delimiter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Delimiter">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="inline"/>
 *     &lt;enumeration value="block"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Delimiter")
@XmlEnum
public enum Delimiter {

    @XmlEnumValue("inline")
    INLINE("inline"),
    @XmlEnumValue("block")
    BLOCK("block");
    private final String value;

    Delimiter(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Delimiter fromValue(String v) {
        for (Delimiter c: Delimiter.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
