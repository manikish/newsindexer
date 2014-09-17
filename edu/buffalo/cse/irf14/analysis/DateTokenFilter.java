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
				  	
				}
				
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
