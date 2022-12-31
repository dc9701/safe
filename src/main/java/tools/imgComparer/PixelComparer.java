package tools.imgComparer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;

import javax.imageio.ImageIO;

/*
 * Compares images in 2 directories to see if they are the same or not.
 * 
 */



public class PixelComparer {

	/**
	 * @param args
	 */
	static String masterDirLoc;
	static String candidateLoc;
	static int maxDiff;
	static int tolerance = 6;
	//amount to cut image by.
	static int yEnd = 0;
	static int yStart = 0;
	static int xEnd = 0;
	static int xStart = 0;
	static int pixelsWrongAboveYcut = 0;
	static int maxWrongPixelsAboveYcut = 0;
	static int pixelsWrongBelowYcut = 0;
	static int maxWrongPixelsBelowYcut = 0;
	
	
	public static boolean imgCpr(String masterImg, String candidateImg,
							     int maxdifference, int tol, int yStartPxl, int xStartPxl, int yEndPxl, int xEndPxl) {

		maxDiff = maxdifference;
		tolerance =tol;
		yStart = yStartPxl;
		yEnd = yEndPxl;
		xStart = xStartPxl;
		xEnd = xEndPxl;
		
		return compareImages(masterImg,candidateImg);
	} 

	//returns true if two images are identical by a pixel by pixel comparison.
	private static boolean compareImages(String masterImageLoc, String candidateImageLoc){	
		try {
			BufferedImage bi1,bi2;
			File masterFil = new File(masterImageLoc);
			File candidateFil = new File(candidateImageLoc);
			
			//convert .tif images to jpg
			//String jpg1 = convertImage(file1);
			//String jpg2 = convertImage(file2);
			//make sure file exists
			if(!masterFil.isFile() || !candidateFil.isFile()){
				System.out.println("not full");
				return true;
			}
						
			 //ensure picture is jpg or png
			if(masterFil.getName().contains(".jpg") || candidateFil.getName().contains(".jpg") ||
					masterFil.getName().contains(".JPG") || candidateFil.getName().contains(".JPG") ||
					masterFil.getName().contains(".png") || candidateFil.getName().contains(".png") ||
					masterFil.getName().contains(".PNG") || candidateFil.getName().contains(".PNG")){
						
				bi1 = ImageIO.read(masterFil);
				bi2 = ImageIO.read(candidateFil);
			
				int maxWidth = (xEnd != 0)?xEnd:bi1.getWidth();
				int minWidth = xStart;
				
				int minHeight = yStart;//bi1.getMinY();
				int maxHeight = (yEnd != 0)?yEnd:bi1.getHeight();
				
				System.out.println("Value of min Width = " + minWidth);
				System.out.println("Value of max Width = " + maxWidth);				
				System.out.println("Value of min Height = " + minHeight);
				System.out.println("Value of max Height = " + maxHeight);	

				if ( (bi1.getWidth() != bi2.getWidth()) || (bi1.getHeight() != bi2.getHeight()) ) {
					System.out.println("You dun Goofd");
					return false;
				}
				
				int wrongPixels = 0;
				//loop over every pixel
				for(int i = minWidth; i < maxWidth;i++){
					for(int j = minHeight; j < maxHeight;j++){
						int pixelMaster = bi1.getRGB(i, j);
						int pixelCompare = bi2.getRGB(i, j);
						
						
						int totalDiff = pixelDiff(pixelMaster,pixelCompare);
									
						if(totalDiff > tolerance){
							wrongPixels++;

						}

					}//end of inner for
					
				}//end of outer for
			
				if (wrongPixels > maxDiff)
					return false;
				else
					return true;
				}
			else{
				System.out.println("pic is not jpg.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
		
	}
	
	
	
	
	//returns the difference in value between 2 pixels.
	private static int pixelDiff(int pix1, int pix2){
		//get RGB values
		int redMaster = (pix1 >> 16) & 0xFF;
		int greenMaster = (pix1 >> 8) & 0xFF;
		int blueMaster = pix1 & 0xFF;

		int redCompare = (pix2 >> 16) & 0xFF;
		int greenCompare = (pix2 >> 8) & 0xFF;
		int blueCompare = pix2 & 0xFF;
		//find how close pixels are to each other.
		int redDiff = Math.abs(redMaster - redCompare);
		int greenDiff = Math.abs(greenMaster - greenCompare);
		int blueDiff = Math.abs(blueMaster - blueCompare);
		int totalDiff = redDiff+greenDiff+blueDiff;
		return totalDiff;
	}
	

		
	}

