package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.epubloader.EPUBUtils;
import com.projectclean.lwepubreader.epub.exceptions.EPUBException;
import com.projectclean.lwepubreader.epub.exceptions.InvalidIdentifierException;
import com.projectclean.lwepubreader.epub.exceptions.InvalidMetadataException;
import com.projectclean.lwepubreader.epub.exceptions.MetaElementWithZeroLengthValueException;
import com.projectclean.lwepubreader.epub.exceptions.MultipleTitlesWithoutAssociatedMetaException;
import com.projectclean.lwepubreader.epub.exceptions.OptionalElementWithZeroLengthValueException;
import com.projectclean.lwepubreader.epub.exceptions.UniqueIdentifierNotUniqueException;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Metadata implements IVerificable,IEPUBMainNode,IMetadata{

	private String packageUniqueId;
	
	LinkedList<DCIdentifier> DC_IDENTIFIER;
	LinkedList<DCTitle> DC_TITLE;
	LinkedList<DCLanguage> DC_LANGUAGE;
	LinkedList<Meta> META;
	LinkedList<OptionalElement> DCMES_OPTIONAL;
	LinkedList<OPF2Meta> OPF2_META;
	LinkedList<Link> LINK;
	
	public Metadata(){
		DC_IDENTIFIER = new LinkedList<DCIdentifier>();
		DC_TITLE = new LinkedList<DCTitle>();
		DC_LANGUAGE = new LinkedList<DCLanguage>();
		META = new LinkedList<Meta>();
		DCMES_OPTIONAL = new LinkedList<OptionalElement>();
		OPF2_META = new LinkedList<OPF2Meta>();
		LINK = new LinkedList<Link>();
	}
	
	public void setPackageUniqueId(String puniqueidentifier){
		packageUniqueId = puniqueidentifier;
	}
	
	/**********************************************************************************
	 * Class that describes a required element.
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class RequiredElement{		
		public String textContent;
		public String id;		
		
		public boolean check(){
			return id==null || id!=null && id.length() > 0;
		}		
		
		public String toString(){
			return "id: "+id+" - textContent: "+textContent;
		}
	}
	
	/***********************************************************************************
	 * Class that describes an optional element.
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class OptionalElement extends RequiredElement{
		public String nodeName;
		
		public String toString(){
			return "<"+nodeName+" id=\""+id+"\">"+textContent+"</"+nodeName+">\n";
		}
	}
	
	/***********************************************************************************
	 * Class that describes an "extended" optional element.
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class OptionalElementLangAndDir extends OptionalElement{
		public String xml_lang;
		public String dir;
		
		public boolean check(){
			if (dir != null && !(dir.equalsIgnoreCase("rtl") || dir.equalsIgnoreCase("ltr"))){	
				return false;
			}else{
				return super.check();
			}			
		}	
		
		public String toString(){
			return "<"+nodeName+" id=\""+id+"\" xml_lang=\""+xml_lang+"\" dir=\""+dir+"\">"+textContent+"</"+nodeName+">\n";
		}
	}
	
	
	//
	
	/***********************************************************************************
	 * dc:identifier class.
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class DCIdentifier extends RequiredElement{		
		public String toString(){
			return "<dc:identifier id=\""+id+"\">"+textContent+"</dc:identifier>\n";
		}
	}
	
	/***********************************************************************************
	 * dc:title class.
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class DCTitle extends OptionalElementLangAndDir{		
		
		public String toString(){
			return "<dc:title xml_lang=\""+xml_lang+"\" dir=\""+dir+"\" id=\""+id+"\">"+textContent+"</dc:title>\n";
		}
		
	}
	
	/***********************************************************************************
	 * Meta class
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class Meta extends OptionalElementLangAndDir{		
		public String property;
		public String refines;
		public String scheme;	
		
		public String toString(){
			return "<meta property=\""+property+"\" refines=\""+refines+"\" scheme=\""+scheme+"\" lang=\""+xml_lang+"\" dir=\""+dir+"\">"+textContent+"</meta>\n";
		}
	}
	
	/***********************************************************************************
	 * OPF2 deprecated meta class.
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class OPF2Meta extends OptionalElement{
		public String name;
		public String content;
		
		public String toString(){
			return "<meta name=\""+name+"\" content=\""+content+"\"/>\n";
		}
	}
	
	/***********************************************************************************
	 * cd:language class
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class DCLanguage extends RequiredElement{
		
		public boolean check(){
			return true;
		}
		
		public String toString(){
			return "<dc:language id=\""+id+"\">"+textContent+"</dc:language>\n";
		}
	}
	
	/**********************************************************************************
	 * Optional DCMES elements 
	 **********************************************************************************/
	
	/*public class DCContributor extends OptionalElementLangAndDir{}
	
	public class DCCoverage extends OptionalElementLangAndDir{}
	
	public class DCCreator extends OptionalElementLangAndDir{}
	
	public class DCDate extends OptionalElement{}
	
	public class DCDescription extends OptionalElementLangAndDir{}
	
	public class DCFormat extends OptionalElement{}
	
	public class DCPublisher extends OptionalElementLangAndDir{}
	
	public class DCRelation extends OptionalElementLangAndDir{}
	
	public class DCRights extends OptionalElementLangAndDir{}
	
	public class DCSource extends OptionalElement{}
	
	public class DCSubject extends OptionalElementLangAndDir{}
	
	public class DCType extends OptionalElement{}	*/
	
	/***********************************************************************************
	 * Link class
	 * @author Carlos A.P.
	 *
	 **********************************************************************************/
	public class Link extends RequiredElement{
		String href;
		String rel;
		String refines;
		String media_type;
		
		public String toString(){
			return "<link href=\""+href+"\" rel=\""+rel+"\" id=\""+id+"\" refines=\""+refines+"\" media-type=\""+media_type+"\"/>\n";
		}
	}

	@Override
	public boolean isValid() throws EPUBException {
		
		/* Phase 1
		 * The minimal required metadata that each Rendition of an EPUB Publication must include
		 * consists of three elements from the Dublin Core Metadata Element Set [DCMES] � title , 
		 * identifier and language � together with the modified property from DCMI Metadata Terms [DCTERMS]
		 */
		
		if (DC_IDENTIFIER.size() < 1) throw new InvalidMetadataException("There is no dc:identifier element in metadata. It's needed one element of this type at least.");
		if (DC_TITLE.size() < 1) throw new InvalidMetadataException("There is no dc:title element in metadata. It's needed one element of this type at least.");
		if (DC_LANGUAGE.size() < 1) throw new InvalidMetadataException("There is no dc:language element in metadata. It's needed one element of this type at least.");
		if (META.size() < 1 || getMetaByAttributes("dcterms:modified",null,null).size() < 1) throw new InvalidMetadataException("There is no meta element in metadata. It's needed one element of this type at least.");
		
		//Check for correctness of the DCIdentifier ids.
		//***********************************************************************
		for (DCIdentifier identifier : DC_IDENTIFIER){
			if (!identifier.check()) throw new InvalidIdentifierException("dc:identifier: Not null id attributes need at least one character long.");
		}
		
		//Check for uniqueness of the unique-identifier.
		//***********************************************************************
		int uniqueAmount = 0; //If this number is different from 1, OOPS! ERROR!
		
		for (DCIdentifier identifier : DC_IDENTIFIER){
			if (identifier.id.equalsIgnoreCase(packageUniqueId)) uniqueAmount++;
		}
		
		//Check for multiple titles and if there are several, check if associated meta nodes exist.
		if (DC_TITLE.size() > 1){
			for (DCTitle title : DC_TITLE){
				boolean metaFound = false;
				for (Meta meta : META){
					if (meta.refines.equalsIgnoreCase(title.id) && meta.property.equalsIgnoreCase("title-type")) metaFound = true;
				}
				if (!metaFound){
					throw new MultipleTitlesWithoutAssociatedMetaException("When there are multiple titles, everyone needs an associated meta element which specifies the type of title (main, secondary, etc).");
				}
			}
		}
		
		if (uniqueAmount != 1) throw new UniqueIdentifierNotUniqueException("dc:identifier: the unique-identifier parameter of package must link with ONLY ONE dc:identifier element.");
		
		//Check for meta and DCMES elements contains non empty values.
		
		for (Meta meta : META){
			if (meta.textContent == null || meta.textContent.length() <= 0) throw new MetaElementWithZeroLengthValueException("All meta values must be not null after whitespace trimming");
		}
		for (OptionalElement opt : DCMES_OPTIONAL){
			if (opt.textContent == null || opt.textContent.length() <= 0) throw new OptionalElementWithZeroLengthValueException("All optional values must be not null after whitespace trimming");
		}
		
		return true;
	}

	public void parse(Node pnode) {
		
		NodeList children = pnode.getChildNodes();
		int size = children.getLength();
		
		for (int i=0;i<size;i++){
			Node item = children.item(i);
			String nodeName = item.getNodeName();
			
			//Required nodes
			if (nodeName.equalsIgnoreCase("dc:identifier")){
				DC_IDENTIFIER.add(parseIdentifier(item));
			}else if (nodeName.equalsIgnoreCase("dc:title")){
				DC_TITLE.add(parseTitle(item));
			}else if (nodeName.equalsIgnoreCase("dc:language")){
				DC_LANGUAGE.add(parseLanguage(item));			
			}else if (nodeName.equalsIgnoreCase("meta")){
				
				boolean isOPF2 = false;
				
				if (item.hasAttributes()){
					//Are there "name" and "content" attributes? If true, OPF2.					
					for (int j=0;j<item.getAttributes().getLength();j++){
						if (item.getAttributes().item(j).getNodeName().equalsIgnoreCase("name")){
							isOPF2 = true;
							break;
						}
					}
				}
				
				if (isOPF2){
					OPF2_META.add(parseOPF2Meta(item));
				}else{
					META.add(parseMeta(item));					
				}
			}else if (nodeName.equalsIgnoreCase("link")){
				LINK.add(parseLink(item));
			//Optional DCMES nodes.
			}else if (nodeName.equalsIgnoreCase("dc:contributor")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:coverage")){ //* 
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:creator")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:date")){
				DCMES_OPTIONAL.add(parseDCMESOptionalElement(item));
			}else if (nodeName.equalsIgnoreCase("dc:description")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:format")){
				DCMES_OPTIONAL.add(parseDCMESOptionalElement(item));
			}else if (nodeName.equalsIgnoreCase("dc:publisher")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:relation")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:rights")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:source")){
				DCMES_OPTIONAL.add(parseDCMESOptionalElement(item));
			}else if (nodeName.equalsIgnoreCase("dc:subject")){ //*
				DCMES_OPTIONAL.add(parseDCMESOptionalElementLandAndDir(item));
			}else if (nodeName.equalsIgnoreCase("dc:type")){
				DCMES_OPTIONAL.add(parseDCMESOptionalElement(item));
			}
		}		
	}

	private DCIdentifier parseIdentifier(Node pitem) {
		
		DCIdentifier identifier = new DCIdentifier();
		
		identifier.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){
			String identifierId = EPUBUtils.getAttributeValue("id", pitem.getAttributes());
			
			if (identifierId != null && identifierId.length() > 0) identifier.id = identifierId; 			
		}
		return identifier;
	}
	
	private DCTitle parseTitle(Node pitem) {
		
		DCTitle title = new DCTitle();		
		
		title.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){			
			NamedNodeMap attributes = pitem.getAttributes();
			
			String titleId = EPUBUtils.getAttributeValue("id", attributes);		
			if (titleId != null && titleId.length() > 0) title.id = titleId;
			
			String titleLang = EPUBUtils.getAttributeValue("xml:lang", attributes);	
			if (titleLang != null && titleLang.length() > 0) title.xml_lang = titleLang;
			
			String titleDir = EPUBUtils.getAttributeValue("dir", attributes);		
			if (titleDir != null && titleDir.length() > 0) title.dir = titleDir;
		}
		
		return title;
	}
	
	private DCLanguage parseLanguage(Node pitem) {
		
		DCLanguage language = new DCLanguage();
		
		language.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){
			NamedNodeMap attributes = pitem.getAttributes();
			
			String identifierId = EPUBUtils.getAttributeValue("id", attributes);
			
			if (identifierId != null && identifierId.length() > 0) language.id = identifierId; 			
		}
		
		return language;
	}
	
	private OptionalElement parseDCMESOptionalElement(Node pitem){
		
		OptionalElement optionalElement = new OptionalElement();
		
		optionalElement.nodeName = pitem.getNodeName();
		optionalElement.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){
			NamedNodeMap attributes = pitem.getAttributes();
			
			String elementId = EPUBUtils.getAttributeValue("id", attributes);	
			
			if (elementId != null && elementId.length() > 0) optionalElement.id = elementId; 			
		}		
		
		return optionalElement;
	}
	
	private OptionalElementLangAndDir parseDCMESOptionalElementLandAndDir(Node pitem){
		OptionalElementLangAndDir optionalElement = new OptionalElementLangAndDir();
		
		optionalElement.nodeName = pitem.getNodeName();
		optionalElement.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){
			NamedNodeMap attributes = pitem.getAttributes();
			
			String elementId = EPUBUtils.getAttributeValue("id", attributes);			
			if (elementId != null && elementId.length() > 0) optionalElement.id = elementId;
			
			String elementLang = EPUBUtils.getAttributeValue("xml:lang", attributes);			
			if (elementId != null && elementId.length() > 0) optionalElement.xml_lang = elementLang; 			
			
			String elementDir = EPUBUtils.getAttributeValue("dir", attributes);				
			if (elementId != null && elementId.length() > 0) optionalElement.dir = elementDir; 			
		}	
		
		return optionalElement;
	}
	
	private Meta parseMeta(Node pitem){
		Meta metaElement = new Meta();
		
		metaElement.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){	
			NamedNodeMap attributes = pitem.getAttributes();
			
			String elementId = EPUBUtils.getAttributeValue("id", attributes);		
			if (elementId != null && elementId.length() > 0) metaElement.id = elementId;
			
			String elementLang = EPUBUtils.getAttributeValue("xml:lang", attributes);				
			if (elementLang != null && elementLang.length() > 0) metaElement.xml_lang = elementLang; 			
			
			String elementDir = EPUBUtils.getAttributeValue("dir", attributes);				
			if (elementDir != null && elementDir.length() > 0) metaElement.dir = elementDir; 		
			
			String property = EPUBUtils.getAttributeValue("property", attributes);			
			if (property != null && property.length() > 0) metaElement.property = property;
			
			String refines = EPUBUtils.getAttributeValue("refines", attributes);				
			if (refines != null && refines.length() > 0) metaElement.refines = refines; 			
			
			String scheme = EPUBUtils.getAttributeValue("scheme", attributes);		
			if (scheme != null && scheme.length() > 0) metaElement.scheme = scheme; 			
		}	
		
		return metaElement;
	}
	
	private Link parseLink(Node pitem){
		Link link = new Link();
		
		link.textContent = pitem.getTextContent();
		
		if (pitem.hasAttributes()){			
			NamedNodeMap attributes = pitem.getAttributes();
			
			String href = EPUBUtils.getAttributeValue("href", attributes);		
			if (href != null && href.length() > 0) link.href = href;
			
			String rel = EPUBUtils.getAttributeValue("href", attributes);		
			if (rel != null && rel.length() > 0) link.rel = rel; 			
			
			String elementId = EPUBUtils.getAttributeValue("id", attributes);			
			if (elementId != null && elementId.length() > 0) link.id = elementId; 		
			
			String mediaType = EPUBUtils.getAttributeValue("media-type", attributes);			
			if (mediaType != null && mediaType.length() > 0) link.media_type = mediaType;
			
			String refines = EPUBUtils.getAttributeValue("refines", attributes);			
			if (refines != null && refines.length() > 0) link.refines = refines;			
		}	
		
		return link;
	}
	
	private OPF2Meta parseOPF2Meta(Node pitem){
		OPF2Meta meta = new OPF2Meta();
		
		if (pitem.hasAttributes()){	
			NamedNodeMap attributes = pitem.getAttributes();
			
			String name = EPUBUtils.getAttributeValue("name", attributes);			
			if (name != null && name.length() > 0) meta.name = name;
			
			String content = EPUBUtils.getAttributeValue("content", attributes);			
			if (content != null && content.length() > 0) meta.content = content;			
		}	
		
		return meta;
	}
	
	public LinkedList<DCIdentifier> getDC_IDENTIFIER() {
		return DC_IDENTIFIER;
	}

	public LinkedList<DCTitle> getDC_TITLE() {
		return DC_TITLE;
	}

	public LinkedList<DCLanguage> getDC_LANGUAGE() {
		return DC_LANGUAGE;
	}

	public LinkedList<Meta> getMETA() {
		return META;
	}

	public LinkedList<OptionalElement> getDCMES_OPTIONAL() {
		return DCMES_OPTIONAL;
	}

	public LinkedList<OPF2Meta> getOPF2_META() {
		return OPF2_META;
	}

	public LinkedList<Link> getLINK() {
		return LINK;
	}
	
	public LinkedList<OptionalElement> getOptionalElementsByName(String pname){	
		LinkedList<OptionalElement> elements = new LinkedList<OptionalElement>();
		for (OptionalElement opt : DCMES_OPTIONAL){
			if (opt.nodeName.equalsIgnoreCase(pname)) elements.add(opt);
		}
		return elements;
	}
	
	public OptionalElement getOptionalElementByName(String pname){
		for (OptionalElement opt : DCMES_OPTIONAL){
			if (opt.nodeName.equalsIgnoreCase(pname)) return opt;
		}
		return null;
	}
	
	public LinkedList<Meta> getMetaByAttributes(String property,String refines,String scheme){
		LinkedList<Meta> metaList = new LinkedList<Meta>();	
		
		for (Meta meta : META){
			
			boolean propertyCheck = false,refinesCheck = false,schemeCheck = false;
					
			if (property!=null && meta.property.equalsIgnoreCase(property) || property==null){
				propertyCheck = true;			
			}
			
			if (refines!=null && meta.refines.equalsIgnoreCase(refines) || refines==null){
				refinesCheck = true;			
			}
			
			if (scheme!=null && meta.scheme.equalsIgnoreCase(scheme) || scheme==null){
				schemeCheck = true;			
			}
			
			if (propertyCheck && refinesCheck && schemeCheck){
				metaList.add(meta);
			}
		}
		
		return metaList;
	}
	
	public String toString(){
		String res = "<metadata>\n";
		
		for (DCIdentifier mt : DC_IDENTIFIER){
			res+="\t" + mt.toString();
		}
		
		for (DCTitle mt : DC_TITLE){
			res+="\t" + mt.toString();
		}
		
		for (DCLanguage mt : DC_LANGUAGE){
			res+="\t" + mt.toString();
		}
		
		for (Meta mt : META){
			res+="\t" + mt.toString();
		}
		
		for (OptionalElement mt : DCMES_OPTIONAL){
			res+="\t" + mt.toString();
		}
		
		for (OPF2Meta mt : OPF2_META){
			res+="\t" + mt.toString();
		}
		
		for (Link mt : LINK){
			res+="\t" + mt.toString();		}		
		
		res += "</metadata>\n";
		
		return res;
	}

	@Override
	public String getBookTitle() throws EPUBException {
		if (DC_TITLE.size() == 0) throw new EPUBException();			
		if (DC_TITLE.size() == 1) return DC_TITLE.getFirst().textContent;
		
		//Multiple titles.	
		String titleId = null;
		
		for (Meta m : META){
			if (m.property.toUpperCase().equalsIgnoreCase("TITLE_TYPE") && m.textContent.toUpperCase().equalsIgnoreCase("MAIN")){
				titleId = m.refines;
			}
		}
		
		if (titleId == null) throw new EPUBException();	
		
		for (RequiredElement element : DC_TITLE){
			if (titleId.toUpperCase().endsWith(element.id.toUpperCase())) return element.textContent;
		}
		
		return null;
	}

	@Override
	public String[] getBookAuthor() throws EPUBException {
		if (DCMES_OPTIONAL.size() == 0) throw new EPUBException();			
		if (DCMES_OPTIONAL.size() == 1) {
			if (DCMES_OPTIONAL.getFirst().nodeName.equalsIgnoreCase("dc:creator")){
				String[] author = new String[1];
				
				author[0] = DCMES_OPTIONAL.getFirst().textContent;
				
				return author;
			}				
		}
		
		LinkedList<String> authors = new LinkedList<String>();
		
		for (int i=0;i<DCMES_OPTIONAL.size();i++){
			OptionalElement n = DCMES_OPTIONAL.get(i);			
			if (DCMES_OPTIONAL.get(i).nodeName.equalsIgnoreCase("dc:creator")){
				authors.add(n.textContent);
			}
		}
		
		String[] arrayAuthors = new String[authors.size()];
		
		for (int i=0;i<arrayAuthors.length;i++){
			arrayAuthors[i] = authors.get(i);
		}
		
		return arrayAuthors;	
	}
	
}
