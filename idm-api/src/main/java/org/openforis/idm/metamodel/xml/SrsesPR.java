package org.openforis.idm.metamodel.xml;

import org.openforis.idm.metamodel.LanguageSpecificText;
import org.openforis.idm.metamodel.SpatialReferenceSystem;
import org.openforis.idm.metamodel.Survey;

/**
 * @author G. Miceli
 */
class SrsesPR extends IdmlPullReader {
	
	public SrsesPR() {
		super("spatialReferenceSystems", 1);
		addChildPullReaders(new SrsPR());
	}

	private class SrsPR extends IdmlPullReader {

		private SpatialReferenceSystem srs;
		
		public SrsPR() {
			super("spatialReferenceSystem");
			addChildPullReaders(new LabelPR(), new DescriptionPR(), new WktPR());
		}
		
		@Override
		protected boolean onStartTag() throws XmlParseException {
			String id = getAttribute("srid", true);
			this.srs = new SpatialReferenceSystem();
			srs.setId(id);
			return false;
		}

		private class LabelPR extends LanguageSpecificTextPR {
			public LabelPR() {
				super("label");
			}
			
			@Override
			protected void processText(LanguageSpecificText lst) {
				srs.addLabel(lst);
			}
		}

		private class DescriptionPR extends LanguageSpecificTextPR {
			public DescriptionPR() {
				super("description");
			}
			
			@Override
			protected void processText(LanguageSpecificText lst) {
				srs.addDescription(lst);
			}
		}

		private class WktPR extends TextPullReader {
			public WktPR() {
				super("wkt", 1);
				setTrimWhitespace(false);
			}

			@Override
			protected void processText(String text) {
				srs.setWellKnownText(text);
			}
		}
	
		@Override
		protected void onEndTag() throws XmlParseException {
			Survey survey = getSurvey();
			survey.addSpatialReferenceSystem(srs);
			this.srs = null;
		}
	}
}