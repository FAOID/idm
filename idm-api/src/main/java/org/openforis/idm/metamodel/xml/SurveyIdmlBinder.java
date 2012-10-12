package org.openforis.idm.metamodel.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openforis.idm.metamodel.DefaultSurveyContext;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.SurveyContext;
import org.openforis.idm.metamodel.xml.internal.marshal.SurveyMarshaller;
import org.openforis.idm.metamodel.xml.internal.unmarshal.PlainTextApplicationOptionsBinder;
import org.openforis.idm.metamodel.xml.internal.unmarshal.SurveyUnmarshaller;

/**
 * Load a Survey object from IDML
 * 
 * @author G. Miceli
 */
public class SurveyIdmlBinder {
	private static final String UTF8_ENCODING = "UTF-8";
	
	private SurveyContext surveyContext;
	private List<ApplicationOptionsBinder<?>> optionsBinders;

	public SurveyIdmlBinder(SurveyContext surveyContext) {
		this.surveyContext = surveyContext;
		this.optionsBinders = new ArrayList<ApplicationOptionsBinder<?>>();
	}

	// TODO Remove
	public static void main(String[] args) {
		try {
			File f = new File("../idm-test/src/main/resources/test.idm.xml");
			InputStream is = new FileInputStream(f);
			SurveyContext ctx = new DefaultSurveyContext();
			SurveyIdmlBinder binder = new SurveyIdmlBinder(ctx);
			PlainTextApplicationOptionsBinder textAOB = new PlainTextApplicationOptionsBinder("ui");
			binder.addApplicationOptionsBinder(textAOB);
			
			Survey survey = binder.unmarshal(is);
			
			// Write
			binder.marshal(survey, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addApplicationOptionsBinder(ApplicationOptionsBinder<?> binder) {
		optionsBinders.add(binder);
	}

	/**
	 * 
	 * @param type
	 * @return the first binder which supports the specified type, or null
	 * if none found
	 */
	public ApplicationOptionsBinder<?> getApplicationOptionsBinder(String type) {
		for (ApplicationOptionsBinder<?> binder : optionsBinders) {
			if ( binder.isSupported(type) ) {
				return binder;
			}
		}
		return null;
	}
	
	public SurveyContext getSurveyContext() {
		return surveyContext;
	}
	
	public void marshal(Survey survey, OutputStream os) throws IOException {
		SurveyMarshaller ser = new SurveyMarshaller(this);
		ser.marshal(survey, os, UTF8_ENCODING);
	}
		
	public Survey unmarshal(InputStream is) throws XmlParseException, IOException {
		SurveyUnmarshaller unmarshaller = new SurveyUnmarshaller(this);
		unmarshaller.parse(is, UTF8_ENCODING);
		return unmarshaller.getSurvey();
	}	
}
