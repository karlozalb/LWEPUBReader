package com.projectclean.lwepubreader.epub;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public interface IProgressListener {

    void onProgressFinished();
    void onProgressChanged(int pprogressdelta);

}
