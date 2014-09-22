package edu.buffalo.cse.irf14.analysis;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NumberTokenFilter extends TokenFilter {
	
	private TokenFilter nextFilter;
	private TokenStream myStream;
	private boolean isTokenToBeRemoved = false;
	private Calendar myCalendar = null;
	private Token myToken;
	
	public NumberTokenFilter(TokenStream stream) {
		super(stream);
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
		myStream = getStream();
		myStream.reset();
		try {
			while(increment()) {
				myToken = myStream.next();
				char text = myToken.getTermBuffer()[0];
				/*if(text.matches("[0-9]+[^0-9a-zA-Z]*[0-9]*[^0-9a-zA-Z]*[0-9]*")) {
					trimText(myToken);
				}*/
				if(text>=48&&text<=57)
					trimText(myToken.getTermText());
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void trimText(String dateTimeString) {
		// TODO Auto-generated method stub
		StringBuffer myNewText = new StringBuffer();
		char[] text = dateTimeString.toCharArray();
		try {
			@SuppressWarnings("unused")
			Integer myInt = Integer.parseInt(dateTimeString);
			if(dateTimeString.length()==8) {				
				if(dateTimeString.indexOf(":")!=-1) {
					checkForValidDateTime(dateTimeString, null);
				}
				else {
					String[] timeString = dateTimeString.split(":");
					if(timeString.length!=3) {
						isTokenToBeRemoved = true;
					}
					else {
						checkForValidDateTime(null,timeString);
					}
				}
			}
			else if(dateTimeString.indexOf("-")==-1){
				isTokenToBeRemoved = true;
			}
			else {
				String[] dateRange = dateTimeString.split("-");
				for(String s:dateRange)
					trimText(s.trim());
			}
			
		}
		catch(NumberFormatException e) {
			for(char c:text) {
				if(!(c>=48 && c<=57) && c!=',' && c!='.')
					myNewText.append(c);
			}
			myToken.setTermText(myNewText.toString());
		}
		if(isTokenToBeRemoved) {
			myStream.remove();
			isTokenToBeRemoved = false;
		}
			
	}

	private void checkForValidDateTime(String dateString, String[] timeString) {
		if(timeString==null) {
			myCalendar = new GregorianCalendar(
					Integer.parseInt(dateString.substring(0, 4)), 
					Integer.parseInt(dateString.substring(4, 6)), 
					Integer.parseInt(dateString.substring(6, 8)));
		}
		else {
			myCalendar = new GregorianCalendar(1900, 01, 01, 
					Integer.parseInt(timeString[0]),
					Integer.parseInt(timeString[1]),
					Integer.parseInt(timeString[2]));
		}
		myCalendar.setLenient(false);
		try {
			myCalendar.getTime();
			//do nothing as its a valid date/time and needs to be retained in the token stream.
		}
		catch(Exception e) {
			isTokenToBeRemoved = true;
		}
	}

}
