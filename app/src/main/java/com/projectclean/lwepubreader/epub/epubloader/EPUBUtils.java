package com.projectclean.lwepubreader.epub.epubloader;

import org.w3c.dom.NamedNodeMap;

public class EPUBUtils {

	public static String getAttributeValue(String pattributename,NamedNodeMap pnodes){		
		
		if (pnodes != null && pnodes.getNamedItem(pattributename) != null){
			return pnodes.getNamedItem(pattributename).getNodeValue();
		}	
		
		return "";
	}
	
	public static StringBuilder trimLeadingAndTrailingWhiteSpaces(StringBuilder pstring){		
		int i = 0;
		
		for (i=0;i<pstring.length();i++){
			if (pstring.charAt(i) != ' ' && pstring.charAt(i) != '\t' && pstring.charAt(i) != '\n' && pstring.charAt(i) != '\r') break;
		}
		
		pstring.delete(0, i);
		
		for (i=pstring.length()-1;i>=0;i--){
			if (pstring.charAt(i) != ' ' && pstring.charAt(i) != '\t' && pstring.charAt(i) != '\n' && pstring.charAt(i) != '\r') break;
		}
		
		if (i>=0) pstring.delete(i+1, pstring.length());
		
		return pstring;
	}	

}
