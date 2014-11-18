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

package nl.tue.id.roboticslab.naotherapy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import nl.tue.id.roboticslab.naotherapy.therapy.Emotion;
import nl.tue.id.roboticslab.naotherapy.therapy.Therapy;
import nl.tue.id.roboticslab.naotherapy.therapy.TherapyController;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 12-9-14.
 */
public class Utilities {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int getItemHeightOfListView(ListView listView, int items) {

        ListAdapter mAdapter = listView.getAdapter();

        int listviewElementsHeight = 0;
        // for listview total item height
        // items = mAdapter.getCount();


        for (int i = 0; i < items; i++) {

            View childView = mAdapter.getView(i, null, listView);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            listviewElementsHeight += childView.getMeasuredHeight();
        }


        return listviewElementsHeight;

    }

    public static String getRobotIp(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(SettingsActivity.KEY_PREF_ROBOT_IP, "");
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.findViewById(R.id.tts_edittext);
        view.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static Therapy loadTherapy(Context ctx, String name) {
        Therapy therapy = null;
        TherapyController controller = new TherapyController(ctx);

        if (controller.getAvailableTherapies().contains(name)) {
            therapy = controller.getTherapy(name);
        }

        return therapy;
    }

    public static ArrayList<Emotion> generateRandomEmotions(Context context, int nEmotions) {
        ArrayList<Emotion> emotions = new ArrayList<Emotion>();

        // Open the therapy
        Therapy therapy = loadTherapy(context, "Emotions");
        if (therapy == null) {
            return emotions;
        }

        // Get all the available emotions
        List<Emotion> therapyEmotions = therapy.getEmotions();

        // Shuffle the emotions
        java.util.Collections.shuffle(therapyEmotions);

        // Pick up nEmotions
        for (Emotion emotion : therapyEmotions.subList(0, nEmotions)) {
            emotions.add(emotion);
        }

        // return the list
        return emotions;
    }

    /**
     * Returns a random emotion from a list of emotions. The emotion returned is different
     * to the lastEmotion parameter
     *
     * @param emotions
     * @param lastEmotion
     * @return
     */
    public static Emotion getRandomEmotion(List<Emotion> emotions, String lastEmotion) {
        Emotion selectedEmotion;
        do {
            java.util.Collections.shuffle(emotions);

            selectedEmotion = emotions.get(0);
        } while (selectedEmotion.getName().equalsIgnoreCase(lastEmotion));
        return selectedEmotion;
    }

    public static void createTherapiesDirectory(Context context) {
        File dir = context.getDir(TherapyController.THERAPIES_DIRECTORY, Context.MODE_PRIVATE);
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("");
            for (String file : files) {
                InputStream in = assetManager.open(file);
                File outFile = new File(dir, file);
                if (!outFile.exists()) {
                    OutputStream out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();

                    out.flush();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}

