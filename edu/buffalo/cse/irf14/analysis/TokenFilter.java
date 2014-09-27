/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * The abstract class that you must extend when implementing your 
 * TokenFilter rule implementations.
 * Apart from the inherited Analyzer methods, we would use the 
 * inherited constructor (as defined here) to test your code.
 * @author nikhillo
 *
 */
	
public abstract class TokenFilter implements Analyzer {
	private TokenStream myStream;
	private TokenFilter next;
	/**
	 * Default constructor, creates an instance over the given
	 * TokenStream
	 * @param stream : The given TokenStream instance
	 */
	public TokenFilter(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		myStream = stream;
	}
	
	public TokenStream getStream() {
		return myStream;
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		next = this;
		while(next!=null) {
			
				next.perform();
				next = next.getNextFilter();
			
		}
		
		return false;
	}
	
	public abstract void setNextFilter(TokenFilter nextFilter); // dont know if we can add this
	public abstract TokenFilter getNextFilter();
	public abstract void perform();
}
