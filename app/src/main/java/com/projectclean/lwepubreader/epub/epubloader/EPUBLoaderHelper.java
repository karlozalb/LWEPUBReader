package com.projectclean.lwepubreader.epub.epubloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.CharStreams;
import com.projectclean.lwepubreader.epub.epubspec.Package;

public class EPUBLoaderHelper {

	private ZipFile mZipHelper;
    private DocumentBuilderFactory mDocumentFactory = DocumentBuilderFactory.newInstance();
 
    private Package ePub;
    private String mPath;
     
    public EPUBLoaderHelper(String pzippath) throws IOException{
         mPath = pzippath;
    	  
         ePub = new Package();
 
         //Zip opening
         mZipHelper = new ZipFile(pzippath);
         Enumeration<? extends ZipEntry> entries = mZipHelper.entries();
 
         //We need to find container.xml to point to our OPF file.       
         while(entries.hasMoreElements()){          
            ZipEntry entry = entries.nextElement();
            if (entry.getName().equalsIgnoreCase("META-INF/container.xml")){
                //We've found our container file.
                parseContainerFile(mZipHelper.getInputStream(entry));
                return;
            }
         }
          
         //If we're here, we haven't found container.xml, so the epub is invalid.        
         throw new IOException();
    }  
    
	public InputStream getInputStream(String pfilename){    	
    	try {
        	ZipEntry entry = getZipEntry(pfilename);
        	if (entry != null) return mZipHelper.getInputStream(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	public String getFileAsAString(String pfilename){
		InputStream is = getInputStream(pfilename);

        try {
            String string = CharStreams.toString(new InputStreamReader(is));

            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
	}
	
     
    /**
     * This method looks for the OPF file, parsing container.xml.
     * @param pcontainerstream
     * @return 
     */
    void parseContainerFile(InputStream pcontainerstream){
        DocumentBuilder builder;
        try {
            builder = mDocumentFactory.newDocumentBuilder();
            //Load and Parse the XML document
            //document contains the complete XML as a Tree.
             
            Document document = builder.parse(pcontainerstream);   
            
            pcontainerstream.close();
             
            //Root node.
            NodeList nodeList = document.getDocumentElement().getChildNodes(); //<container> node children.
             
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);               
                if (node.getNodeName().equalsIgnoreCase("rootfiles")){
                    //root files list, children of "rootfiles" node.
                    NodeList rootFileList = node.getChildNodes(); //<rootfiles> node children.
                     
                    for (int j = 0; j < rootFileList.getLength(); j++) {
                        Node rootFile = rootFileList.item(i);               
                        if (rootFile.hasAttributes() && rootFile.getAttributes().getNamedItem("media-type").getNodeValue().equalsIgnoreCase("application/oebps-package+xml")){
                            //We return the opf file path.
                            parseOPFFile(rootFile.getAttributes().getNamedItem("full-path").getNodeValue());
                            return; //Exit now from this.
                        } 
                    }
                }
            }           
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
             
        //The opf file was not found
        //ERROR!!!!!!!!!!!!!!!!!!!!!!!!!
    }
     
    void parseOPFFile(String popfpath){
        DocumentBuilder builder;        
        try {
            builder = mDocumentFactory.newDocumentBuilder();
             
            Document document = builder.parse(mZipHelper.getInputStream(getZipEntry(popfpath)));
             
            //<package node>
            Node packageNode = document.getDocumentElement();
                         
            parsePackage(packageNode);
             
            NodeList nodeList = document.getDocumentElement().getChildNodes(); //<package> node children.             
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);               
                if (node.getNodeName().equalsIgnoreCase("metadata")){ //metadata node found.
                    processMetadata(node);
                }else if (node.getNodeName().equalsIgnoreCase("manifest")){
                	processManifest(node);
                }else if (node.getNodeName().equalsIgnoreCase("spine")){
                	processSpine(node);
                }else if (node.getNodeName().equalsIgnoreCase("guide")){
                	processGuide(node);
                }else if (node.getNodeName().equalsIgnoreCase("bindings")){
                	processBindings(node);
                }else if (node.getNodeName().equalsIgnoreCase("collection")){
                	processCollection(node);
                }
            }           
             
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    private void parsePackage(Node packageNode) {       
        ePub.parse(packageNode);        
    }
 
    private void processGuide(Node pguidenode) {
    	ePub.getGuide().parse(pguidenode);
    }
 
    private void processSpine(Node pspinenode) {
    	ePub.getSpine().parse(pspinenode);
    }
 
    private void processManifest(Node pmanifestnode) {
    	this.ePub.getManifest().parse(pmanifestnode);
    }
 
    private void processMetadata(Node pmetadatanode) {      
        this.ePub.getMetadata().parse(pmetadatanode);
    }
    
    private void processBindings(Node pbindingsnode) {      
        this.ePub.getBindings().parse(pbindingsnode);
    }
    
    private void processCollection(Node pcollectionsnode) {      
        this.ePub.getCollection().parse(pcollectionsnode);
    }
	
    public Package getPackage(){
    	return ePub;
    }
    
    private ZipEntry getZipEntry(String popfpath){
    	Enumeration<? extends ZipEntry> entries = mZipHelper.entries();
    	
    	//We need to find container.xml to point to our OPF file.       
    	while(entries.hasMoreElements()){          
    		ZipEntry entry = entries.nextElement();    		
    		
    		if (entry.getName().equalsIgnoreCase(popfpath) || entry.getName().endsWith(popfpath)){                
    			return entry;
    		}
    	}
    	
    	return null; //File not found.
    }
    
    public InputStream getBookCover(){
    	InputStream istream = getInputStream("cover.jpg");
    	if (istream == null){
    		istream = getInputStream("cover.jpeg");    		
    		if (istream == null){
        		istream = getInputStream("cover.png");
        	}
    	}
    	return istream;
    }
    
    public String getPath(){
    	return mPath;
    }   
     
}
