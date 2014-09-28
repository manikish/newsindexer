/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * This factory class is responsible for instantiating "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {
	private static AnalyzerFactory myClass;
	
	private AnalyzerFactory() {}
	
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
	public static AnalyzerFactory getInstance() {
		//TODO: YOU NEED TO IMPLEMENT THIS METHOD
		if(myClass==null) {
			synchronized (AnalyzerFactory.class) {
				if(myClass==null)
					myClass = new AnalyzerFactory();
			}
		}
		return myClass;
	}
	
	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance
	 * for a given {@link FieldNames} field
	 * Note again that the singleton factory instance allows you to reuse
	 * {@link TokenFilter} instances if need be
	 * @param name: The {@link FieldNames} for which the {@link Analyzer}
	 * is requested
	 * @param TokenStream : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable {@link FieldNames}
	 * null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream) {
		//TODO : YOU NEED TO IMPLEMENT THIS METHOD
		TokenFilter myFilterChain = null;
		TokenFilterFactory myFilterFactory = TokenFilterFactory.getInstance();
		TokenFilter tempFilter = null;
		switch(name) {
		case CATEGORY: {
			myFilterChain = myFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream);
			tempFilter = myFilterChain;
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, stream));
			tempFilter = tempFilter.getNextFilter();
			break;
		}
		case AUTHOR:
		case AUTHORORG: {
			myFilterChain = myFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream);
			tempFilter = myFilterChain;
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream));
			tempFilter = tempFilter.getNextFilter();
			break;
		}
		case CONTENT: {
				myFilterChain = myFilterFactory.getFilterByType(TokenFilterType.STOPWORD, stream);
				tempFilter = myFilterChain;
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.DATE, stream));
				tempFilter = tempFilter.getNextFilter();
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.NUMERIC, stream));
				tempFilter = tempFilter.getNextFilter();
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream));
				tempFilter = tempFilter.getNextFilter();
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, stream));
				tempFilter = tempFilter.getNextFilter();
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream));
				tempFilter = tempFilter.getNextFilter();
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.STEMMER, stream));
				tempFilter = tempFilter.getNextFilter();
				tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, stream));
				tempFilter = tempFilter.getNextFilter();
				break;
				}
		case TITLE: {
			myFilterChain = myFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream);
			tempFilter = myFilterChain;
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream));
			tempFilter = tempFilter.getNextFilter();
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, stream));
			tempFilter = tempFilter.getNextFilter();
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.STEMMER, stream));
			tempFilter = tempFilter.getNextFilter();
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, stream));
			tempFilter = tempFilter.getNextFilter();
			break;
			}
		case PLACE: {
			myFilterChain = myFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream);
			tempFilter = myFilterChain;
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, stream));
			tempFilter = tempFilter.getNextFilter();
			
			break;
			}
		case NEWSDATE: {
			myFilterChain = myFilterFactory.getFilterByType(TokenFilterType.ACCENT, stream);
			tempFilter = myFilterChain;			
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.DATE, stream));
			tempFilter = tempFilter.getNextFilter();			
			tempFilter.setNextFilter(myFilterFactory.getFilterByType(TokenFilterType.SYMBOL, stream));
			tempFilter = tempFilter.getNextFilter();
			
			break;
			}
		}
		return myFilterChain;
	}
}
