package org.openforis.idm.metamodel.xml.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field points to the object's parent in the object hierarchy
 * 
 * @author G. Miceli
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlInherited {
	/**
	 * @return the field in the XML parent to inherit
	 */
	String[] value();
}
