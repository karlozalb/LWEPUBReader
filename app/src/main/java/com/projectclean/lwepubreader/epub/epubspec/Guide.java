package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.epubloader.EPUBUtils;
import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

import java.util.LinkedList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Guide implements IVerificable,IEPUBMainNode{
	
	LinkedList<Reference> REFERENCES;
	
	public Guide(){
		REFERENCES = new LinkedList<Reference>();
	}

	@Override
	public void parse(Node pnode) {
		NodeList children = pnode.getChildNodes();
		int size = children.getLength();
		
		for (int i=0;i<size;i++){
			Reference ref = new Reference();
			Node node = children.item(i);
			
			if (node.hasAttributes()){
				NamedNodeMap attributes = node.getAttributes();
				
				ref.type = EPUBUtils.getAttributeValue("type", attributes);
				ref.title = EPUBUtils.getAttributeValue("title", attributes);
				ref.href = EPUBUtils.getAttributeValue("href", attributes);
				
				REFERENCES.add(ref);
			}			
		}
	}
	
	public String toString(){
		String res = "<guide>\n";
		
		for (Reference mt : REFERENCES){
			res+="\t" + mt.toString();
		}
		
		res += "</guide>\n";
		return res;
	}

	@Override
	public boolean isValid() throws EPUBException {
		return false;
	}

	public class Reference{		
		public String type;
		public String title;	
		public String href;
		
		public String toString(){
			return "<reference type=\""+type+"\" title=\""+title+"\" href=\""+href+"\">\n";
		}
	}
}
