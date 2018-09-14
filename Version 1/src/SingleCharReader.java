import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SingleCharReader {
	public static void main(String[] args) throws IOException {
		String path = "/Users/bobbydilley/Pictures/test10.jpg";
		
		String everything;
		BufferedReader br = new BufferedReader(new FileReader("src/charactersaves"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        line = br.readLine();
		    }
		    everything = sb.toString();
		    //System.out.println(everything);
		} finally {
		    br.close();
		}
		String bob[] = everything.split(";");
		double biggestamount = 0;
		String biggestchar = "";
		for(int i = 0 ; i < bob.length ; i++) {
			if(bob[i].indexOf(":") > -1) {
				String lel[] = bob[i].split(":");
				//System.out.println("Testing: " + lel[0]);
				String nums[] = lel[1].split(",");
				double se[] = new double[9];
				double ve[] = new double[5];
				for(int j = 0 ; j < 8 ; j++) {
					//System.out.println("Adding se" + (j+1) + " > " + nums[j]);
					se[j+1] = Double.parseDouble(nums[j]);
				}
				for(int j = 8 ; j < 12 ; j++) {
					//System.out.println("Adding ve" + (j+1-8) + " > " + nums[j]);
					ve[j+1-8] = Double.parseDouble(nums[j]);
				}	
				double amount = Main.run(se, ve, lel[0], path);
				//System.out.println("Chance of " + lel[0] + " is " + amount);
				if(amount > biggestamount) {
					biggestamount = amount;
					biggestchar = lel[0];
				}
				
			}
			
		}
		System.out.println("This is a " + biggestchar);
	}
}
