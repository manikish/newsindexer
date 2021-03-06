package edu.buffalo.cse.irf14.analysis;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateTokenFilter extends TokenFilter {
	private TokenFilter nextFilter;
	private TokenStream myStream;


	private static final String[] months = {"January","February","March","April","May","June","July"
		,"August","September","October","November","December"};
	private static final String[] monthsInShort = {"Jan","Feb","Mar","Apr","May","Jun","Jul"
		,"Aug","Sep","Oct","Nov","Dec"};

	private static final String[] OTHER_PATTERNS = {"BC","BC.","AD","AD.","AM","AM.","PM","PM."};
	
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
		
			while(myStream.hasNext()) {
				Token myToken = myStream.next();
				String myTokenText = myToken.getTermText();
				
				NumberFormat formatter = NumberFormat.getInstance();
				ParsePosition pos = new ParsePosition(0);
				  
				boolean isMonthMatching = false;
				String month = "01",suffix = "";
				if(myTokenText.length() > 3 && myTokenText.length() < 10)
				{
					for (int i = 0; i < months.length; i++) {
	                    if (myTokenText.equalsIgnoreCase(months[i])) {
	                    	Integer m = i+1;
							month = m.toString();
	                    	if(m < 10)
	                        month = "0"+month;
							isMonthMatching = true;
							break;
						}
					}
				}
				if(!isMonthMatching)
				{
					for (int i = 0; i < monthsInShort.length; i++) {
	                    if (myTokenText.equalsIgnoreCase(months[i])) {
	                    	Integer m = i+1;
							month = m.toString();
	                    	if(m < 10)
	                        month = "0"+month;
							isMonthMatching = true;
							break;
						}
					}
				}
				if (isMonthMatching) {
					handleMonthPatterns(month);
				}
				else if(myTokenText.length() == 2 || myTokenText.length() == 3)
				{
						int patternIndex = -1;
						for (int i = 0; i < OTHER_PATTERNS.length; i++) {
		                    if (myTokenText.equalsIgnoreCase(OTHER_PATTERNS[i])) {
		                    	patternIndex = i;
	                            break;
		                    }
						}
						if(patternIndex>=0 && patternIndex <=3)
						{
				     		handleBCADYearFormats(patternIndex);	
						}
						else if(patternIndex != -1)
						{
			                handleAMPMTimeFormats(patternIndex);
						}
				}
				handleMiscAMPMTimeFormats(myTokenText);
				
					pos.setIndex(0);
                    Number yearNumber = formatter.parse(myTokenText, pos);
                    int yearPredicted = -1;
                    if(yearNumber != null)
                    {
                    	if(myTokenText.length() == pos.getIndex())
                    	{
                        	yearPredicted = yearNumber.intValue();
                    	}
                    	 else
                         {
                         	handleMiscBCADFormats(myTokenText);
                         }
                    }
                   
                    if(yearPredicted > 999 && yearPredicted < 9999)
                    {
                        myStream.remove();
                        String dateString = "1900";
                    	if(myTokenText.length() != 4)
                    	{
                    		suffix = myTokenText.substring(4);
                    	}
                    	
                		dateString = yearPredicted+"0101"+suffix;
                       Token insertToken = new Token();
	                    insertToken.setTermText(dateString);
						myStream.insert(myStream.getNextIndex(), insertToken);
                    }
                    else
                    {
                    	//code for 2011-14 format
    					String[] strings = myTokenText.split("-");
    					if(strings.length == 2 && strings[0].length()!=0 && isNumeric(strings[0]) && isNumeric(strings[1]))
    					{
    						try
    						{
    	                    	pos.setIndex(0);
    							int yearFrom = Integer.parseInt(strings[0]);
    	                    	int yearTo = formatter.parse(strings[1],pos).intValue();
    							
    	                    	if(yearTo<99 && strings[1].length()>2)
    	                    	{
    	                    		suffix = strings[1].substring(2);
    	                    	}
    							int prefixYearTo = yearFrom/100;
    							yearTo = prefixYearTo*100+yearTo;
    							 
    							String dateString = yearFrom+"0101-"+yearTo+"0101"+suffix;
    							myStream.remove();
    							Token insertToken = new Token();
    		                    insertToken.setTermText(dateString);
    							myStream.insert(myStream.getNextIndex(), insertToken);
    						}
    						catch (NumberFormatException e)
    						{
    							
    						}
    						
    					}
                    }
				
				}
			 
	}
	
	public void handleMonthPatterns(String month)
	{
		String year = "1900",day = "01",suffix = "";

		NumberFormat formatter = NumberFormat.getInstance();
		  ParsePosition pos = new ParsePosition(0);
		  
		Token nextToken = myStream.next();
		String tokenText = nextToken==null?null:nextToken.getTermText();
		
		
		 Number number = tokenText!=null?formatter.parse(tokenText, pos):null;
		if(number!= null)
		{
			if(tokenText.length() == pos.getIndex())
	        {
	        	myStream.previous();
	        	myStream.previous();
	        	Token monthPreviousToken = myStream.previous();
	        	pos.setIndex(0);
	        	Number expectedDay = null;
	        	if(monthPreviousToken!=null)
	        	expectedDay = formatter.parse(monthPreviousToken.getTermText(),pos);
	        	if(expectedDay == null)
	        	{
	        		year = "1900";
	        		day  = getFormattedDay(number); 
	        		myStream.next();
	        		myStream.remove();
	        		myStream.next();  
	        		myStream.remove();
	           	}
	        	else
	        	{
	        		year = getFormattedYear(number);
	        		day  = getFormattedDay(expectedDay);
	        		myStream.remove();
	            	myStream.next();
	            	myStream.remove();
	            	myStream.next();
	            	myStream.remove();
	        	}
	        	
	        }
	        else
	        {
	        	day = getFormattedDay(number);
	        	Token predictedYearToken = myStream.next();
	        	if(predictedYearToken == null)
	        	{
	        		return;
	        	}
	        	String predictedYearTokenText = predictedYearToken.getTermText();
	            pos.setIndex(0);
	        	Number expectedYear = formatter.parse(predictedYearToken.getTermText(),pos);
	            if(expectedYear != null)
	            {
	            	year = getFormattedYear(expectedYear);
	                int lengthDiff = predictedYearTokenText.length() - pos.getIndex();
	            	if(lengthDiff != 0)
	            	{
	            		suffix = predictedYearTokenText.substring(pos.getIndex(), predictedYearTokenText.length());
	            	}
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
	        
	        String dateString = year+month+day+suffix;
	        Token insertToken = new Token();
	        insertToken.setTermText(dateString);
	        myStream.insert(myStream.getNextIndex(), insertToken);
		}
        
	}
	
	public void handleMiscAMPMTimeFormats(String myTokenText)
	{
		String REGEX = "[0-2]?\\d:[0-5]?\\d[AP]M";
		Pattern r = Pattern.compile(REGEX);
        Matcher m = r.matcher(myTokenText);
        if (m.find())
        {
        	NumberFormat formatter = NumberFormat.getInstance();
  		  ParsePosition pos = new ParsePosition(0);
  		  
        	String[] components = myTokenText.split(":");
        	Number minutesNumber = formatter.parse(components[1],pos);
        	String timeFormat = components[1].substring(pos.getIndex());
        	
        	String[] timeFormats = {"AM","AM.","PM","PM."};
        	
        	int patternIndex = -1;
			for (int i = 0; i < timeFormats.length; i++) {
                if (timeFormat.equalsIgnoreCase(timeFormats[i])) {
                	patternIndex = i;
                    break;
                }
			}
			
			String modifiedString = null;
			myStream.remove();
			String hours = components[0], minutes = minutesNumber.toString();
			if(components[0].length() < 2)
			{
				hours = "0"+components[0];
				if(patternIndex == 2 || patternIndex == 3)
				{
					int hoursInt = Integer.parseInt(hours);
					if(hoursInt != 12)
					{
						hoursInt = hoursInt+12;
						hours = ""+hoursInt;
					}
					
				}
			}
			if(minutesNumber.intValue() < 10)
			{
				minutes = "0"+minutes;
			}
			
			switch(patternIndex)
			{
			case 0:
			case 2:
				modifiedString = hours+":"+minutes+":00";
			    break;
			case 1:
			case 3:
				modifiedString = hours+":"+minutes+":00"+".";
				break;
			}
			Token insertToken = new Token();
            insertToken.setTermText(modifiedString);
			myStream.insert(myStream.getNextIndex(), insertToken);
        }
	}
	
	public void handleAMPMTimeFormats(int patternIndex)
	{
		myStream.previous();
			Token predictedTimeToken = myStream.previous();
			String predictedTimeTokenText = predictedTimeToken.getTermText();
			String REGEX = "[0-2]?\\d:\\d{2}";
        Pattern r = Pattern.compile(REGEX);
        Matcher m = r.matcher(predictedTimeTokenText);
        if (m.find( )) {
        	myStream.remove();
        	myStream.next();
        	myStream.remove();
        	
          String match = m.group();
          if(match.length() == predictedTimeTokenText.length())
          {
        	  String modifiedString = null;
        	  switch(patternIndex)
        	  {
        	  case 4:
        		  modifiedString = getFormattedAMTime(predictedTimeTokenText)+":00";
        		  break;
        	  case 5:
        		  modifiedString = getFormattedAMTime(predictedTimeTokenText)+":00.";
        		  break;
        	  case 6:
        		  modifiedString = getFormattedPMTime(predictedTimeTokenText)+":00";
        		  break;
        	  case 7:
        		  modifiedString = getFormattedPMTime(predictedTimeTokenText)+":00.";
        		  break;

        	  }
        	          	
        	  Token insertToken = new Token();
                 insertToken.setTermText(modifiedString);
					myStream.insert(myStream.getNextIndex(), insertToken);
          }
        }
	}
	
	public void handleMiscBCADFormats(String myTokenText)
	{
		String REGEX = "^\\d*[AB][DC]";

		Pattern r = Pattern.compile(REGEX);
        Matcher m = r.matcher(myTokenText);
        if(m.find())
        {
        	myStream.remove();
        	NumberFormat formatter = NumberFormat.getInstance();
  		    ParsePosition pos = new ParsePosition(0);
            Number yearNumber = formatter.parse(myTokenText, pos);
            
            String[] yearFormats = {"AD","AD.","BC","BC."};
        	String yearFormat = myTokenText.substring(pos.getIndex());
            
        	int patternIndex = -1;
			for (int i = 0; i < yearFormats.length; i++) {
                if (yearFormat.equalsIgnoreCase(yearFormats[i])) {
                	patternIndex = i;
                    break;
                }
			}
			
			String year = getFormattedYear(yearNumber);
			String modifiedString = "";
			switch(patternIndex)
			{
			case 0: 
				modifiedString = year+"0101";
				break;
			case 1:
				modifiedString = year+"0101.";
				break;
			case 2:
				modifiedString = "-"+year+"0101";
				break;
			case 3:
				modifiedString = "-"+year+"0101.";
                break;
			}
			Token insertToken = new Token();
            insertToken.setTermText(modifiedString);
			 myStream.insert(myStream.getNextIndex(), insertToken);
        }
	}
	
	public void handleBCADYearFormats(int patternIndex)
	{
		NumberFormat formatter = NumberFormat.getInstance();
		  ParsePosition pos = new ParsePosition(0);
		myStream.previous();
			Token predictedYearToken = myStream.previous();
			String predictedYearTokenText = predictedYearToken.getTermText();
        pos.setIndex(0);
    	Number expectedYear = formatter.parse(predictedYearTokenText,pos);
        if(expectedYear != null && predictedYearTokenText.length() == pos.getIndex())
        {
        	myStream.remove();
        	myStream.next();
        	myStream.remove();
        	
        	String dateString = null;
        	
        	switch(patternIndex)
        	{
        	case 0:
            	dateString = "-"+getFormattedYear(expectedYear)+"0101";
            	break;
        	case 1:
            	dateString = "-"+getFormattedYear(expectedYear)+"0101.";
            	break;
        	case 2:
            	dateString = getFormattedYear(expectedYear)+"0101";
            	break;
        	case 3:
            	dateString = getFormattedYear(expectedYear)+"0101.";
            	break;
        	}
        	
             Token insertToken = new Token();
             insertToken.setTermText(dateString);
			 myStream.insert(myStream.getNextIndex(), insertToken);
        }
        else
        {
        	myStream.next();
        	myStream.next();
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
	
	public static String getFormattedDay(Number dayInNumber)
	{
		String day;
		int dayInt = dayInNumber.intValue();
		if(dayInt<=9)
		{
			day = "0"+dayInNumber.toString();
		}
		else
		{
			day = dayInNumber.toString();
		}
		return day;
	}
	
	public static String getFormattedAMTime(String givenTime)
	{
	    String[] timeComponents = givenTime.split(":");
	    String prefix = "";
	    if(timeComponents[0].length() !=2 )
	       prefix = "0";
	    return prefix+givenTime;
	}
	
	public static String getFormattedPMTime(String givenTime)
	{
	    String[] timeComponents = givenTime.split(":");
	    String hourComponent = timeComponents[0];
	    int hourComponentValue = Integer.parseInt(hourComponent);
	    if(hourComponentValue < 12 )
	       hourComponentValue = hourComponentValue+12;
	    	
	    return hourComponentValue+":"+timeComponents[1];
	}
	
	public static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
}
