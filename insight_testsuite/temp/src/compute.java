import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class compute {
	static Map<String,Integer> map_states = new HashMap<>();
	static Map<String,Integer> map_position = new HashMap<>();
	static int certified=0;
	static long line_count=0;
	public static void find(String path) {
		String line = "",cvsSplitBy = ";";

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			while ((line = br.readLine()) != null) {
				line_count++;
//				if(line_count==1)
//					continue;
				String[] arr = line.split(cvsSplitBy);
				// System.out.println(arr.length);
				if(arr.length<52)
					continue;
				arr[24]=arr[24].replaceAll("\"","");
				if(!arr[2].toLowerCase().equals("certified"))
					continue;
				if(arr[5].toUpperCase().indexOf("H-1B")>=0 || arr[5].toUpperCase().indexOf("H-1B1")>=0
				|| arr[5].toUpperCase().indexOf("E-3")>=0){

					++certified;
					// System.out.println(line);
					map_states.put(arr[50].toUpperCase(),map_states.getOrDefault(arr[50].toUpperCase(),0)+1);
					map_position.put(arr[24].toUpperCase(),map_position.getOrDefault(arr[24].toUpperCase(),0)+1);

				// System.out.println(arr[2] + " : " + arr[5] + " : " 	+ arr[24] + " : " +arr[50] + " : " + arr[5].toUpperCase().indexOf("H-1B"));
			}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void find_top(String path_occupations, String path_states){
		line_count--;
		int print_line_size=10;
//		System.out.println(certified);
		DecimalFormat df = new DecimalFormat("#.0");
		try {
			BufferedWriter writer_occupations = new BufferedWriter(new FileWriter(path_occupations));
			BufferedWriter writer_states = new BufferedWriter(new FileWriter(path_states));


		PriorityQueue<Map.Entry<String,Integer>> pq_states = new PriorityQueue<>(
		new Comparator<Map.Entry<String,Integer>>(){
			@Override
			public int compare(Map.Entry<String,Integer> a, Map.Entry<String,Integer> b){
				return a.getValue()==b.getValue()?a.getKey().compareTo(b.getKey()):b.getValue()-a.getValue();
			}
		}
		);

		PriorityQueue<Map.Entry<String, Integer>> pq_position = new PriorityQueue<>(
			new Comparator<Map.Entry<String, Integer>>(){
				public int compare(Map.Entry<String,Integer> a, Map.Entry<String, Integer> b){
					return a.getValue()==b.getValue()?a.getKey().compareTo(b.getKey()):b.getValue()-a.getValue();
				}
			}
		);

		pq_states.addAll(map_states.entrySet());
		pq_position.addAll(map_position.entrySet());
		writer_states.write("TOP_STATES;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE");
		writer_states.newLine();
		while(!pq_states.isEmpty() && --print_line_size>=0){
			String output = pq_states.peek().getKey() + ";" + pq_states.peek().getValue() + ";"+ df.format((((double)pq_states.poll().getValue())/certified)*100)+"%";
			writer_states.write(output);
			writer_states.newLine();
			// System.out.println(output);
		}

		print_line_size=10;

		writer_occupations.write("TOP_OCCUPATIONS;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE");
		writer_occupations.newLine();
		while(!pq_position.isEmpty() && --print_line_size>=0){
			String output = pq_position.peek().getKey() + ";" + pq_position.peek().getValue() + ";" + df.format(((pq_position.poll().getValue()*1.0)/certified)*100) + "%";
			writer_occupations.write(output);
			writer_occupations.newLine();
			// System.out.println(output);
		}
		 writer_states.close();
		 writer_occupations.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// System.out.println(args[0]);
		find(args[0]);
		find_top(args[1],args[2]);
//		System.out.println(map_states);
//		System.out.println(map_position);
	}

}
