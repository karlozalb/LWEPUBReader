package com.projectclean.lwepubreader.epub;

import com.pcg.epubloader.EPUBLoaderHelper;
import com.pcg.epubspec.Manifest;
import com.pcg.epubspec.Spine;

import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 11/12/2015.
 */
public class EPUBBook implements IBook {

    private EPUBLoaderHelper mEPUBLoaderHelper;
    private LinkedList<BookChapter> mBookChapters;

    public EPUBBook(String pepubpath){
        mEPUBLoaderHelper = new EPUBLoaderHelper(pepubpath);

        LinkedList<Spine.ItemRef> itemRefs = mEPUBLoaderHelper.getPackage().getSpine().getItemRefs();
        Manifest bookManifest = mEPUBLoaderHelper.getPackage().getManifest();

        mBookChapters = new LinkedList<BookChapter>();

        for (Spine.ItemRef iref : itemRefs){
            BookChapter chapter = new BookChapter();
            chapter.FILENAME = bookManifest.getItemByIdRef(iref.idref).href;
            mBookChapters.add(chapter);
        }
    }

    public String loadChapter(int pchapter){
        return mEPUBLoaderHelper.getFileAsAString(mBookChapters.get(pchapter).FILENAME);
    }

    public class BookChapter{
        public String FILENAME;
    }
}
