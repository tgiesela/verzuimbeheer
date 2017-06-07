package com.gieselaar.verzuimbeheer.timers;

import javax.ejb.Stateless;

@Stateless
public class LoginSessionCleanupTimer {

    /**
     * Default constructor. 
     */
    public LoginSessionCleanupTimer() {
    }

//	@Schedule(second="0", minute="0", hour="*/1", dayOfWeek="*",
//      dayOfMonth="*", month="*", year="*", info="MyTimer")
//    private void scheduledTimeout(final Timer t) {
//        System.out.println("@Schedule called at: " + new java.util.Date());
//    }
    
}