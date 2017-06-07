package com.gieselaar.verzuimbeheer.utils;

import java.awt.AWTEvent;
import java.awt.EventQueue;

/*
 * Deze class wordt gebruikt om RuntimeExceptions die in een Swing
 * application optreden op een centrale plek af te vangen. Hierdoor
 * verdwijnen de meldingen niet meer in system.out
 */

public class EventQueueProxy extends EventQueue {
	protected void dispatchEvent(AWTEvent newEvent) {
        try {
            super.dispatchEvent(newEvent);
        } catch (Exception t) {
            t.printStackTrace();
            String message = t.getMessage();
 
            if (message == null || message.length() == 0) {
                message = "Fatal: " + t.getClass();
                ExceptionLogger.ProcessException(t, null, message);
            }
            else
            	ExceptionLogger.ProcessException(t, null);
        }
    }
}
