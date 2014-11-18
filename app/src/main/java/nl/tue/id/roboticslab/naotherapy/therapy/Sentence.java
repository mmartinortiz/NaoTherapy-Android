/*
 * This file is part of NaoTherapy.
 *
 * NaoTherapy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NaoTherapy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.tue.id.roboticslab.naotherapy.therapy;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 9-4-14.
 */
public class Sentence {
    public static final int NORMAL = 0;
    public static final int POSITIVE = 1;
    public static final int NEGATIVE = 2;
    public static final int SAD = 3;
    public static final int HAPPY = 4;
    private static final String LOG_TAG = Sentence.class.getSimpleName();

    //    public static final String HAPPY = "Happy";
//    public static final String SAD = "Sad";
//    public static final String NEUTRAL = "Neutral";
    private String sentence;
    private String language;
    private int attitude;

    public Sentence(String sentence, String language, int attitude) {
        this.sentence = sentence;
        this.language = language;
        this.attitude = attitude;
//        setAttitude(attitude);
    }

    @Override
    public String toString() {
        return this.sentence + "/" + this.language + "/" + getAttitudeAsString(attitude);
    }

    public String getAttitudeAsString() {
        return getAttitudeAsString(attitude);
    }

    public static String getAttitudeAsString(int attitude) {
        String name = "";
        switch (attitude) {
            case NORMAL:
                name = "Normal";
                break;
            case NEGATIVE:
                name = "Negative";
                break;
            case POSITIVE:
                name = "Positive";
                break;
            case HAPPY:
                name = "Happy";
                break;
            case SAD:
                name = "Sad";
                break;

        }
        return name;
    }

    public static int getAttitudeAsInt(String attitude) {
        int code = -1;

        if (attitude.equalsIgnoreCase("Normal")) {
            return NORMAL;
        }
        if (attitude.equalsIgnoreCase("Negative")) {
            return NEGATIVE;
        }
        if (attitude.equalsIgnoreCase("Positive")) {
            return POSITIVE;
        }
        if (attitude.equalsIgnoreCase("Happy")) {
            return HAPPY;
        }
        if (attitude.equalsIgnoreCase("Sad")) {
            return SAD;
        }

        return code;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getAttitude() {
        return attitude;
    }

    public void setAttitude(int attitude) {
        this.attitude = attitude;
//        if (attitude.equalsIgnoreCase(HAPPY))
//            this.attitude = HAPPY;
//        if (attitude.equalsIgnoreCase(SAD))
//            this.attitude = SAD;
//        if (attitude.equalsIgnoreCase(NEUTRAL))
//            this.attitude = NEUTRAL;
//
    }
}
