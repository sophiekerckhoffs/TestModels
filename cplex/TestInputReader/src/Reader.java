
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;



public class Reader {
	
	public static void main(String[] args) {
		
		String inputFile = "/home/sophie/Downloads/TestCSV.csv";
		BufferedReader br = null;
		String line = "";
		//String cvsSplitby = ",";
		ArrayList<String> hourlyData = new ArrayList<String>();
		
		
		try{
			
			br = new BufferedReader(new FileReader(inputFile));
			
			while((line = br.readLine()) != null){
				hourlyData.add(line);
				//hourlyData = line.split(cvsSplitby);
				
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
		
		double[] hourlyDataDouble = new double[hourlyData.size()];
		for(int i = 0; i < hourlyDataDouble.length; i++){
			hourlyDataDouble[i] = Double.parseDouble(hourlyData.get(i));
		}
		System.out.println(Arrays.toString(hourlyDataDouble));
	
	}
	

}
