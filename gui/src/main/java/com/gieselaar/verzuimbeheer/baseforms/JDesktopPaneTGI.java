package com.gieselaar.verzuimbeheer.baseforms;

import java.awt.Rectangle;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class JDesktopPaneTGI extends JDesktopPane {
	/**
	 * 
	 */

	private static final long serialVersionUID = -5822795174240579514L;
	//private JInternalFrame internalFrame = new JInternalFrame("Internal Frame",true, true, true, true);
	public JDesktopPaneTGI(){
		super();
		this.setDesktopManager(manager);
	}
	private DefaultDesktopManager manager = new DefaultDesktopManager() {
    /**
     * @return 
	 * 
	 */

		private static final long serialVersionUID = 4848609963584222389L;

		/** This moves the <code>JComponent</code> and repaints the damaged areas. */
	    @Override
	    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
	        boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
	        if (!inBounds((JInternalFrame) f, newX, newY, newWidth, newHeight)) return;
	        f.setBounds(newX, newY, newWidth, newHeight);
	        if(didResize) {
	            f.validate();
	        } 
	    }

	    protected boolean inBounds(JInternalFrame f, int newX, int newY, int newWidth, int newHeight) {
	        if (newX < 0 || newY < 0) return false;
	        //if (newX + newWidth > f.getDesktopPane().getWidth()) return false;
	        //if (newY + newHeight > f.getDesktopPane().getHeight()) return false;
	        if (newX + 24 > f.getDesktopPane().getWidth()) return false;
	        if (newY + 24> f.getDesktopPane().getHeight()) return false;
	        return true;
	    }

	};
	public void cascade( JDesktopPane desktopPane, int layer ) {
	    JInternalFrame[] frames = desktopPane.getAllFramesInLayer( layer );
	    if ( frames.length == 0) return;
	 
	    cascade( frames, desktopPane.getBounds(), 24 );
	}
	public void cascade( JDesktopPane desktopPane ) {
	    JInternalFrame[] frames = desktopPane.getAllFrames();
	    if ( frames.length == 0) return;
	 
	    cascade( frames, desktopPane.getBounds(), 24 );
	}
	private void cascade( JInternalFrame[] frames, Rectangle dBounds, int separation ) {
	    //int margin = frames.length*separation + separation;
	    //int width = dBounds.width - margin;
	    //int height = dBounds.height - margin;
	    //int width = dBounds.width;
	    //int height = dBounds.height;
	    for ( int i = 0; i < frames.length; i++) {
	        frames[i].setBounds( separation + dBounds.x + i*separation,
	                             separation + dBounds.y + i*separation,
	                             frames[i].getWidth(), frames[i].getHeight());
	    }
	}

}
