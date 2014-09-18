/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author Administrator
 *
 */
public class CapitalizationTokenFilter extends TokenFilter {
	
	private TokenFilter nextFilter;
	
	public CapitalizationTokenFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setNextFilter(TokenFilter nextFilter) {
		this.nextFilter = nextFilter;
	}

	@Override
	public TokenFilter getNextFilter() {
		return nextFilter;
	}

	@Override
	public void perform() {
		TokenStream myStream = getStream();
		myStream.reset();
		try {
			if(increment()) {
				Token myToken = myStream.next();
				if(myToken.getRetainText()!=null && !myToken.getRetainText()) {
					String text = myToken.getTermText();
					text = text.replace(text.charAt(0), (char)(text.charAt(0)+32));
					myToken.setTermText(text);
					System.out.println(text);
				}
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
