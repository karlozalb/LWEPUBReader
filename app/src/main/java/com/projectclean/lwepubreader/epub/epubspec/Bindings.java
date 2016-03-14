package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.epubloader.EPUBUtils;
import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

import java.util.LinkedList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Bindings implements IVerificable,IEPUBMainNode {

	LinkedList<MediaType> MEDIATYPE;
	
	public Bindings(){
		MEDIATYPE = new LinkedList<MediaType>();
	}

	@Override
	public void parse(Node pnode) {
		NodeList children = pnode.getChildNodes();
		int size = children.getLength();
		
		for (int i=0;i<size;i++){
			MediaType mediaType = new MediaType();	
			Node node = children.item(i);
			
			if (node.hasAttributes()){				
				NamedNodeMap attributes = node.getAttributes();	
				
				mediaType.media_type = EPUBUtils.getAttributeValue("media_type", attributes);
				mediaType.handler = EPUBUtils.getAttributeValue("handler", attributes);					
			}
			MEDIATYPE.add(mediaType);
		}		
	}

	@Override
	public boolean isValid() throws EPUBException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString(){
		String res = "<bindings>\n";
		
		for (MediaType mt : MEDIATYPE){
			res+="\t" + mt.toString();
		}
		
		res += "</bindings>\n";
		return res;
	}
	
	public class MediaType{
		public String media_type;
		public String handler;
		
		public String toString(){
			return "<mediaType handler=\""+handler+"\" media-type=\""+media_type+"\">\n";
		}
	}
}
