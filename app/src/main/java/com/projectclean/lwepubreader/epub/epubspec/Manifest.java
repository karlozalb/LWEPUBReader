package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.epubloader.EPUBUtils;
import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

import java.util.LinkedList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Manifest implements IVerificable,IEPUBMainNode{
	
	public String id;
	LinkedList<Item> ITEM;
	
	public Manifest(){
		ITEM = new LinkedList<Item>();
	}
	
	public class Item{
		public String id; 
		public String href; 
		public String media_type; 
		public String fallback; 
		public String properties;
		public String media_overlay;
		
		public String toString(){
			return "<item id=\""+id+"\" href=\""+href+"\" media_type=\""+media_type+"\" fallback=\""+fallback+"\" properties=\""+properties+"\" media_overlay=\""+media_overlay+"\">\n";
		}
	}
	
	@Override
	public void parse(Node pnode) {
		
		NodeList children = pnode.getChildNodes();
		int size = children.getLength();
		
		for (int i=0;i<size;i++){
			Item item = new Item();	
			Node node = children.item(i);
			
			if (node.hasAttributes()){
				NamedNodeMap attributes = node.getAttributes();
				
				item.id = EPUBUtils.getAttributeValue("id", attributes);
				item.href = EPUBUtils.getAttributeValue("href", attributes);
				item.media_type = EPUBUtils.getAttributeValue("media-type", attributes);	
				item.fallback = EPUBUtils.getAttributeValue("fallback", attributes);
				item.properties = EPUBUtils.getAttributeValue("properties", attributes);	
				item.media_overlay = EPUBUtils.getAttributeValue("media_overlay", attributes);
				
				ITEM.add(item);
			}
			
		}		
	}
	
	public LinkedList<Item> getItems(){
		return ITEM;
	}
	
	public String getCoverImagePath(){
		for (Item item : ITEM){
			if (item.properties.equalsIgnoreCase("cover-image")) return item.href;
		}
		
		return null;
	}
	
	public String toString(){
		String res = "<manifest>\n";
		
		for (Item mt : ITEM){
			res+="\t" + mt.toString();
		}
		
		res += "</manifest>\n";
		return res;
	}

	@Override
	public boolean isValid() throws EPUBException {
		return true;
	}
	
	public Item getItemByIdRef(String pidref){
		Item toRet = null;
		
		for (Item it : ITEM){
			if (it.id.equalsIgnoreCase(pidref)){
				toRet = it;
				break;
			}
		}
		return toRet;
	}

}
