import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
	static BufferedImage image;
	static int redval = 0;
	static int blueval = 0;
	static int greenval = 0;
	static int threshold = 180;
	static boolean[][] bwimage;

	static int top = 0;
	static int bottom = 0;
	static int left = 0;
	static int right = 0;

	static int totalheight;
	static int totalheightsection;

	static int totalwidth;
	static int totalwidthsection;

	static int cuts = 8;
	static int cutsv = 4;

	static double se[] = new double[9];
	static double ve[] = new double[5];

	static double sthreshold = 0.1;
	static boolean debug = false;
	
	public static void main(String[] args) throws IOException {
		debug = true;
		run(se, ve, "8", "/Users/bobbydilley/Pictures/test11.jpg");
	}
	public static double run(double se[], double ve[], String tester, String path) throws IOException {
		//public static void run() throws IOException {
			//String tester = "k";
		 //se[1] = 0.5642633228840125;
		 //se[2] = 0.6253918495297806;
		 //se[3] = 0.6269592476489029;
		 //se[4] = 0.6285266457680251;
		 //se[5] = 0.6943573667711599;
		//se[6] = 0.664576802507837;
		 //se[7] = 0.5956112852664577;
		 //se[8] = 0.5909090909090909;

		 //ve[1] = 1.0;
		 //ve[2] = 0.45839874411302983;
		 //ve[3] = 0.6263736263736264;
		 //ve[4] = 0.5376766091051806;
		

		File file = new File(path);
		image = ImageIO.read(file);
		bwimage = new boolean[image.getWidth()][image.getHeight()];

		// Draw the image into the system
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int c = image.getRGB(j, i);
				int red = (c & 0x00ff0000) >> 16;
				int green = (c & 0x0000ff00) >> 8;
				int blue = c & 0x000000ff;

				if (red < redval + threshold && red > redval - threshold) {
					if (green < greenval + threshold && green > greenval - threshold) {
						if (blue < blueval + threshold && blue > blueval - threshold) {
							bwimage[j][i] = true;
						}
					}
				}
			}
		}

		crop();
		//draw(0, image.getHeight());

		totalheight = bottom - top;
		totalwidth = right - left;

		totalheightsection = totalheight / cuts;
		totalwidthsection = totalwidth / cutsv;

		for (int i = 1; i < cuts + 1; i++) {
			// System.out.println("---- Section ----");
			// draw(top + totalheightsection * (i - 1), top + totalheightsection
			// * i);
			// System.out.println("-----------------");
		}

		double[] s = new double[9];
		double[] v = new double[5];

		if(debug) System.out.print(tester + ":");
		for (int i = 1; i < cuts + 1; i++) {
			s[i] = calculate(top + totalheightsection * (i - 1), top + totalheightsection * i);
			if(debug) System.out.print(s[i] + ",");
		}

		for (int i = 1; i < cutsv + 1; i++) {
			v[i] = calculatev(left + totalwidthsection * (i - 1), left + totalwidthsection * i);
			if(debug) System.out.print(v[i] + ",");
		}
		
		if(debug) System.out.println("");

		int check = 0;

		for (int i = 1; i < cuts + 1; i++) {
			//System.out.println(s[i] + " - " + se[i]);
			if (s[i] < (se[i] + sthreshold)) {
				check++;
				//System.out.println("Passed Section Horiz" + i + " Test");
			}
		}

		for (int i = 1; i < cutsv + 1; i++) {
			//System.out.println(v[i] + " - " + ve[i]);
			if (v[i] < (ve[i] + sthreshold)) {
				check++;
				//System.out.println("Passed Section Vert" + i + " Test");
			}
		}

		//System.out.println("This is " + (double)check / (double)12 + " likely an " + tester);
		return (double)check / (double)12;

	}

	public static double calculate(int top, int bottom) {
		int filled = 0;
		int total = 0;
		for (int i = top; i < bottom; i++) {
			for (int j = left; j < right; j++) {
				if (bwimage[j][i]) {
					filled++;
				}
				total++;
			}
		}
		return (double) filled / (double) total;
	}

	public static double calculatev(int left, int right) {
		int filled = 0;
		int total = 0;
		for (int i = top; i < bottom; i++) {
			for (int j = left; j < right; j++) {
				if (bwimage[j][i]) {
					filled++;
				}
				total++;
			}
		}
		return (double) filled / (double) total;
	}

	public static void draw(int top, int bottom) {
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

	public static void crop() {
		for (int i = 0; i < image.getHeight(); i++) {
			boolean found = false;
			for (int j = 0; j < image.getWidth(); j++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if (found) {
				top = i;
				break;
			}
		}

		for (int i = image.getHeight() - 1; i > 0; i--) {
			boolean found = false;
			for (int j = 0; j < image.getWidth(); j++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if (found) {
				bottom = i;
				break;
			}
		}

		for (int j = 0; j < image.getWidth(); j++) {
			boolean found = false;
			for (int i = 0; i < image.getHeight(); i++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if (found) {
				left = j;
				break;
			}
		}

		for (int j = image.getWidth() - 1; j > 0; j--) {
			boolean found = false;
			for (int i = 0; i < image.getHeight(); i++) {
				if (bwimage[j][i]) {
					found = true;
				}
			}
			if (found) {
				right = j;
				break;
			}
		}
	}
}
