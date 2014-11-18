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

package nl.tue.id.roboticslab.naotherapy.therapy.emotions;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 30-9-14.
 */
public class Statistics implements Parcelable {
    private static final String LOG_TAG = Statistics.class.getSimpleName();
    private int mTotalEmotions = 0;
    private int nEmotionsDisplayed = 0;
    private boolean mValidSameGroup = true;
    private boolean mUsingRobot = true;
    private List<String> mEmotionList;
    private List<Integer> mFailsList;

    public Statistics(int totalEmotions, int emotionsDisplayed, boolean validSameGroup, boolean usingRobot) {
        mEmotionList = new ArrayList<String>();
        mFailsList = new ArrayList<Integer>();

        // Number of emotions displayed in the exercise
        nEmotionsDisplayed = emotionsDisplayed;
        mTotalEmotions = totalEmotions;
        mValidSameGroup = validSameGroup;
        mUsingRobot = usingRobot;
    }

    public void incrementFail(int trial, String emotion) {

        if (trial <= mEmotionList.size()) {
            if (!mEmotionList.get(trial).equalsIgnoreCase(emotion)) {
                Log.v(LOG_TAG, "That is weird... the emotions does not match: " + emotion + " " + mEmotionList.get(trial));
            }

            int fails = mFailsList.get(trial);
            mFailsList.set(trial, fails + 1);
        }
    }

    public void newTrial(int trial, String emotion) {
        if (mEmotionList.size() <= trial) {
            mEmotionList.add(emotion);
        }

        if (mFailsList.size() <= trial) {
            mFailsList.add(0);
        }
    }

    public String getSummary() {
        String resume = "Trials: " + mEmotionList.size() +
                " | Emotions displayed: " + nEmotionsDisplayed +
                " | Total emotions for exercise: " + mTotalEmotions +
                " | Valid emotions on the same group" + mValidSameGroup +
                " | Using the robot" + mUsingRobot + "\n\n";

        for (int i = 0; i < mEmotionList.size(); i++) {
            resume = resume + "Emotion: " + mEmotionList.get(i) + "\n";
            resume = resume + "Fails: " + mFailsList.get(i) + "\n\n";
        }

        return resume;
    }

    public String getCsvHeader() {
        String header = "timestamp; emotions displayed; total emotions; valid emotions on same group; using robot; trials";
        for (int i = 1; i <= mTotalEmotions; i++) {
            header = header + ";" + String.valueOf(i) + " trial emotion;" + String.valueOf(i) + " trial fails";
        }
        return header + "\n";
    }

    public String getAsCsv() {
        // CSV Format:
        // timestamp; emotions displayed; total emotions; valid emotions on same group; using robot; trials; First trial emotion; First trial fails; Second trial...
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm");

        Date date = new Date(timestamp);
        String line = sdf.format(date) + ";" + nEmotionsDisplayed + ";" + mTotalEmotions + ";" + mValidSameGroup + ";" + mUsingRobot + ";" + mEmotionList.size();
        for (int i = 0; i < mTotalEmotions; i++) {
            if (i < mEmotionList.size()) {
                // Add the trial
                line = line + ";" + mEmotionList.get(i) + ";" + mFailsList.get(i);
            } else {
                // Add empty/null values
                line = line + ";-;-";
            }
        }

        line = line + "\n";

        return line;
    }

    // Parcelling part
    public Statistics(Parcel in) {
        in.readStringList(mEmotionList);
        in.readList(mFailsList, Integer.class.getClassLoader());

        int[] values = new int[4];
        in.readIntArray(values);
        mTotalEmotions = values[0];
        nEmotionsDisplayed = values[1];
        mValidSameGroup = values[2] == 1;
        mUsingRobot = values[3] == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mEmotionList);
        dest.writeList(mFailsList);
        dest.writeIntArray(new int[]{mTotalEmotions,
                nEmotionsDisplayed,
                mValidSameGroup ? 1 : 0,
                mUsingRobot ? 1 : 0});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Statistics createFromParcel(Parcel in) {
            return new Statistics(in);
        }

        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };
}
