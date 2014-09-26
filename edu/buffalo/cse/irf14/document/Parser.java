/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	
	//modified sumanth-Sep4//Added constants for extracting Fields.
	public static final String AUTHOR_START_TAG = "<AUTHOR>";
	public static final String AUTHOR_END_TAG = "</AUTHOR>";
	public static final String AUTHOR_BY_DELIMITER = "BY";
	public static final String CONTENT_HYPHEN_SEPERATOR = "-";
	public static final String COMMA_SEPERATOR = ",";
	public static final String SPACE_SEPERATOR = " ";
	
	//private variable which checks if content has been started and hence all the content henceforth needs to be appended
	private static Boolean isContentStarted = Boolean.FALSE;
	private static StringBuffer myContent = new StringBuffer();
	private static StringBuffer title = new StringBuffer();

	private static Boolean AUTHOR_TAG_SET = Boolean.FALSE;
	private static Boolean isPlaceAndDateGiven = Boolean.FALSE;

	//end mod
	
	//Added only for testing the write to result file//delete this after testing
	private static FileWriter writer;
	
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		Document myDocument = new Document();
	
		try {
			String[] myFields = filename.split("\\"+File.separator);
			//set FileId in Document obj
			String myFileId = myFields[myFields.length-1];
			myDocument.setField(FieldNames.FILEID, myFileId.trim());
			//set Category in Document obj
			String myCategory = myFields[myFields.length-2];
			myDocument.setField(FieldNames.CATEGORY, myCategory.trim());
			
			//Added for testing
			System.out.println("File: "+myCategory.trim()+"\\"+myFileId.trim());
			
			 //read each line and populate Title and other fields
			Scanner myScanner = new Scanner(new File(filename));
			String myLine = new String();
			boolean isTitlePopulated = false;
			while(myScanner.hasNextLine()) {
				if((myLine = myScanner.nextLine()).trim().length()!=0) {
					 if (!isTitlePopulated) {
							title = title.append(myLine);
							
							
					 }
					 else
					 {
							populateFields(myDocument, myLine);
					 }
				}
				else if (title.length() != 0)
				{
					isTitlePopulated = true;
				}
			}
			myDocument.setField(FieldNames.TITLE, title.toString());
			myScanner.close();
			//set the Content to the Document obj
			myDocument.setField(FieldNames.CONTENT, myContent.toString());
			restoreDefaults();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			ParserException p = new ParserException(e.getMessage());
			throw p;
		} 
		catch (ArrayIndexOutOfBoundsException a)
		{
			
			ParserException p = new ParserException(a.getMessage());
			throw p;
		}
		catch (NullPointerException n)
		{
			ParserException p = new ParserException(n.getMessage());
			throw p;
		}
		printMap(myDocument);    
		return myDocument;
	}
	
	//created to restore the constants to their default values for use by another file
	private static void restoreDefaults() {
		// TODO Auto-generated method stub
		isContentStarted = Boolean.FALSE;
		myContent = new StringBuffer();
		AUTHOR_TAG_SET = Boolean.FALSE;
		isPlaceAndDateGiven = Boolean.FALSE;
		title = new StringBuffer();
	}

	//redundant method- remove this after use
	private static void printMap(Document aDocument) {
		try {
			if(writer==null) {
				writer = new FileWriter("Result");
			}
			writer.append("Category and File ID: " + aDocument.getField(FieldNames.CATEGORY)[0]+"\\"+aDocument.getField(FieldNames.FILEID)[0]);
			writer.append(" Title: " + aDocument.getField(FieldNames.TITLE)[0]);
			writer.append(" Author: " + (aDocument.getField(FieldNames.AUTHOR)==null?null:aDocument.getField(FieldNames.AUTHOR)[0]));
			writer.append(" AuthorORG: " + (aDocument.getField(FieldNames.AUTHORORG)==null?null:aDocument.getField(FieldNames.AUTHORORG)[0]));
			writer.append(" Place: " + (aDocument.getField(FieldNames.PLACE)==null?null:aDocument.getField(FieldNames.PLACE)[0]));
			writer.append(" Date: " + (aDocument.getField(FieldNames.NEWSDATE)==null?null:aDocument.getField(FieldNames.NEWSDATE)[0]));
			writer.append(" Content: " + (aDocument.getField(FieldNames.CONTENT)==null?null:aDocument.getField(FieldNames.CONTENT)[0]));
			writer.append("\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//end method

	private static void populateFields(Document aDocument, String aText) {
		String myText = new String();
		String[] myFields = {""};
		
		if(!isContentStarted){
			if(!AUTHOR_TAG_SET) {
				//populating Author and AuthorORG tags- step 1
				
				int myStartIndex = aText.indexOf(AUTHOR_START_TAG);
				int myEndIndex = aText.indexOf(AUTHOR_END_TAG);
				//ONLY if both tags are present then populate the Author and AuthorORG Fields- step 1.1
				if(myStartIndex!=-1 && myEndIndex!=-1 && !(myStartIndex>myEndIndex)) {
					myText = aText.substring(myStartIndex+8, myEndIndex).trim();
					myFields = myText.indexOf(COMMA_SEPERATOR)!= -1?myText.split(COMMA_SEPERATOR):new String[] {myText, " "};
					aDocument.setField(FieldNames.AUTHORORG, myFields[1].trim()); 
					if(AUTHOR_BY_DELIMITER.equalsIgnoreCase(myFields[0].split(SPACE_SEPERATOR)[0])) { 
						myText = myFields[0].substring(AUTHOR_BY_DELIMITER.length()).trim();   
					}
					else {
						myText = myFields[0].trim();
					}
					aDocument.setField(FieldNames.AUTHOR, myText);
					AUTHOR_TAG_SET = Boolean.TRUE;
				}
				else
				{
					parsePlaceAndDate(aDocument, aText);
				}
			}
			else
			{
				parsePlaceAndDate(aDocument, aText);
				
			}
		}
		else {
			myContent = myContent.append(SPACE_SEPERATOR).append(aText.trim());
		}
	}
private static void parsePlaceAndDate(Document document, String aText)
	{
		document.setField(FieldNames.PLACE, new String(""));
		document.setField(FieldNames.NEWSDATE, new String("")); 
		
		String[] firstLineComponents = aText.split(CONTENT_HYPHEN_SEPERATOR);
		if (firstLineComponents.length > 0 ) {
			String[] cityAndDateComponents = firstLineComponents[0].split(COMMA_SEPERATOR);
			if (cityAndDateComponents.length > 0) {
				String date = cityAndDateComponents[cityAndDateComponents.length - 1].trim();
	            String[] dateComponents = date.split(SPACE_SEPERATOR);
	            if (dateComponents.length > 0) {
		            String month = dateComponents[0];
		    		String[] months = {"Jan","Feb","March","April","May","June","July","Aug","Sep","Oct","Nov","Dec"};
		    		for (String string : months) {
		    			if (month.equalsIgnoreCase(string)) {
		    				document.setField(FieldNames.NEWSDATE, date);
		    				String place = "";
		    				int length = cityAndDateComponents.length;
		    				for (int j = 0; j < length - 1; j++) {
		    					if (j == length - 2) {
									place = place + cityAndDateComponents[j];
								}
		    					else
		    					{
									place = place + cityAndDateComponents[j] +",";
								}
							}
		    				document.setField(FieldNames.PLACE, place.trim());
							isPlaceAndDateGiven = true;
							if (firstLineComponents.length > 1) {
								myContent = new StringBuffer(firstLineComponents[1].trim());
							}
							break;
		    			}
		    			
		    		}
				}
	           
			}
			
		}
		
		
		if (!isPlaceAndDateGiven) {
			myContent = new StringBuffer(aText.trim());
			document.setField(FieldNames.PLACE, new String(""));
			document.setField(FieldNames.NEWSDATE, new String("")); 
		}
		isContentStarted = Boolean.TRUE;
	}
}


