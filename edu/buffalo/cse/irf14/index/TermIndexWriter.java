package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.buffalo.cse.irf14.analysis.TokenStream;

/**
 * @author sumanth
*/
public class TermIndexWriter implements Writer{

	@Override
	public void write(TokenStream stream, String fileId) {
		// TODO Auto-generated method stub
		FileWriter writer = null;
		FileReader reader = null;
		try {
			reader = new FileReader(IndexWriter.indexDir+File.separator+"termindex");
		} catch (FileNotFoundException e) {
			try {
				writer = new FileWriter(IndexWriter.indexDir+File.separator+"termindex");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}
