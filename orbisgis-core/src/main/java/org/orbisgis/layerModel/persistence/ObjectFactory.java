//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.07.03 at 10:20:47 AM CEST 
//


package org.orbisgis.layerModel.persistence;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.orbisgis.layerModel.persistence package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LayerCollection_QNAME = new QName("", "layer-collection");
    private final static QName _Layer_QNAME = new QName("", "layer");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.orbisgis.layerModel.persistence
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SelectedLayer }
     * 
     */
    public SelectedLayer createSelectedLayer() {
        return new SelectedLayer();
    }

    /**
     * Create an instance of {@link Legends }
     * 
     */
    public Legends createLegends() {
        return new Legends();
    }

    /**
     * Create an instance of {@link MapContext }
     * 
     */
    public MapContext createMapContext() {
        return new MapContext();
    }

    /**
     * Create an instance of {@link SingleLayerType }
     * 
     */
    public SingleLayerType createSingleLayerType() {
        return new SingleLayerType();
    }

    /**
     * Create an instance of {@link SimpleLegend }
     * 
     */
    public SimpleLegend createSimpleLegend() {
        return new SimpleLegend();
    }

    /**
     * Create an instance of {@link LayerType }
     * 
     */
    public LayerType createLayerType() {
        return new LayerType();
    }

    /**
     * Create an instance of {@link LayerCollectionType }
     * 
     */
    public LayerCollectionType createLayerCollectionType() {
        return new LayerCollectionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LayerCollectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "layer-collection")
    public JAXBElement<LayerCollectionType> createLayerCollection(LayerCollectionType value) {
        return new JAXBElement<LayerCollectionType>(_LayerCollection_QNAME, LayerCollectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SingleLayerType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "layer")
    public JAXBElement<SingleLayerType> createLayer(SingleLayerType value) {
        return new JAXBElement<SingleLayerType>(_Layer_QNAME, SingleLayerType.class, null, value);
    }

}
