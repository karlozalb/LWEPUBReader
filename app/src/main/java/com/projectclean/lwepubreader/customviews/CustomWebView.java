package com.projectclean.lwepubreader.customviews;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ShareActionProvider;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.WebView;

import com.projectclean.lwepubreader.R;

import java.lang.reflect.Method;

/**
 * Created by Carlos Albaladejo Pérez on 18/01/2016.
 */
public class CustomWebView extends WebView {
    private Context context;
    private ShareActionProvider mShareActionProvider;

    // override all other constructor to avoid crash
    public CustomWebView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    // setting custom action bar
    private ActionMode mActionMode;
    private ActionMode.Callback mSelectActionModeCallback;
    private GestureDetector mDetector;

    // this will over ride the default action bar on long press
    /*@Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String name = callback.getClass().toString();
            if (name.contains("SelectActionModeCallback")) {
                mSelectActionModeCallback = callback;
                mDetector = new GestureDetector(context,
                        new CustomGestureListener());
            }
        }
        CustomActionModeCallback mActionModeCallback = new CustomActionModeCallback();
        return parent.startActionModeForChild(this, mActionModeCallback);
    }*/

    private class CustomActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;

            mode.getMenuInflater().inflate(R.menu.action_menu, menu);
            //MenuItem shareItem = menu.findItem(R.id.action_share);
            //MenuItemCompat.setShowAsAction(shareItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            android.support.v7.widget.ShareActionProvider actionprov = new android.support.v7.widget.ShareActionProvider((AppCompatActivity)context);
            //MenuItemCompat.setActionProvider(shareItem, actionprov);
            //android.support.v7.widget.ShareActionProvider test =  ( android.support.v7.widget.ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "ActionBarCompat is Awesome! Support Lib v7 #Xamarin");
            //test.setShareIntent (intent);

            /*MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_menu, menu);

            MenuItem shareItem = menu.findItem(R.id.action_share);

            mShareActionProvider = new ShareActionProvider(context);
            MenuItemCompat.setActionProvider(shareItem, mShareActionProvider);

            Intent myShareIntent = new Intent(Intent.ACTION_SEND);
            myShareIntent.setType("text/html");
            myShareIntent.putExtra(Intent.EXTRA_STREAM, "prueba de mono pequeño.");
            mShareActionProvider.setShareIntent(myShareIntent);*/

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.translate_action_btn:
                    getSelectedData();
                    //selectAndCopyText();
                    mode.finish();
                    return true;
                /*case R.id.action_share:
                    onShareAction();
                    mode.finish();
                    return true;*/
                default:
                    mode.finish();
                    return true;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                clearFocus();
            }else{
                if (mSelectActionModeCallback != null) {
                    mSelectActionModeCallback.onDestroyActionMode(mode);
                }
                mActionMode = null;
            }
        }

        private void onShareAction(){
            // Create the share Intent
            String yourShareText = "mongol kebab";
            Intent shareIntent = ShareCompat.IntentBuilder.from((AppCompatActivity)context).setType("text/plain").setText(yourShareText).getIntent();
            // Set the share Intent
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
    }
    private void getSelectedData(){

        String js= "getSelectedText();";
        // calling the js function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:"+js, null);
        }else{
            loadUrl("javascript:" + js);
        }
    }

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mActionMode != null) {
                mActionMode.finish();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Send the event to our gesture detector
        // If it is implemented, there will be a return value
        if(mDetector !=null)
            mDetector.onTouchEvent(event);
        // If the detected gesture is unimplemented, send it to the superclass
        return super.onTouchEvent(event);
    }

    public void selectAndCopyText() {
        try {
            Method m = WebView.class.getMethod("emulateShiftHeld", null);
            m.invoke(this, null);
        } catch (Exception e) {
            e.printStackTrace();
            // fallback
            KeyEvent shiftPressEvent = new KeyEvent(0,0,
                    KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_SHIFT_LEFT,0,0);
            shiftPressEvent.dispatch(this);
        }
    }
}
