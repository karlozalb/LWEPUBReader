package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.epubloader.EPUBUtils;
import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

import java.util.LinkedList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Spine implements IVerificable,IEPUBMainNode{
	
	public String ID;
	public String TOC;
	public String PAGE_PROGRESSION_DIRECTION;
	
	LinkedList<ItemRef> ITEM_REF;
	
	public Spine(){
		ITEM_REF = new LinkedList<ItemRef>();
	}
	
	public class ItemRef{
		public String idref;
		public String linear;
		public String id;
		public String properties;
		
		public String toString(){
			return "<itemRef idref=\""+idref+"\" linear=\""+linear+"\" id=\""+id+"\" properties=\""+properties+"\">\n";
		}
	}

	@Override
	public void parse(Node pnode) {		
		NamedNodeMap attributes = pnode.getAttributes();
		
		ID = EPUBUtils.getAttributeValue("id", attributes);
		TOC = EPUBUtils.getAttributeValue("toc", attributes);
		PAGE_PROGRESSION_DIRECTION = EPUBUtils.getAttributeValue("page-progression-direction", attributes);

		
		NodeList children = pnode.getChildNodes();
		int size = children.getLength();
		
		for (int i=0;i<size;i++){
			ItemRef item = new ItemRef();	
			Node node = children.item(i);			
			
			if (node.hasAttributes()){
				attributes = node.getAttributes();
				
				item.id = EPUBUtils.getAttributeValue("id", attributes);		
				item.idref = EPUBUtils.getAttributeValue("idref", attributes);
				item.linear = EPUBUtils.getAttributeValue("linear", attributes);
				item.properties = EPUBUtils.getAttributeValue("properties", attributes);
				
				ITEM_REF.add(item);
			}		
			
		}		
	}

	@Override
	public boolean isValid() throws EPUBException {
		
		if (!PAGE_PROGRESSION_DIRECTION.equalsIgnoreCase("ltr") && !PAGE_PROGRESSION_DIRECTION.equalsIgnoreCase("rtl") && !PAGE_PROGRESSION_DIRECTION.equalsIgnoreCase("default")) return false;
		
		for (ItemRef itemref : ITEM_REF){
			if (!itemref.linear.equalsIgnoreCase("yes") && !itemref.linear.equalsIgnoreCase("no")) return false;
		}
		
		return false;
	}
	
	public String toString(){
		String res = "<spine id=\""+ID+"\" toc=\""+TOC+"\" page-progression-direction=\""+PAGE_PROGRESSION_DIRECTION+"\">\n";
		
		for (ItemRef ir : ITEM_REF){
			res+="\t"+ir.toString();
		}
		
		res+="</spine>\n";
				
		return res;
	}	

	public LinkedList<ItemRef> getItemRefs(){
		return ITEM_REF;
	}

}
