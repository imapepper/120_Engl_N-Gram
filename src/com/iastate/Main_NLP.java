package com.iastate;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main_NLP {

    private final static File file = new File("/Users/Joey/code/120_Engl_N-Gram/src/resources/hp1.tagged.txt");
    private final static Pattern characterQuote = Pattern.compile("``_``((?!''_'').)+''_''");
    private final static Pattern posTag = Pattern.compile("(\\S+)_(\\S+)");
    private final static String properNounTag = "NNP";
    private final static String verbTag = "VBD";

    private static Matcher matcher;
    private static int offset = 0;
    private static List<String> characterNames = new ArrayList<>();
    private static List<String> characterVerbs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        populateInitialCharacterNames(Main_NGrams.constructFileString(file));
        String characterRegex = buildRegexFromList(characterNames, properNounTag);
        Pattern names = Pattern.compile(characterRegex);
        populateCharacterVerbs(names);

        String verbRegex = buildRegexFromList(characterVerbs, verbTag);
        String charactersByVerbRegex = "(\\S+_NNP\\s+)?(\\S+_NNP) " + verbRegex;
        Pattern charactersByVerb = Pattern.compile(charactersByVerbRegex);

        addCharactersByVerbUse(charactersByVerb);

        Collections.sort(characterNames);
        Collections.sort(characterVerbs);
        System.out.println("characterNames = " + characterNames);
    }

    private static String inspectNextPosTag(String pos) {
        if(matcher.find(offset)) {
            offset = matcher.end();
            if(matcher.group(2).contains(pos)) {
                return matcher.group(1);
            }
        }
        return "";
    }

    private static void populateInitialCharacterNames(String fileString) {
        matcher = characterQuote.matcher(fileString);
        while(matcher.find(offset)) {
            int quoteOffset = matcher.end();
            offset = quoteOffset;
            matcher.usePattern(posTag);
            String characterName = "";
            String pos = inspectNextPosTag(verbTag);
            while(!pos.isEmpty()) {
                String name = inspectNextPosTag(properNounTag);
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
    }

    private static void populateCharacterVerbs(Pattern names) {
        matcher.usePattern(names);
        offset = 0;
        while(matcher.find(offset)) {
            int quoteOffset = matcher.end();
            offset = quoteOffset;
            matcher.usePattern(posTag);
            String pos = inspectNextPosTag(verbTag);
            if(!pos.isEmpty()) {
                pos = pos.toUpperCase().trim();
                if(!characterVerbs.contains(pos)) characterVerbs.add(pos);
            }
            matcher.usePattern(names);
            offset = quoteOffset;
        }
    }

    private static void addCharactersByVerbUse(Pattern charactersByVerb) {
        matcher.usePattern(charactersByVerb);
        offset = 0;
        while(matcher.find(offset)) {
            offset = matcher.end();
            String characterName = "";
            if(matcher.group(1) != null) {
                characterName += matcher.group(1);
            }
            characterName += matcher.group(2);
            characterName = characterName.replaceAll("_NNPS?", "").toUpperCase().trim();
            if(!characterName.isEmpty() && !characterNames.contains(characterName)) {
                characterNames.add(characterName);
            }
        }
    }

    @NotNull
    private static String buildRegexFromList(List<String> regexList, String pos) {
        String regex = "(?i)(";
        for(int i = 0; i < regexList.size(); i++) {
            String listItem = regexList.get(i);
            String[] items = listItem.split(" ");
            for (int j = 0; j < items.length; j++) {
                String name = items[j];
                items[j] = name + "_" + pos;
            }
            listItem = String.join(" ", (CharSequence[]) items);
            regex += listItem;
            if(i != regexList.size() - 1) {
                regex += "|";
            }
        }
        regex += ")";
        return regex;
    }
}