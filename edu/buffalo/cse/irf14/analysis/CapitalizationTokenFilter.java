/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class CapitalizationTokenFilter extends TokenFilter {
	
	private TokenFilter nextFilter;
	private boolean isStreamAnalyzed;
	
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
		/*if(!isStreamAnalyzed)
			analyzeStream(myStream);*/
		while(myStream.hasNext()) {
			Token myToken = myStream.next();
			myToken.setTermText(myToken.getTermText().toLowerCase());
			/*if (myToken.getRetainText() != null && !myToken.getRetainText()) {
				String text = myToken.getTermText();
				text = text.replace(text.charAt(0), (char) (text.charAt(0) + 32));
				myToken.setTermText(text);
//				System.out.println(text);
			}*/
		}		
	}

	private void analyzeStream(TokenStream myStream) {
		while(myStream.hasNext()) {
			Token t= myStream.next();
			List<Token> list = new ArrayList<Token>();
			if(t.getRetainText()!=null && t.getRetainText()) {
				//int length =0;
				while(myStream.hasNext()) {
					Boolean retainValue = myStream.next().getRetainText();
					if(retainValue!=null && retainValue) {
						list.add(myStream.previous());
						myStream.remove();
					//	length++;
					}else {
						break;
					}
				}
				/*for(;length!=0;length--)
					myStream.next();*/
			}			
			t.merge(list.toArray(new Token[0]));
		}
		isStreamAnalyzed = true;
		myStream.reset();
	}

}
