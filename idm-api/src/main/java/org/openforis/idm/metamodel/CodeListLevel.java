/**
 * 
 */
package org.openforis.idm.metamodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author G. Miceli
 * @author M. Togna
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "labels", "descriptions" })
public class CodeListLevel implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "label", type = LanguageSpecificText.class)
	private List<LanguageSpecificText> labels;

	@XmlElement(name = "description", type = LanguageSpecificText.class)
	private List<LanguageSpecificText> descriptions;

	public List<LanguageSpecificText> getLabels() {
		if(this.labels != null) {
			return Collections.unmodifiableList(this.labels);
		} else {
			return null;
		}
	}

	public List<LanguageSpecificText> getDescriptions() {
		if(this.descriptions != null) {
			return Collections.unmodifiableList(this.descriptions);
		} else {
			return null;
		}
	}

	public String getName() {
		return this.name;
	}
}
