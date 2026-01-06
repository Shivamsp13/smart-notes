package com.shivam.smartnotes.utils;

public class TextQualityEvaluator {

    private static final int MIN_TEXT_LENGTH=50;
    private static final double MIN_ALPHA_RATIO=0.3;

    private TextQualityEvaluator() {
    }

    public static boolean isUsable(String text){

        if(text==null){
            return false;
        }

        String normalized=normalize(text);

        if (normalized.length() < MIN_TEXT_LENGTH) {
            return false;
        }

        double alphaRatio = calculateAlphabeticRatio(normalized);

        return alphaRatio >= MIN_ALPHA_RATIO;
    }
    private static String normalize(String text) {
        return text
                .replaceAll("\\s+", " ")
                .trim();
    }
    private static double calculateAlphabeticRatio(String text) {
        int alphaCount = 0;
        int totalCount = 0;

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }

            totalCount++;

            if (Character.isLetter(c)) {
                alphaCount++;
            }
        }

        if (totalCount == 0) {
            return 0.0;
        }

        return (double) alphaCount / totalCount;
    }
}
