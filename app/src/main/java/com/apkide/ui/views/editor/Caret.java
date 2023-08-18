package com.apkide.ui.views.editor;

import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class Caret {
    private final View myView;
    private Timer myTimer;
    private boolean myShowing;
    private boolean myBlinks;
    private final long myBlinkRate;
    
    public Caret(View view) {
        this(view, 500);
    }
    
    public Caret(View view, long blinkRate) {
        myView = view;
        myBlinks = true;
        myShowing = true;
        myBlinkRate = blinkRate;
    }
    
    public void setBlinks(boolean blinks) {
        myBlinks = blinks;
        schedule();
    }
    
    public boolean isBlinks() {
        return myBlinks;
    }
    
    public boolean isShowing() {
        return myShowing;
    }
    
    public void schedule() {
        cancel();
        myShowing = true;
        myTimer = new Timer("Caret", true);
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (myBlinks) {
                    myShowing = !myShowing;
                    myView.postInvalidate();
                }
            }
        }, 1000L, myBlinkRate);
    }
    
    public void cancel() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
        }
        myShowing = false;
    }
}