package com.iastate;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main_NGrams {

    public static void main(String[] args) throws Exception {
        File file;
        Writer output;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        File result = new File("Result.txt");
        int n = 3;
        try {
            file = new File("/Users/Joey/code/120_Engl_N-Gram/src/resources/harry-potter.txt");
            output = new BufferedWriter(new FileWriter(result));
            LinkedHashMap<String, Integer> nGramMap = findNGram(n, file);
            int count = 1;
            for(Map.Entry<String, Integer> entry : nGramMap.entrySet()) {
                output.write(count + "\t" + entry.getKey() + "\t" + entry.getValue() + "\n");
                count++;
            }
            List<String> nGramList = new ArrayList<>(nGramMap.keySet());
            int index = random.nextInt(0, nGramList.size());
            String seedNGram = nGramList.get(index);
            System.out.print(seedNGram + " ");
            List<String> possibleNextNGrams = nextNGram(nGramList, seedNGram);
            for(int i = 0; i < 1000; i++) {
                List<String> probabilityList = new ArrayList<>();
                for(String nGram : possibleNextNGrams) {
                    for(int j = 0; j < nGramMap.get(nGram); j++) {
                        probabilityList.add(nGram);
                    }
                }
                String nextNGram = probabilityList.get(random.nextInt(0, probabilityList.size()));
                String[] nGramWords = nextNGram.split(" ");
                System.out.print(nGramWords[nGramWords.length - 1] + " ");
                possibleNextNGrams = nextNGram(nGramList, nextNGram);
            }
            output.close();
        } catch(Exception e) {
            throw new Exception(e);
        }
    }

    static String constructFileString(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String fileString = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) continue;
            if (!fileString.isEmpty()) fileString += " ";
            fileString += line.trim();
        }
        scanner.close();
        return fileString;
    }

    private static LinkedHashMap<String, Integer> findNGram(int n, File file) throws FileNotFoundException {
        Map<String, Integer> countMap = new HashMap<>();
        List<String> nGramList = new ArrayList<>();
        String replaced = constructFileString(file).replaceAll("[\\W&&\\S]", " ");
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
        return countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private static List<String> nextNGram(List<String> nGramList, String previousGram) {
        String previous = previousGram.substring(previousGram.indexOf(" ") + 1);
        nGramList = nGramList.parallelStream()
                .filter(e -> e.startsWith(previous + " "))
                .collect(Collectors.toList());
        return nGramList;
    }
}