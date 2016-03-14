package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.epubloader.EPUBUtils;
import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Package implements IVerificable,IEPUBMainNode{

	public static String VERSION = "version",UNIQUE_IDENTIFIER = "unique-identifier",PREFIX = "prefix",XML_LANG = "xml:lang",DIR = "dir",ID = "id";
	
	String version,unique_identifier,prefix,xml_lang,dir,id;
	
	Metadata METADATA;
	Manifest MANIFEST;
	Spine SPINE;
	Guide GUIDE;
	Bindings BINDINGS;
	Collection COLLECTION;
	
	public Package(){
		METADATA = new Metadata();
		MANIFEST = new Manifest();
		SPINE = new Spine();
		GUIDE = new Guide();
		BINDINGS = new Bindings();
		COLLECTION = new Collection();
	}
	
	public Metadata getMetadata(){
		return METADATA;
	}
	
	public Manifest getManifest(){
		return MANIFEST;
	}
	
	public Spine getSpine(){
		return SPINE;
	}
	
	public Guide getGuide(){
		return GUIDE;
	}
	
	public Bindings getBindings(){
		return BINDINGS;
	}
	
	public Collection getCollection(){
		return COLLECTION;
	}

	@Override
	public void parse(Node pnode) {		
		NamedNodeMap attributes = pnode.getAttributes();		
		
		version = EPUBUtils.getAttributeValue(VERSION, attributes);
		unique_identifier = EPUBUtils.getAttributeValue(UNIQUE_IDENTIFIER, attributes);
		
		//We need this identifier to correctly validate the metadata node later.
		METADATA.setPackageUniqueId(unique_identifier);		
		
		prefix = EPUBUtils.getAttributeValue(PREFIX, attributes);
		xml_lang = EPUBUtils.getAttributeValue(XML_LANG, attributes);
		dir = EPUBUtils.getAttributeValue(DIR, attributes);
		id = EPUBUtils.getAttributeValue(ID, attributes);
	}

	@Override
	public boolean isValid() throws EPUBException {
		return false;
	}
	
	public String toString(){
		String res = "<package version=\""+version+"\" unique_identifier=\""+unique_identifier+"\" prefix=\""+prefix+"\" xml_lang=\""+xml_lang+"\" dir=\""+dir+"\" id=\""+id+"\">\n";
		
		res+=METADATA.toString();
		res+=MANIFEST.toString();
		res+=SPINE.toString();
		res+=GUIDE.toString();
		res+=BINDINGS.toString();
		
		res+="</package>\n";
		
		return res;
	}
	
}
