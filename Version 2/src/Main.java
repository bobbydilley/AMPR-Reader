import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Main implements Runnable {
	private String filePath;
	private File file;
	private BufferedImage image;
	private int threshold = 10;
	private int top = 0;
	private int left = 0;
	private int bottom = 0;
	private int right = 0;
	private int chopRatio = 5;
	
	float[] b = {(float) 0.75615764, (float) 0.87931037, (float) 0.51724136, (float) 0.68431854, (float) 0.886289, (float) 0.90061474, (float) 0.5436475, (float) 0.83729506, (float) 0.5471311, (float) 0.8692623};
	float[] d = {(float) 0.7081281, (float) 0.85857964, (float) 0.33990148, (float) 0.51744664, (float) 0.92528737, (float) 0.87357724, (float) 0.5382114, (float) 0.5365854, (float) 0.5369919, (float) 0.8611789};
	float[] n5 = {(float) 0.46859607, (float) 0.73583746, (float) 0.5123153, (float) 0.5917488, (float) 0.70730704, (float) 0.7652439, (float) 0.27662602, (float) 0.8546748, (float) 0.27439025, (float) 0.8105691};
	float[] n1 = {(float) 0.8529205, (float) 0.9950739, (float) 0.9950739, (float) 0.9950739, (float) 0.94714284, (float) 0.9714286, (float) 0.9714286, (float) 0.9714286, (float) 0.9714286, (float) 0.0};
	float[] s = {(float) 0.58066505, (float) 0.6520936, (float) 0.51662564, (float) 0.61206895, (float) 0.65353036, (float) 0.824187, (float) 0.31930894, (float) 0.6876016, (float) 0.31910568, (float) 0.82987803};
	float[] m = {(float) 0.9536125, (float) 0.69827586, (float) 0.4827586, (float) 0.6330049, (float) 0.9950739, (float) 0.64715445, (float) 0.89715445, (float) 0.9595528, (float) 0.74593496, (float) 0.5585366};
	float[] r = {(float) 0.9536125, (float) 0.6159688, (float) 0.4070197, (float) 0.7692939, (float) 0.7112069, (float) 0.877459, (float) 0.542418, (float) 0.89180326, (float) 0.5633197, (float) 0.5643443};
	float[] p = {(float) 0.9375, (float) 0.58382934, (float) 0.33333334, (float) 0.4295635, (float) 0.869, (float) 0.5285, (float) 0.8555, (float) 0.2625, (float) 0.2625, (float) 0.0};
	float[] n9 = {(float) 0.5992063, (float) 0.64136904, (float) 0.5, (float) 0.6577381, (float) 0.9082341, (float) 0.81876546, (float) 0.5269136, (float) 0.87506175, (float) 0.2765432, (float) 0.814321};
	float[] c = {(float) 0.82886904, (float) 0.60218257, (float) 0.33333334, (float) 0.39136904, (float) 0.8235, (float) 0.301, (float) 0.2625, (float) 0.2825, (float) 0.8255, (float) 0.0};
	float[] n = {(float) 0.9375, (float) 0.6443452, (float) 0.36954364, (float) 0.52728176, (float) 1.0, (float) 0.6014815, (float) 0.745679, (float) 0.79209876, (float) 0.7496296, (float) 0.6148148};
	
	public Main(String filePath) {
		this.filePath = filePath;
		this.file = new File(this.filePath);
		try {
			this.image = ImageIO.read(this.file);
		} catch (IOException e) {
			System.err.println("Failed to read in the file");
		}
	}
	
	@Override
	public void run() {
		boolean[][] bwimage = getImage();
		crop(bwimage);
		cropletter(bwimage);
	}
	
	public void cropletter(boolean[][] bwimage) {	
		int letterEnd = this.right;
		int letterStart = this.left;
		boolean notBlank = true;
		for(int i = this.left ; i < this.right ; i++) {
			boolean blank = true;
			for(int j = this.top ; j < this.bottom ; j++) {
				if(bwimage[i][j] == true) {
					blank = false;
					notBlank = true;
				}
			}
			if(blank) {
				letterEnd = i;
				if(notBlank) {
					checkLetter(bwimage, letterStart, letterEnd);
				}
				letterStart = letterEnd;
				notBlank = false;
			}
		}
		
		checkLetter(bwimage, letterEnd, this.right);
	}
	
	public float getDifference(float[] key1, float[] key2) {
		float totalDifference = key1.length;
		for(int i = 0 ; i < key1.length ; i++) {
				if(key1[i] > key2[i] - 0.1 && key1[i] <= key2[i] + 0.1)
					totalDifference -= 1;
		}
		return totalDifference;
	}
	
	public float[] getLetterKey(boolean[][] bwimage, int start, int end) {
		float key[] = new float[this.chopRatio * 2];
		int count = 0;
		
		int cut = Math.floorDiv((end - start), this.chopRatio);
		for(int i = start ; i < end - cut ; i+= cut) {
			int filled = 0;
			for(int j = i ; j < i + cut ; j++) {
				for(int p = this.top ; p < this.bottom ; p++) {
					if(bwimage[j][p] == true) {
						filled++;
					}
				}
			}
			key[count] = ((float) filled / (cut * (this.bottom - this.top)));
			count++;
		}
		
		cut = Math.floorDiv((this.bottom - this.top), this.chopRatio);
		for(int i = this.top ; i < this.bottom - cut ; i+= cut) {
			int filled = 0;
			for(int j = i ; j < i + cut ; j++) {
				for(int p = start ; p < end ; p++) {
					if(bwimage[p][j] == true) {
						filled++;
					}
				}
			}
			key[count] = ((float) filled / (cut * (end - start)));
			count++;
		}
		
		return key;
	}
	
	public void printKey(float[] key) {
		System.out.print("{");
		for(int i = 0 ; i < key.length ; i++) {
			System.out.print("(float) " + key[i] + ", ");
		}
		System.out.println("}");
	}
	
	
	public void checkLetter(boolean[][] bwimage, int start, int end) {
		float[] key = getLetterKey(bwimage, start, end);
		
		printKey(key);
		//display(bwimage, this.top, this.bottom, start, end);
		
		float min = 10000;
		String digit = "-";
		
		if(getDifference(key, b) < min) {
			min = getDifference(key, b);
			digit = "B";
		}
		
		if(getDifference(key, d) < min) {
			min = getDifference(key, d);
			digit = "D";
		}
		
		if(getDifference(key, n5) < min) {
			min = getDifference(key, n5);
			digit = "5";
		}
		
		if(getDifference(key, n1) < min) {
			min = getDifference(key, n1);
			digit = "1";
		}
		
		if(getDifference(key, s) < min) {
			min = getDifference(key, s);
			digit = "S";
		}
		
		if(getDifference(key, m) < min) {
			min = getDifference(key, m);
			digit = "M";
		}
		
		if(getDifference(key, r) < min) {
			min = getDifference(key, r);
			digit = "R";
		}
		
		if(getDifference(key, p) < min) {
			min = getDifference(key, p);
			digit = "P";
		}

		
		if(getDifference(key, n9) < min) {
			min = getDifference(key, n9);
			digit = "9";
		}

		
		if(getDifference(key, c) < min) {
			min = getDifference(key, c);
			digit = "C";
		}

		
		if(getDifference(key, n) < min) {
			min = getDifference(key, n);
			digit = "N";
		}


		
		System.out.println("Letter is a: " + digit);
	}
	
	public void display(boolean[][] bwimage, int top, int bottom, int left, int right) {
		for (int i = top; i < bottom; i++) {
			for (int j = left; j < right; j++) {
				if (bwimage[j][i]) {
					System.out.print("X");
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}
	
	public boolean[][] getImage() {
		boolean[][] bwimage = new boolean[this.image.getWidth()][this.image.getHeight()];
		for (int i = 0; i < this.image.getHeight(); i++) {
			for (int j = 0; j < this.image.getWidth(); j++) {
				int c = this.image.getRGB(j, i);
				int red = (c & 0x00ff0000) >> 16;
				int green = (c & 0x0000ff00) >> 8;
				int blue = c & 0x000000ff;
				if (red < threshold && green < threshold && blue < threshold) {
						bwimage[j][i] = true;	
				}
			}
		}
		return bwimage;
	}
	
	public void crop(boolean[][] bwimage) {
		for(int i = 0 ; i < image.getHeight() ; i++) {
			boolean found = false;
			for(int j = 0 ; j < image.getWidth() ; j++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if(found) {
				this.top = i;
				break;
			}
		}
		
		for(int i = image.getHeight() - 1; i > 0; i--) {
			boolean found = false;
			for(int j = 0 ; j < image.getWidth() ; j++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if(found) {
				this.bottom = i;
				break;
			}
		}
		
		for(int j = 0; j < image.getWidth(); j++) {
			boolean found = false;
			for(int i = 0; i < image.getHeight(); i++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if(found) {
				this.left = j;
				break;
			}
		}
		
		for(int j = image.getWidth() - 1; j > 0; j--) {
			boolean found = false;
			for(int i = 0; i < image.getHeight(); i++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if(found) {
				this.right = j;
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		new Main(args[0]).run();
	}
}
