package edu.buffalo.cse.irf14.analysis;

public class StopwordTokenFilter extends TokenFilter {
	
	public StopwordTokenFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if(getStream()!=null&&getStream().getCurrent()!=null) {
			return true;
		}
		return false;
	}

}
