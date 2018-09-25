package tirzad.starunique.wordguess.data;

import android.text.TextUtils;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by StarUnique on 20/09/2017.
 */

public class SentenceHandling {

    private static int mArrayIndex;
    private static String[][] mArrayOfWords;
    private static ArrayList<String> mStringArray;

    public static String changeToStars(String sentence) {
        StringBuilder starredSentence = new StringBuilder();
        int sentenceLength = sentence.length();
        int characterCounter = 0;
        mArrayIndex = 0;
        char eachLetter = ' ';


        mArrayOfWords = new String[sentenceLength / 3][4];

        while (characterCounter < sentenceLength) {
            int charCounter = 0;
            String eachWord = "", starredWord = "";

            eachLetter = sentence.charAt(characterCounter);
            while (Character.isLowerCase(eachLetter) && ((characterCounter + charCounter +1)<sentenceLength)) {
                eachWord = eachWord + eachLetter;
                charCounter++;
                eachLetter = sentence.charAt(characterCounter + charCounter);
            }

            if (charCounter > 0) {
                mArrayIndex++;
                mArrayOfWords[mArrayIndex][3] = Integer.toString(starredSentence.length());
                mArrayOfWords[mArrayIndex][2] = eachWord;
                mArrayOfWords[mArrayIndex][0] = Integer.toString(eachWord.length());
                mArrayOfWords[mArrayIndex][1] = Integer.toString(mArrayIndex);

                for (int counter = 1; counter <= charCounter - mArrayOfWords[mArrayIndex][0].length(); counter++)
                    starredWord = starredWord + "*";

                starredSentence.append(Integer.toString(eachWord.length()) +
                        starredWord + eachLetter)
                        ;
            } else {
                starredSentence.append(eachLetter);
            }
            //		System.out.println(starredWord);
            characterCounter += charCounter + 1;

        }

        return starredSentence.toString();
    }

    public static String giveHintWord(String starredSentence) {
        StringBuilder buffer = new StringBuilder(starredSentence);
        if (mArrayIndex > 0) {
            SecureRandom randomNumbers = new SecureRandom();
            int randomWord = 1 + randomNumbers.nextInt(mArrayIndex);

            while (mArrayOfWords[randomWord][1] == "")
                randomWord = 1 + randomNumbers.nextInt(mArrayIndex);

            String chosenHint = mArrayOfWords[randomWord][2];
            int hintWordPosition = Integer.parseInt(mArrayOfWords[randomWord][3]);

            mArrayOfWords[randomWord][1] = mArrayOfWords[mArrayIndex][1];
            mArrayOfWords[randomWord][2] = mArrayOfWords[mArrayIndex][2];
            mArrayOfWords[randomWord][0] = mArrayOfWords[mArrayIndex][0];
            mArrayOfWords[randomWord][3] = mArrayOfWords[mArrayIndex][3];

            mArrayIndex--;

            buffer.delete(hintWordPosition, hintWordPosition + chosenHint.length());
            buffer.insert(hintWordPosition, chosenHint);
        }
        return buffer.toString();
    }

    public static String checkGuessedWord(String starredSentence, String guessedWord) {
        StringBuilder builder = new StringBuilder(starredSentence);
        if (!TextUtils.isEmpty(guessedWord) && mArrayIndex > 0) {
            for (int counter = 1; counter <= mArrayIndex; counter++) {
                if (mArrayOfWords[counter][2].equals(guessedWord)) {

                    int position = Integer.parseInt(mArrayOfWords[counter][3]);
                    builder.delete(position,
                            position + mArrayOfWords[counter][2].length());
                    builder.insert(position, mArrayOfWords[counter][2]);

                    mArrayOfWords[counter][1] = mArrayOfWords[mArrayIndex][1];
                    mArrayOfWords[counter][2] = mArrayOfWords[mArrayIndex][2];
                    mArrayOfWords[counter][0] = mArrayOfWords[mArrayIndex][0];
                    mArrayOfWords[counter][3] = mArrayOfWords[mArrayIndex][3];

                    mArrayOfWords[mArrayIndex][1] = "";
                    mArrayOfWords[mArrayIndex][2] = "";
                    mArrayOfWords[mArrayIndex][0] = "";
                    mArrayOfWords[mArrayIndex][3] = "";

                    mArrayIndex--;
                }
            }
        }
        return builder.toString();

    }


}
