package edu.buffalo.cse.irf14.analysis;

public class StopwordTokenFilter extends TokenFilter {
	
	public static final String STOPWORD_LIST = "a|above|about|after|again|against|all|am|an|and|any|are|aren't|aint|ain't|as|at|be|because|been|before|being|below|"
			+ "between|both|but|by|can't|cannot|could|couldn't|did|didn't|do|does|doesn't|doing|don't|down|during|each|few|for|from|further|had|hadn't|has|"
			+ "hasn't|have|haven't|having|he|he'd|he'll|he's|her|here|here's|her's|herself|him|himself|his|how|how's|i|i'd|i'll|i'm|i've|if|in|into|isn't|it|it's|"
			+ "its|itself|let's|me|more|most|mustn't|my|myself|no|nor|not|of|off|on|once|only|or|other|ought|our|ours|ourself|ourselves|out|over|own|same|shan't|"
			+ "she|she'd|she'll|she's|should|shouldn't|so|some|such|than|that|that's|the|their|theirs|them|themselves|then|there|there's|these|they|they'd|they'll|"
			+ "they're|they've|this|those|through|to|too|under|until|up|very|was|wasn't|we|we'd|we'll|we're|we've|were|weren't|what|what's|when|when's|where|where's|"
			+ "which|while|who|who's|whom|why|why's|with|won't|would|wouldn't|you|you'd|you'll|you're|you've|your|yours|yourself|yourselves|";
	
	private TokenFilter nextFilter;
	private TokenStream myStream;
	
	public TokenFilter getNextFilter() {
		return nextFilter;
	}

	public void setNextFilter(TokenFilter nextFilter) {
		this.nextFilter = nextFilter;
	}
	
	public StopwordTokenFilter(TokenStream stream) {
		// TODO Auto-generated constructor stub
		super(stream);
		myStream = stream;
	}

	@Override
	//This method removes the stopwords from the token stream
	public void perform() {
		try {
			if(increment()) {
				Token myToken = myStream.next();
				if(STOPWORD_LIST.contains(myToken.getTermText())) {
					myStream.remove();
				}
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
