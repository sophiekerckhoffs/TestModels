
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Reader {
	
	public static void main(String[] args) {
		
		String inputFile = "/home/sophie/Downloads/TestCSV.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitby = ";";
		String[] hourlyData = new String[5];
		double[] hourlyDataDouble = new double[hourlyData.length];
		
		try{
			
			br = new BufferedReader(new FileReader(inputFile));
			
			while((line = br.readLine()) != null){
				hourlyData = line.split(cvsSplitby);
				
			}
					
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			if (br != null) {
				try {
					br.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				
			}
		}
		
		for(int i = 0; i < hourlyDataDouble.length; i++){
			hourlyDataDouble[i] = Double.parseDouble(hourlyData[i]);
		}
		System.out.println(Arrays.toString(hourlyDataDouble));
	
	}
	

}
