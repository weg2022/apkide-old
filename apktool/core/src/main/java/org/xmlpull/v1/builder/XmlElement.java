package org.xmlpull.v1.builder;

import java.util.Iterator;

//setText() instead of replaceChildrenWithText
//setAttribute*() instead of addAtrribute*()
//TODO add method attributeValue( "id" );
//TODO add method requiredAttribute( "id" );
//TODO add method requiredAttributeValue( "id" );
//TODO setAttributeValue(key, value) //create attribute if necessary
//TODO setAttributeValue(attribute) //replace exisiting attribute if necessary
//TODO add XmlElement requiredFirstElement() in Adapter?
//TODO add XmlElement requiredOneElement() in AdpateR?

/**
 * Represents
 * <a href="http://www.w3.org/TR/xml-infoset/#infoitem.element">Element Information Item</a>
 * except for in-scope namespaces that can be reconstructed by visiting this element parent,
 * checking its namespaces, then grandparent and so on. For convenience there are
 * methods to resolve namespace prefix for given namespace name.
 * <p>
 * <br />NOTE: this representaiton is optimized for streaming - iterator approach that
 * allows gradual visiting of nodes is preferred over indexed access.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @version $Revision: 1.25 $
 */
public interface XmlElement extends XmlContainer, XmlContained, Cloneable {
    String NO_NAMESPACE = "";
    
    //JDK15 covariant public XmlElement clone() throws CloneNotSupportedException
    
    /**
     * Method clone
     *
     * @return an Object
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;
    
    
    //----------------------------------------------------------------------------------------------
    // Element properties
    
    /**
     * XML Infoset [base URI] property
     *
     * @return a String
     */
    String getBaseUri();
    
    /**
     * XML Infoset [base URI] property
     *
     * @param baseUri a  String
     */
    void setBaseUri(String baseUri);
    
    /**
     * Get top most container that is either XmlDocument or XmlElement (may be event this element!!!)
     */
    XmlContainer getRoot();
    
    /**
     * XML Infoset [parent] property.
     * If current element is not child of containing parent XmlElement or XmlDocument
     * then builder exception will be thrown
     */
    XmlContainer getParent();
    
    /**
     * Method setParent
     *
     * @param parent a  XmlContainer
     */
    void setParent(XmlContainer parent);
    
    /**
     * Return namespace of current element
     * (XML Infoset [namespace name] and [prefix] properties combined)
     * null is only returned if
     * element was created without namespace
     */
    XmlNamespace getNamespace();
    
    /**
     * Return namespace name (XML Infoset [namespace name]property
     * or null if element has no namespace
     */
    String getNamespaceName();
    
    /**
     * Set namespace ot use for theis element.
     * Note: namespace prefix is <b>always</b> ignored.
     */
    void setNamespace(XmlNamespace namespace);
    
    //    public String getPrefix();
    //    public void setPrefix(String prefix);
    
    /**
     * XML Infoset [local name] property.
     *
     * @return a String
     */
    String getName();
    
    /**
     * XML Infoset [local name] property.
     *
     * @param name a  String
     */
    void setName(String name);
    
    
    //----------------------------------------------------------------------------------------------
    // Attributes management
    
    
    //JDK15 Iterable
    
    /**
     * Return Iterator<XmlAttribute> - null is never returned if there is no children
     * then iteraotr over empty collection is returned
     */
    Iterator attributes();
    
    /**
     * Add attribute (adds it to the XML Infoset [namespace attributes] set)
     * Attribute mist
     *
     * @param attributeValueToAdd a  XmlAttribute
     * @return a XmlAttribute
     */
    XmlAttribute addAttribute(XmlAttribute attributeValueToAdd);
    
    /**
     * addAttribute
     *
     * @param name  a  String
     * @param value a  String
     * @return a XmlAttribute
     */
    XmlAttribute addAttribute(String name, String value);
    
    /**
     * Method addAttribute
     *
     * @param namespace a  XmlNamespace
     * @param name      a  String
     * @param value     a  String
     * @return a XmlAttribute
     */
    XmlAttribute addAttribute(XmlNamespace namespace, String name, String value);
    
    /**
     * Method addAttribute
     *
     * @param type      a  String
     * @param namespace a  XmlNamespace
     * @param name      a  String
     * @param value     a  String
     * @return a XmlAttribute
     */
    XmlAttribute addAttribute(String type, XmlNamespace namespace,
                                     String name, String value);
    
    /**
     * Method addAttribute
     *
     * @param type      a  String
     * @param namespace a  XmlNamespace
     * @param name      a  String
     * @param value     a  String
     * @param specified a  boolean
     * @return a XmlAttribute
     */
    XmlAttribute addAttribute(String type, XmlNamespace namespace,
                                     String name, String value, boolean specified);
    
    /**
     * Method addAttribute
     *
     * @param attributeType      a  String
     * @param attributePrefix    a  String
     * @param attributeNamespace a  String
     * @param attributeName      a  String
     * @param attributeValue     a  String
     * @param specified          a  boolean
     * @return a XmlAttribute
     */
    XmlAttribute addAttribute(String attributeType,
                                     String attributePrefix,
                                     String attributeNamespace,
                                     String attributeName,
                                     String attributeValue,
                                     boolean specified);
    
    /**
     * Method ensureAttributeCapacity
     *
     * @param minCapacity an int
     */
    void ensureAttributeCapacity(int minCapacity);
    
    //TODO add attributeValue(name)
    //TODO add attributeValue(XmlNamespace, name)
    
    /**
     * Method getAttributeValue
     *
     * @param attributeNamespaceName String
     * @param attributeName           a  String
     * @return a String
     */
    String getAttributeValue(String attributeNamespaceName,
                                    String attributeName);
    
    /**
     * Find attribute that matches given name or namespace
     * Returns null if not found.
     * Will match only attribute that have no namesapce.
     */
    XmlAttribute attribute(String attributeName);
    
    /**
     * Find attribute that matches given name or namespace
     * Returns null if not found.
     * NOTE: if namespace is null in this case it will match only
     * attributes that have no namespace.
     */
    XmlAttribute attribute(XmlNamespace attributeNamespaceName,
                                  String attributeName);
    
    /**
     * Find attribute that matches given name or namespace
     * Returns null if not found.
     * NOTE: if namespace is null in this case it will match only
     * attributes that has no namespace.
     *
     * @deprecated Use attribute()
     */
    @Deprecated
    XmlAttribute findAttribute(String attributeNamespaceName,
                               String attributeName);
    
    
    /**
     * Method hasAttributes
     *
     * @return a boolean
     */
    boolean hasAttributes();
    
    /**
     * Method removeAttribute
     *
     * @param attr a  XmlAttribute
     */
    void removeAttribute(XmlAttribute attr);
    
    /**
     * Method removeAllAttributes
     */
    void removeAllAttributes();
    
    
    //----------------------------------------------------------------------------------------------
    // Namespaces management
    
    //JDK15 Iterable
    
    /**
     * Return Iterator<XmlNamespace> - null is never returned if there is no children
     * then iteraotr over empty collection is returned
     */
    Iterator namespaces();
    
    /**
     * Create new namespace with prefix and namespace name (both must be not null)
     * and add it to current element.
     */
    XmlNamespace declareNamespace(String prefix, String namespaceName);
    
    /**
     * Add namespace to current element (both prefix and namespace name must be not null)
     */
    XmlNamespace declareNamespace(XmlNamespace namespace);
    
    /**
     * Method ensureNamespaceDeclarationsCapacity
     *
     * @param minCapacity an int
     */
    void ensureNamespaceDeclarationsCapacity(int minCapacity);
    
    /**
     * Method hasNamespaceDeclarations
     *
     * @return a boolean
     */
    boolean hasNamespaceDeclarations();
    
    /**
     * Find namespace (will have non empty prefix) corresponding to namespace prefix
     * checking first current elemen and if not found continue in parent (if element has parent)
     * and so on.
     */
    XmlNamespace lookupNamespaceByPrefix(String namespacePrefix);
    
    /**
     * Find namespace (will have non empty prefix) corresponding to namespace name
     * checking first current elemen and if not found continue in parent (if element has parent).
     * and so on.
     */
    XmlNamespace lookupNamespaceByName(String namespaceName);
    
    /**
     * Create new namespace with null prefix (namespace name must be not null).
     */
    XmlNamespace newNamespace(String namespaceName);
    
    /**
     * Create new namespace with prefix and namespace name (both must be not null).
     */
    XmlNamespace newNamespace(String prefix, String namespaceName);
    
    /**
     * Method removeAllNamespaceDeclarations
     */
    void removeAllNamespaceDeclarations();
    
    
    //----------------------------------------------------------------------------------------------
    // Children management (element content)
    
    //JDK15 Iterable
    
    /**
     * Return Iterator<Object>  - null is never returned if there is no children
     * then iteraotr over empty collection is returned
     */
    Iterator children();
    
    /**
     * NOTE: =child added is _not_ checked if it XmlContainer, caller must manually fix
     * parent in child by calling setParent() !!!!
     */
    void addChild(Object child);
    
    /**
     * Method addChild
     *
     * @param pos   an int (starting from 0)
     * @param child an Object
     */
    void addChild(int pos, Object child);
    
    /**
     * NOTE: the child element must unattached to be added
     * (it is atttached if it is XmlContainer of recognized type and getParent() != null)
     */
    XmlElement addElement(XmlElement el);
    
    /**
     * Method addElement
     *
     * @param pos   an int (starting from 0)
     * @param child a  XmlElement
     * @return a XmlElement
     */
    XmlElement addElement(int pos, XmlElement child);
    
    /**
     * Method addElement
     *
     * @param name a  String
     * @return a XmlElement
     */
    XmlElement addElement(String name);
    
    /**
     * Method addElement
     *
     * @param namespace a  XmlNamespace
     * @param name      a  String
     * @return a XmlElement
     */
    XmlElement addElement(XmlNamespace namespace, String name);
    
    /**
     * Method hasChildren
     *
     * @return a boolean
     */
    boolean hasChildren();
    
    /**
     * Method hasChild
     *
     * @param child an Object
     * @return a boolean
     */
    boolean hasChild(Object child);
    
    /**
     * Method ensureChildrenCapacity
     *
     * @param minCapacity an int
     */
    void ensureChildrenCapacity(int minCapacity);
    
    /**
     * @deprecated see element()
     */
    @Deprecated
    XmlElement findElementByName(String name);
    
    /**
     * @deprecated see element()
     */
    @Deprecated
    XmlElement findElementByName(String namespaceName, String name);
    
    /**
     * @deprecated see elements()
     */
    @Deprecated
    XmlElement findElementByName(String name,
                                        XmlElement elementToStartLooking);
    
    /**
     * @deprecated see elements()
     */
    @Deprecated
    XmlElement findElementByName(String namespaceName, String name,
                                        XmlElement elementToStartLooking);
    
    /**
     * return element at poition (0..count-1) or IndexOutOfBoundsException if positon incorrect
     */
    XmlElement element(int position);
    
    //int count()
    //int countElement()
    //XmlElement element(String name) //return first element matching, null if not found!
    
    /**
     * call element(n, name) and if null was returnedthrow XmlBuilderException
     */
    XmlElement requiredElement(XmlNamespace n, String name) throws XmlBuilderException;
    
    /**
     * find first element with name and namespace (if namespace is null it is ignored in search)
     */
    XmlElement element(XmlNamespace n, String name);
    
    /**
     * find first element with name and namespace (if namespace is null it is ignored in search)
     * if no matching element is found then new element is created, appended to children, and returned
     */
    XmlElement element(XmlNamespace n, String name, boolean create);
    //Iterable elements(String name);
    //Iterable elements(XmlNamespace n, String name);
    
    /**
     * Return all elements that has namespace and name (null is never returned but empty iteraotr)
     */
    Iterable elements(XmlNamespace n, String name);
    
    
    void insertChild(int pos, Object childToInsert);
    
    /**
     * Create unattached element
     */
    XmlElement newElement(String name);
    
    /**
     * Method newElement
     *
     * @param namespace a  XmlNamespace
     * @param name      a  String
     * @return a XmlElement
     */
    XmlElement newElement(XmlNamespace namespace, String name);
    
    /**
     * Method newElement
     *
     * @param namespaceName a  String
     * @param name          a  String
     * @return a XmlElement
     */
    XmlElement newElement(String namespaceName, String name);
    
    /**
     * Removes all children - every child that was
     * implementing XmlNode will have set parent to null.
     */
    void removeAllChildren();
    
    /**
     * Method removeChild
     *
     * @param child an Object
     */
    void removeChild(Object child);
    
    /**
     * Method replaceChild
     *
     * @param newChild an Object
     * @param oldChild an Object
     */
    void replaceChild(Object newChild, Object oldChild);
    
    //public void remove(int pos);
    //public void set(int index, Object child);
    
    //----------------------------------------------------------------------------------------------
    // Utility methods to make manipulating Infoset easier for typical use cases
    //JDK15 Iterable
    
    /**
     * Return Iterator<XmlElement>  -  that represents all XmlElement content.
     * When used exception will be thrown if non white space children are found
     * (as expected no mixed content!).
     */
    Iterable requiredElementContent();
    
    /**
     * return children content as text - if there are any no text children throw exception
     */
    String requiredTextContent();
    
    /**
     * Remove all children and then add this text as only child.
     */
    void replaceChildrenWithText(String textContent);
    
    //public Iterable elementsContent();
    //public Iterable elementsContent(String name);
    //public Iterable elementsContent(XmlNamespace n String name);
    
    //String text() //children must map to text only nodes!!!
    //public String requiredTextContent();
    
    
    //selectNodes(String xpath)
    
    //public XmlNamespace getNamespacePrefix(String namespaceName, boolean generate)
    //public XmlNamespace findNamespace(String prefix, String namespace)
    
    /** it may need to reconsruct whole subtree to get count ... */
    //public int getChildrenCount();
    
    //public Object getFirstChild() throws XmlPullParserException;
    //public Object getNextSibling() throws XmlPullParserException;
    
    //public Object getChildByName(String namespace, String name);
    
}

