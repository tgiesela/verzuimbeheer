package com.gieselaar.verzuimbeheer.baseforms;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class InternalFrameEventListener implements InternalFrameListener{
	public InternalFrameEventListener(JDesktopPaneTGI mdiWindow){
	}
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		System.out.println("Frame Opened");
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		System.out.println("Frame closing");
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		System.out.println("Frame closed");
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		System.out.println("Frame Iconified");
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		System.out.println("Frame Deiconified");
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		System.out.println("Frame Activated");
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		System.out.println("Frame Deactivated");
	}

}
