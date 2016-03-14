package com.projectclean.lwepubreader.epub.epubspec;

import com.projectclean.lwepubreader.epub.exceptions.EPUBException;

public interface IVerificable {

	public boolean isValid() throws EPUBException;
	
}
