package com.iastate;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        File file;
        File result = new File("Result.txt");
        Writer output = null;
        try {
            file = new File("/Users/Joey/code/120_Engl_N-Gram/src/resources/sample.txt");
            output = new BufferedWriter(new FileWriter(result));
            int count = 1;
            for(Map.Entry<String, Integer> entry : findNGram(5, file).entrySet()) {
                output.write(count + "\t" + entry.getKey() + "\t" + entry.getValue() + "\n");
                count++;
            }
            output.close();
        } catch(Exception e) {
            System.out.println("e = " + e);
        }
    }

    private static LinkedHashMap<String, Integer> findNGram(int n, File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        Map<String, Integer> countMap = new HashMap<>();
        List<String> nGramList = new ArrayList<>();
        String fileString = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            fileString += " " + line.trim();
        }
        String replaced = fileString.replaceAll("[\\W&&\\S]", " ");
        List<String> split = new ArrayList<>(Arrays.asList(replaced.split("\\s+")));
        for (String s : split) {
            nGramList.add(s.toUpperCase());
            if(nGramList.size() == n) {
                String nGram = String.join(" ", nGramList);
                if(countMap.containsKey(nGram)) {
                    countMap.put(nGram, (countMap.get(nGram) + 1));
                }
                else {
                    countMap.put(nGram, 1);
                }
                nGramList.remove(0);
            }
        }
        LinkedHashMap<String, Integer> sortedMap = countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        scanner.close();
        return sortedMap;
    }
}