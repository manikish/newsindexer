package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.TokenStream;

public interface Writer {

	public void write(TokenStream stream, String fileId);
}
