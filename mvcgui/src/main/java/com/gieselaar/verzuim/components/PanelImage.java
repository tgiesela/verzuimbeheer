package com.gieselaar.verzuim.components;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.gieselaar.verzuim.utils.ExceptionLogger;


public class PanelImage extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3251293319962241421L;
	private BufferedImage originalImage = null;
	private BufferedImage resizedImage = null;
	private int IMG_WIDTH;
	private int IMG_HEIGHT;
	private BufferedImage resizeImage(BufferedImage originalImage, int type){
	
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		 
		return resizedImage;
	}
		 
	@SuppressWarnings("unused")
	private BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){
		 
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);
		 
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		 
		return resizedImage;
	}
	public PanelImage(URL imageURL){
		try {
			originalImage = ImageIO.read(imageURL);
		} catch (IOException e) {
        	ExceptionLogger.ProcessException(e,this);
		}
	}
	public PanelImage(String imagefilename){
		URL imageURL = getClass().getResource(imagefilename);
		try {
			originalImage = ImageIO.read(imageURL);
		} catch (IOException e) {
        	ExceptionLogger.ProcessException(e,this);
		}
	}
	@Override
	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x,y,width,height);
		IMG_WIDTH = this.getWidth();
		IMG_HEIGHT = this.getHeight();
	}
	@Override
	public void paint(Graphics g){
		int type;
		if (originalImage == null)
			;
		else
		{
			type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			resizedImage = resizeImage(originalImage, type);
			g.drawImage(resizedImage, 0, 0, null);
		}
	}
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(resizedImage, 0, 0, null);
	}
}
