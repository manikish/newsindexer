package edu.buffalo.cse.irf14.analysis;

import java.text.NumberFormat;
import java.text.ParsePosition;

import com.sun.xml.internal.fastinfoset.util.StringArray;


public class DateTokenFilter extends TokenFilter {
	private TokenFilter nextFilter;
	private TokenStream myStream;
	private static final String MONTHS = "January|February|March|April|May|June|July|August|September|October|November|December" 
			 ;
	private static final String OTHER_PATTERNS = "BC|AD|UTC|AM|PM|";
	
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

	public DateTokenFilter(TokenStream stream) {
		// TODO Auto-generated constructor stub
		super(stream);
		myStream = stream;
	}
		
	@Override
	public void perform() {
		// TODO Auto-generated method stub
		myStream.reset();
		try {
			if(increment()) {
				Token myToken = myStream.next();
				String myTokenText = myToken.getTermText();
//code for the formats- 10 january 1990 && December 7, 1941 && April 11  
				if (MONTHS.contains(myTokenText)) {
					Character c = myToken.getTermBuffer()[0];
					String month = null,year,day;
					
					switch (c) {
					case 'F':
						month = "02";
						break;
					case 'S':
						month = "09";
						break;
					case 'O':
						month = "10";
						break;
					case 'N':
						month = "11";
						break;
					case 'D':
						month = "12";
						break;
					case 'A':
					{
						Character second = myToken.getTermBuffer()[1];
						int res = second.compareTo('p');
						if(res==0)
						{
							month = "04";
						}
						else if(res>0)
						{
							month = "08";
						}
					}
						break;
					case 'J':
					{
						Character fourth = myToken.getTermBuffer()[3];
						int res = fourth.compareTo('u');
						if(res == 0)
						{
							month = "01";
						}
						else if(res>0)
						{
							month = "07";
						}
						else
						{
							month = "06";
						}
					}
					case 'M':
					{
						Character third = myToken.getTermBuffer()[2];
						int res = third.compareTo('r');
					 	if(res == 0)
						{
							month = "03";
						}
						else 
						{
							month = "05";
						}
					}
					default: 
						month = "-1";
						break;
					}
					Token nextToken = myStream.next();
					String tokenText = nextToken.getTermText();
					
					NumberFormat formatter = NumberFormat.getInstance();
					  ParsePosition pos = new ParsePosition(0);
					 Number number = formatter.parse(tokenText, pos);
					
                    if(tokenText.length() == pos.getIndex())
                    {
                    	myStream.previous();
                    	myStream.previous();
                    	Token monthPreviousToken = myStream.previous();
                    	pos.setIndex(0);
                    	Number expectedDay = formatter.parse(monthPreviousToken.getTermText(),pos);
                    	if(expectedDay == null)
                    	{
                    		year = "1900";
                    		day  = number.toString(); 
                    		myStream.next();
                    		myStream.remove();
                    		myStream.next();
                    		myStream.remove();
                       	}
                    	else
                    	{
                    		year = getFormattedYear(number);
                    		day  = expectedDay.toString();
                    		myStream.remove();
                        	myStream.next();
                        	myStream.remove();
                        	myStream.next();
                        	myStream.remove();
                    	}
                    	
                    }
                    else
                    {
                    	day = number.toString();
                    	Token predictedYearToken = myStream.next();
                        pos.setIndex(0);
                    	Number expectedYear = formatter.parse(predictedYearToken.getTermText(),pos);
                        if(expectedYear != null)
                        {
                        	year = getFormattedYear(expectedYear);
                        	myStream.remove();
                        	myStream.previous();
                        	myStream.remove();
                        	myStream.previous();
                        	myStream.remove();
                        }
                        else
                        {
                        	year = "1900";
                        	myStream.previous();
                        	myStream.remove();
                        	myStream.previous();
                        	myStream.remove();
                        }
                        
                    }
                    
                    String dateString = year+month+day;
                    Token insertToken = new Token();
                    insertToken.setTermText(dateString);
                    myStream.insert(myStream.getNextIndex(), insertToken);
				}
				else 
				{
					//code for 2011-14 format
					String[] strings = myTokenText.split("-");
					try
					{
						int yearFrom = Integer.parseInt(strings[0]);
						int yearTo   = Integer.parseInt(strings[1]);
						
						int prefixYearTo = yearFrom/100;
						yearTo = prefixYearTo*100+yearTo;
						 
						String dateString = yearFrom+"0101-"+yearTo+"0101";
						myStream.remove();
						Token insertToken = new Token();
	                    insertToken.setTermText(dateString);
						myStream.insert(myStream.getNextIndex(), insertToken);
					}
					catch (NumberFormatException e)
					{
						throw e;
					}
					
				}
				
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getFormattedYear(Number yearInNumber)
	{
		String year;
		int yearInt = yearInNumber.intValue();
		if(yearInt < 1000 && yearInt > 99)
        {
        	year = "0"+yearInNumber.toString();
        }
        else if(yearInt < 100 && yearInt > 9)
        {
        	year = "00"+yearInNumber.toString();
        }
        else if(yearInt >= 0 && yearInt < 10)
        {
        	year = "000"+yearInNumber.toString();
        }
        else
        {
            year = yearInNumber.toString();	
        }
    	
		return year;
	}
	
	public static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
}
