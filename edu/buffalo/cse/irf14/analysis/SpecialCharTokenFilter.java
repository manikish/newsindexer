package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class SpecialCharTokenFilter extends TokenFilter {
	private TokenFilter nextFilter;
	private TokenStream myStream;
	
	public SpecialCharTokenFilter(TokenStream stream) {
		super(stream);
		myStream = stream;
		perform();
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
	
	public void perform() {
		myStream.reset();
		
			while(myStream.hasNext()) {
				Token myToken = myStream.next();
				String REGEX = "[^\\w[-_]\\s\\@\\.\\-]";
	            Pattern pattern = Pattern.compile(REGEX);
	            String preFilteredText = pattern.matcher(myToken.getTermText()).replaceAll("");
	            if(preFilteredText.length() == 0)
	            {
	            	myStream.remove();
	            }
	            else 
	            {
	            	String[] components = preFilteredText.split("@");
	            	if(components.length == 1)
	            	{
	            		REGEX = "[a-z|A-Z]-";
                        pattern = Pattern.compile(REGEX);
                        Matcher m = pattern.matcher(preFilteredText);
                        if(m.find())
                        {
                        	preFilteredText=preFilteredText.replaceAll("-", "");
                        }
	            		
	            		Token filteredToken = new Token(); 
	            		filteredToken.setTermText(preFilteredText);
	            		myStream.remove();
	            		myStream.insert(myStream.getNextIndex(), filteredToken);
	               	}
	            	else
	            	{
	            		Token filteredTokenOne = new Token(); 
	            		filteredTokenOne.setTermText(components[0]);
	            		myStream.remove();
	            		myStream.insert(myStream.getNextIndex(), filteredTokenOne);
	            		Token filteredTokenTwo = new Token();
	            		filteredTokenTwo.setTermText(components[1]);
	            		myStream.insert(myStream.getNextIndex(), filteredTokenTwo);
	            	}
	            }
			}
		
		
	}
}
