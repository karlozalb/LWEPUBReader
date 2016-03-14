package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

import java.util.LinkedList;

import org.w3c.dom.Node;

public class Collection implements IVerificable,IEPUBMainNode{
	
	public String XML_LANG,DIR,ID,ROLE;
	
	/*
	 * This is a recursive structure, since a Collection element could has other Collections inside.
	 * There will be needed a check system that take into account the specific necessities of the Metadata/Link versions for this element.
	 */
	LinkedList<Metadata> METADATA; //I use the same class, but the check system will check for EPUB compliance.
	LinkedList<Metadata.Link> LINK;  //I use the same class, but the check system will check for EPUB compliance.
	LinkedList<Collection> COLLECTION;
	
	@Override
	public void parse(Node pnode) {
		//Todo!
	}
	@Override
	public boolean isValid() throws EPUBException {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
