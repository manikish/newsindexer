package edu.buffalo.cse.irf14.analysis;

public class ContentFilter extends TokenFilter {
	private TokenFilter nextFilter;

	public ContentFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return super.getStream();
	}

}
