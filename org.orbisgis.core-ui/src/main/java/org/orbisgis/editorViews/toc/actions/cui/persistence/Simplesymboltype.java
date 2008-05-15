//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.05.15 at 03:42:11 PM CEST 
//


package org.orbisgis.editorViews.toc.actions.cui.persistence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for simplesymboltype complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="simplesymboltype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="fill-color" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fill-pattern" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="geometry-type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="line-color" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outline-pattern" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outline-size" type="{http://www.w3.org/2001/XMLSchema}string" default="1.0" />
 *       &lt;attribute name="simbol-size" type="{http://www.w3.org/2001/XMLSchema}string" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simplesymboltype")
public class Simplesymboltype {

    @XmlAttribute(name = "fill-color")
    protected String fillColor;
    @XmlAttribute(name = "fill-pattern")
    protected String fillPattern;
    @XmlAttribute(name = "geometry-type", required = true)
    protected String geometryType;
    @XmlAttribute(name = "line-color")
    protected String lineColor;
    @XmlAttribute(name = "outline-pattern")
    protected String outlinePattern;
    @XmlAttribute(name = "outline-size")
    protected String outlineSize;
    @XmlAttribute(name = "simbol-size")
    protected String simbolSize;

    /**
     * Gets the value of the fillColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFillColor() {
        return fillColor;
    }

    /**
     * Sets the value of the fillColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFillColor(String value) {
        this.fillColor = value;
    }

    /**
     * Gets the value of the fillPattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFillPattern() {
        return fillPattern;
    }

    /**
     * Sets the value of the fillPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFillPattern(String value) {
        this.fillPattern = value;
    }

    /**
     * Gets the value of the geometryType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeometryType() {
        return geometryType;
    }

    /**
     * Sets the value of the geometryType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeometryType(String value) {
        this.geometryType = value;
    }

    /**
     * Gets the value of the lineColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineColor() {
        return lineColor;
    }

    /**
     * Sets the value of the lineColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineColor(String value) {
        this.lineColor = value;
    }

    /**
     * Gets the value of the outlinePattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutlinePattern() {
        return outlinePattern;
    }

    /**
     * Sets the value of the outlinePattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutlinePattern(String value) {
        this.outlinePattern = value;
    }

    /**
     * Gets the value of the outlineSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutlineSize() {
        if (outlineSize == null) {
            return "1.0";
        } else {
            return outlineSize;
        }
    }

    /**
     * Sets the value of the outlineSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutlineSize(String value) {
        this.outlineSize = value;
    }

    /**
     * Gets the value of the simbolSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSimbolSize() {
        if (simbolSize == null) {
            return "1";
        } else {
            return simbolSize;
        }
    }

    /**
     * Sets the value of the simbolSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSimbolSize(String value) {
        this.simbolSize = value;
    }

}
