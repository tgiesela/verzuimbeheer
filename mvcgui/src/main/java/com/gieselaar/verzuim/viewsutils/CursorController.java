package com.gieselaar.verzuim.viewsutils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class CursorController {
    public static final Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);
    public static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final int delay = 500; // in milliseconds

    private CursorController() {}
    
    public static ActionListener createListener(final Component component, final ActionListener mainActionListener) {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        //System.out.println("Enabling busy cursor");
                    	component.setCursor(busyCursor);
                    }
                };
                Timer timer = new Timer(); 
                
                try {   
                    timer.schedule(timerTask, delay);
                    //System.out.println("Invoking actionListener");
                    mainActionListener.actionPerformed(ae);
                } finally {
                    timer.cancel();
                    //System.out.println("Back to standard cursor");
                    component.setCursor(defaultCursor);
                }
            }

        };
        return actionListener;
    }
    public static MouseListener createListener(final Component component, final MouseAdapter mainActionListener) {
			
    	MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                Timer timer = new Timer(); 
                try {   
                    timer.schedule(getTimerTask(), delay);
                    mainActionListener.mouseClicked(e);
                } finally {
                    timer.cancel();
                    component.setCursor(defaultCursor);
                }
			}
			private TimerTask getTimerTask(){
				TimerTask timerTask = new TimerTask() {
					public void run() {
						component.setCursor(busyCursor);
					}
				};
				return timerTask;
			}
        };
        return mouseAdapter;
    }
    public static Action createListener(final Component component, final AbstractAction mainActionListener) {
		
    	AbstractAction abstractAdapter = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private TimerTask getTimerTask(){
				TimerTask timerTask = new TimerTask() {
					public void run() {
						component.setCursor(busyCursor);
					}
				};
				return timerTask;
			}
			@Override
			public void actionPerformed(ActionEvent e) {
                Timer timer = new Timer(); 
                try {   
                    timer.schedule(getTimerTask(), delay);
                    mainActionListener.actionPerformed(e);
                } finally {
                    timer.cancel();
                    component.setCursor(defaultCursor);
                }
			}
        };
        return abstractAdapter;
    }
}