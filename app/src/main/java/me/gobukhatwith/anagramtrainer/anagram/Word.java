package me.gobukhatwith.anagramtrainer.anagram;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Word {
    private String word;
    private String anagram;
    private Integer length;
    private HashMap<Character, Integer> letters;
    private Integer frequency;
    private Double difficulty;
    private Integer shuffleIntensity;

    private Integer hintCount = 0;

    public Word(String word, Integer frequency, Integer shuffleIntensity) {
        this.word = word;
        this.length = word.length();
        this.frequency = frequency;
        this.shuffleIntensity = shuffleIntensity;
        letters = new HashMap<>();

        for (int i = 0; i < word.length(); i++) {
            if (letters.get(word.charAt(i)) == null) {
                letters.put(word.charAt(i), 1);
            } else {
                letters.put(word.charAt(i), letters.get(word.charAt(i)) + 1);
            }
        }

        this.difficulty = Math.log10((double) frequency) + this.length;
    }

    public Word(String word, Integer frequency) {
        this(word, frequency, 50);
    }

    public Word(String word) {
        this(word, 1);
    }

    public String getWord() {
        return this.word;
    }

    public String getAnagram() {
        if (anagram == null) {
            generateAnagramWithIntensity();
            //generateAnagram();
        }
        return anagram;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    private void generateAnagram() {
        List<Character> characters = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : letters.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                characters.add(entry.getKey());
            }
        }
        StringBuilder anagram = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = characters.size(); i > 0; i--) {
            int position = Math.abs(secureRandom.nextInt()) % i;
            anagram.append(characters.get(position));
            characters.remove(position);
        }
        this.anagram = anagram.toString();
    }

    private void generateAnagramWithIntensity() {
        Map<Integer, Integer> permutations = new HashMap<>();

        int permutationsCount = (word.length() * ((shuffleIntensity / 2) % 51)) / 100;

        SecureRandom secureRandom = new SecureRandom();
        Set<Integer> usedOldPositions = new HashSet<>();
        Set<Integer> usedNewPositions = new HashSet<>();
        for (int i = 0; i < permutationsCount; i++) {
            int oldPosition = Math.abs(secureRandom.nextInt()) % word.length();
            while (usedOldPositions.contains(oldPosition)) {
                oldPosition = Math.abs(secureRandom.nextInt()) % word.length();
            }
            usedOldPositions.add(oldPosition);

            int newPosition = Math.abs(secureRandom.nextInt()) % word.length();
            while (usedNewPositions.contains(newPosition) || usedOldPositions.contains(newPosition)) {
                newPosition = Math.abs(secureRandom.nextInt()) % word.length();
            }
            usedNewPositions.add(newPosition);

            permutations.put(oldPosition, newPosition);
        }

        StringBuilder anagram = new StringBuilder(word);
        for (Map.Entry<Integer, Integer> entry : permutations.entrySet()) {
            String tmp = anagram.substring(entry.getKey(), entry.getKey() + 1);
            anagram.replace(entry.getKey(), entry.getKey() + 1, anagram.substring(entry.getValue(), entry.getValue() + 1));
            anagram.replace(entry.getValue(), entry.getValue() + 1, tmp);
        }
        this.anagram = anagram.toString();
    }

    public boolean isCorrect(String wordString) {
        return wordString.equals(word);
    }

    public boolean isAnagramOf(String anagram) {
        Word word = new Word(anagram);
        if (!word.length.equals(length) || word.letters.size() != letters.size()) {
            return false;
        }

        for (Map.Entry<Character, Integer> entry : word.letters.entrySet()) {
            if (letters.get(entry.getKey()) == null || !letters.get(entry.getKey())
                    .equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    public String hint() {
        if (hintCount >= word.length()) {
            return word;
        }
        hintCount++;
        return word.substring(0, hintCount);
    }
}
