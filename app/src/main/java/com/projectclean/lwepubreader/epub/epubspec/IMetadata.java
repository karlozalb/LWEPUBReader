package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

public interface IMetadata {
	
	public String getBookTitle() throws EPUBException;
	public String[] getBookAuthor() throws EPUBException;

}
