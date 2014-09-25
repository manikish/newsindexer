package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.text.Normalizer.Form;


public class AccentTokenFilter extends TokenFilter {

	private TokenFilter nextFilter;
	private Token myToken;
	private TokenStream myStream;
	private String myAccentList = "AAAAAAACEEEEIIIIDNOOOOO\u00d7\u00d8UUUUYIB"	// from unicode \u00c0 to \u00df values
			+ "aaaaaaaceeeeiiiidnooooo\u00f7\u00f8uuuuypy"	// from unicode \u00e0 to \u00ff values 
			+ "AaAaAaCcCcCcCcDdDdEeEeEeEeEeGgGgGgGgHhHhIiIiIiIiIiJjJjKkkLlLlLlLlLlNnNnNnnNnOoOo"	// from unicode \u0010 to \u014f
			+ "OoOoRrRrRrSsSsSsSsTtTtTtUuUuUuUuUuUuWwYyYZzZzZzf"; // from unicode \u0150 to \u017f values 
	
	public AccentTokenFilter(TokenStream stream) {
		super(stream);
		this.myStream = stream;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setNextFilter(TokenFilter nextFilter) {
		// TODO Auto-generated method stub
		this.nextFilter = nextFilter;
	}

	@Override
	public TokenFilter getNextFilter() {
		// TODO Auto-generated method stub
		return nextFilter;
	}

	@Override
	public void perform() {
		// TODO Auto-generated method stub
		while(myStream.hasNext()) {
			myToken = myStream.next();
			String text = myToken.getTermText();
			StringBuffer result = new StringBuffer("");
			text = Normalizer.normalize(text, Form.NFC);
			for(int i=0; i<text.length();i++) {
				char myChar = text.charAt(i);
				if(myChar >= '\u00c0' && myChar <= '\u017f') {
					//text = text.replace(myChar, myAccentList.charAt(myChar-'\u00c0'));
					result.append(myAccentList.charAt(myChar-'\u00c0'));
				}
				else if(myChar >= '\u00A1' && myChar <= '\u00BF') {
					//text = text.replace(myChar, '\u0000');
					//do nothing
				}
				else {
					result.append(myChar);
				}
			}
			myToken.setTermText(result.toString());
		}
	}

}
