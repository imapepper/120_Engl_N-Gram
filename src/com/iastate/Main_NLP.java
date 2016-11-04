package com.iastate;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main_NLP {

    private final static File file = new File("C:/code/120_Engl_N-Gram/src/resources/hp1.tagged.txt");
    private final static Pattern characterQuote = Pattern.compile("``_``((?!''_'').)+''_''");
//    private final static Pattern properNounPattern = Pattern.compile("\\b(\\S+)_(NNP|NNPS)\\b");
    private final static Pattern posTag = Pattern.compile("(\\S+)_(\\S+)");
    private static Matcher matcher;
    private static int offset = 0;

    public static void main(String[] args) throws Exception {
        String fileString = Main_NGrams.constructFileString(file);
        matcher = characterQuote.matcher(fileString);
        List<String> properNounTags = new ArrayList<>(Arrays.asList("NNP", "NNPS"));
        List<String> verbTags = new ArrayList<>(Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
        List<String> characterNames = new ArrayList<>();

        while(matcher.find(offset)) {
            int quoteOffset = matcher.end();
            offset = quoteOffset;
            matcher.usePattern(posTag);
            String characterName = "";
            String pos = inspectNextPosTag(verbTags);
            while(!pos.isEmpty()) {
                String name = inspectNextPosTag(properNounTags);
                if(!name.isEmpty()) characterName += name + " ";
                else pos = "";
            }
            matcher.usePattern(characterQuote);
            characterName = characterName.toUpperCase().trim();
            offset = quoteOffset;
            if(!characterName.isEmpty() && !characterNames.contains(characterName)) {
                characterNames.add(characterName);
            }
        }
        String nameRegex = "(";
        for(int i = 0; i < characterNames.size(); i++) {
            String characterName = characterNames.get(i);
            String[] names = characterName.split(" ");
            for (int j = 0; j < names.length; j++) {
                String name = names[j];
                names[j] = name + "_NNP";
            }
            characterName = String.join(" ", (CharSequence[]) names);
            nameRegex += characterName;
            if(i != characterNames.size() - 1) {
                nameRegex += "|";
            }
        }
        nameRegex += ")";
        matcher.usePattern(Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE));
        List<String> characterVerbs = new ArrayList<>();
        while(matcher.find()) {
            String pos = inspectNextPosTag(verbTags);
            if(!pos.isEmpty()) {
                characterVerbs.add(pos);
            }
        }
        Collections.sort(characterNames);
        Collections.sort(characterVerbs);
        System.out.println("characterNames = " + characterNames);
        System.out.println("characterVerbs = " + characterVerbs);
    }

    private static String inspectNextPosTag(List<String> pos) {
        if(matcher.find(offset)) {
            offset = matcher.end();
            if(pos.contains(matcher.group(2))) {
                return matcher.group(1);
            }
        }
        return "";
    }
}