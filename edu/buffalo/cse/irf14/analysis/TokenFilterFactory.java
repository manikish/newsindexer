/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	
	private static TokenFilterFactory myClass;
	
	private TokenFilterFactory() {}
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if(myClass==null) {
			myClass = new TokenFilterFactory();
		} 
		return myClass;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenFilter myFilter = null;
		switch(type) {
		case STOPWORD: 
				//create new TokenFilter for stopword elimination.
				myFilter = new StopwordTokenFilter(stream);
				break;
		case CAPITALIZATION: 
				myFilter = new CapitalizationTokenFilter(stream);
				break;
		case DATE:
				myFilter = new DateTokenFilter(stream);
				break;
		case STEMMER: 
				myFilter = new StemmerTokenFilter(stream);
				break;
		case NUMERIC:
				myFilter = new NumberTokenFilter(stream);
				break;
		case SYMBOL:
				myFilter = new SymbolTokenFilter(stream);
				break;
		case SPECIALCHARS:
			    myFilter = new SpecialCharTokenFilter(stream);
			    break;
		}
		return myFilter;
	}
}
	
