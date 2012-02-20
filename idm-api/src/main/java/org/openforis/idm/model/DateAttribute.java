package org.openforis.idm.model;

import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.validation.DateValidator;
import org.openforis.idm.metamodel.validation.ValidationResults;

/**
 * @author G. Miceli
 * @author M. Togna
 */
public class DateAttribute extends Attribute<DateAttributeDefinition, Date> {

	public DateAttribute(DateAttributeDefinition definition) {
		super(definition, 3);
	}

	@Override
	public Date createValue(String string) {
		return Date.parseDate(string);
	}
	
	@SuppressWarnings("unchecked")
	public Field<Integer> getYearField() {
		return (Field<Integer>) getField(0);
	}

	@SuppressWarnings("unchecked")
	public Field<Integer> getMonthField() {
		return (Field<Integer>) getField(1);
	}

	@SuppressWarnings("unchecked")
	public Field<Integer> getDayField() {
		return (Field<Integer>) getField(2);
	}

	@Override
	public Date getValue() {
		Integer year = getYearField().getValue();
		Integer month = getMonthField().getValue();
		Integer day = getDayField().getValue();
		return new Date(year, month, day);
	}

	@Override
	public void setValue(Date date) {
		Integer year = date.getYear();
		Integer month = date.getMonth();
		Integer day = date.getDay();
		getYearField().setValue(year);
		getMonthField().setValue(month);
		getDayField().setValue(day);
	}
	
	@Override
	protected boolean validateValue(ValidationResults results) {
		DateValidator validator = new DateValidator();
		boolean result = validator.validate(this);
		results.addResult(this, validator, result);
		return result;
	}
	
}
