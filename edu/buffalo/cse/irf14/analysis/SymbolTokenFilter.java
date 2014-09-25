package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymbolTokenFilter extends TokenFilter {
	
	private TokenFilter nextFilter;
	private TokenStream myStream;
	private Token myToken;
	private boolean numberInToken = false;
	private static final String[] QUOTE_LIST = {"'s","'ve","'re","won't","shan't","n't","'d","'m","'ll","'em"};
	private static final String[] QUOTE_SUB = {""," have"," are","will not","shall not"," not"," would"," am"," will","them"};
	public static Map<String, String> map = new HashMap<String, String>();
	
	public SymbolTokenFilter(TokenStream stream) {
		super(stream);
		myStream = stream;
		for(int i=0;i<QUOTE_LIST.length;i++)
			map.put(QUOTE_LIST[i], QUOTE_SUB[i]);
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
		Pattern pattern = Pattern.compile(".*[./!?]$"); //Pattern.compile("[\\p{Punct}]$");
		
		while(myStream.hasNext()) {
			myToken = myStream.next();
			String text = myToken.getTermText();
			if(text.matches(".*[']+[\\w]*") && !text.matches("[']?.*[']+")) {
				for(String symbol:QUOTE_LIST) {
					if(myToken.getTermText().contains(symbol))
						text=text.replace(symbol, map.get(symbol));
				}
			}
			text = text.replace("'", "");
			Matcher matcher = pattern.matcher(text);
			while(matcher.matches()) {
				text=text.substring(0, text.length()-1);
				matcher = pattern.matcher(text);
			}
			myToken.setTermText(text);
			
			//test for hyphen
			int index = text.indexOf("-");
			if(index!=-1) {
				String[] temp = text.split("-");
				for(String t:temp){
					if(numberInToken)
						break;
					else {
						if(t.matches(".*[0-9]+.*")) {
							numberInToken = true;	
						}
					}	
				}
				if(!numberInToken) {
					text = "";
					for(String s: temp) {
						if(!s.isEmpty())
							text = text+" "+s;
					}
					myToken.setTermText(text.trim());
					/*
					String temp2 = text.replaceAll("-", "");
					if(temp2.isEmpty())
						myStream.remove();
					else {
						text = text.replaceAll("-", " ");
						myToken.setTermText(text);
					}
					 */
				}
			}
		}
	}

}
