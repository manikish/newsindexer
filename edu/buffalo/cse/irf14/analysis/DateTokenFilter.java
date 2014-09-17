package edu.buffalo.cse.irf14.analysis;


public class DateTokenFilter extends TokenFilter {
	private TokenFilter nextFilter;
	private TokenStream myStream;
	private static final String DATE_PATTERNS = "January|February|March|April|May|June|July|August|September|October|November|December" 
			 +"BC|AD|UTC|AM|PM|";
	
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
				if (DATE_PATTERNS.contains(myToken.getTermText())) {
					Character c = myToken.getTermBuffer()[0];
					String month,year,time;
										
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
						else
						{
							
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
//					if(nextToken.getTermText().is)
					{
						
					}
				}
				
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
