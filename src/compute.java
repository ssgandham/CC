import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Compute {
	static Map<String, Integer> map_states = new HashMap<>();
	static Map<String, Integer> map_position = new HashMap<>();
	static int certified = 0;

	// Method for finding the count from input file
	public static void find_count(String path) {
		String line = "", cvsSplitBy = ";";

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			while ((line = br.readLine()) != null) {
				// The words in the line of input file are split based on
				// semi-colon
				String[] arr = line.split(cvsSplitBy);

				// Check to see if the all the columns are present in input file
				if (arr.length < 52)
					continue;

				arr[24] = arr[24].replaceAll("\"", "");

				// The data is skipped if its not certified
				if (!arr[2].toLowerCase().equals("certified"))
					continue;

				// The data is not taken into consideration of H1B, H-1B1 or E-3
				// is not present
				if (arr[5].toUpperCase().indexOf("H-1B") >= 0 || arr[5].toUpperCase().indexOf("H-1B1") >= 0
						|| arr[5].toUpperCase().indexOf("E-3") >= 0) {

					// If the data is certified then the certified count is
					// increased and the relevant data is put into map
					++certified;

					map_states.put(arr[50].toUpperCase(), map_states.getOrDefault(arr[50].toUpperCase(), 0) + 1);
					map_position.put(arr[24].toUpperCase(), map_position.getOrDefault(arr[24].toUpperCase(), 0) + 1);

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method for writing the data to output files
	public static void write_to_file(String path_occupations, String path_states) {
		int print_line_size = 10;
		String str_states = "TOP_STATES;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE",
				str_occupations = "TOP_OCCUPATIONS;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE";

		DecimalFormat df = new DecimalFormat("#.0");
		try {

			BufferedWriter writer_occupations = new BufferedWriter(new FileWriter(path_occupations));
			BufferedWriter writer_states = new BufferedWriter(new FileWriter(path_states));

			// PriorityQueue for states and occupations with comparator function
			// which can sort the values based on the frequency of values
			PriorityQueue<Map.Entry<String, Integer>> pq_states = new PriorityQueue<>(
					new Comparator<Map.Entry<String, Integer>>() {
						@Override
						public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
							return a.getValue() == b.getValue() ? a.getKey().compareTo(b.getKey())
									: b.getValue() - a.getValue();
						}
					});

			PriorityQueue<Map.Entry<String, Integer>> pq_position = new PriorityQueue<>(
					new Comparator<Map.Entry<String, Integer>>() {
						public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
							return a.getValue() == b.getValue() ? a.getKey().compareTo(b.getKey())
									: b.getValue() - a.getValue();
						}
					});

			// Map data is added to PriorityQueue so that the keys can be sorted
			// based on the frequency count
			pq_states.addAll(map_states.entrySet());
			pq_position.addAll(map_position.entrySet());

			// The top states data is written to states file
			writer_states.write(str_states);
			writer_states.newLine();

			while (!pq_states.isEmpty() && --print_line_size >= 0) {
				String output = pq_states.peek().getKey() + ";" + pq_states.peek().getValue() + ";"
						+ df.format((((double) pq_states.poll().getValue()) / certified) * 100) + "%";
				writer_states.write(output);
				writer_states.newLine();

			}

			// The top occupations data is writtent to occupations file
			print_line_size = 10;
			writer_occupations.write(str_occupations);
			writer_occupations.newLine();

			while (!pq_position.isEmpty() && --print_line_size >= 0) {
				String output = pq_position.peek().getKey() + ";" + pq_position.peek().getValue() + ";"
						+ df.format(((pq_position.poll().getValue() * 1.0) / certified) * 100) + "%";
				writer_occupations.write(output);
				writer_occupations.newLine();

			}

			// The files are closed once the data is written
			writer_states.close();
			writer_occupations.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// Method for finding the count from input file
		find_count(args[0]);

		// Method for writing the data to output files
		write_to_file(args[1], args[2]);

	}

}
