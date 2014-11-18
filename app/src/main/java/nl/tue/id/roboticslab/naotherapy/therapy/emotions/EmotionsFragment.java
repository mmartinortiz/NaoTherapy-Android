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

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.SettingsActivity;
import nl.tue.id.roboticslab.naotherapy.Utilities;
import nl.tue.id.roboticslab.naotherapy.communicator.Communicator;
import nl.tue.id.roboticslab.naotherapy.therapy.Emotion;
import nl.tue.id.roboticslab.naotherapy.therapy.Therapy;

/**
 * A placeholder fragment containing a simple view.
 */
public class EmotionsFragment extends Fragment implements View.OnClickListener {
    public static final int NORMAL = 0;
    public static final int RIGHT = 1;
    public static final int WRONG = 2;
    public static final int DISCARDED = 3;
    private static final String LOG_TAG = EmotionsFragment.class.getSimpleName();
    private static final String EMOTION_LIST = "emotion_list";
    private static final String EMOTION_NAME = "emotion_name";
    private static final String LAST_EMOTION_NAME = "last_emotion_name";
    private static final String EMOTION_GROUP = "emotion_group";
    private static final String STATISTICS = "statistics";
    private static final String TRIAL = "trial";
    private static final String IS_NEXT_BUTTON_VISIBLE = "is_next_button_visible";
    private static final String IS_EMOTION_GRIDVIEW_VISIBLE = "is_emotion_visible";
    private static final String HEADER_TEXT = "header_text";
    private static final String ADAPTER_STATES = "disabled_emotions";
    private static final String IS_EMOTION_CONGRATULATE_VISIBLE = "is_congratulate_visible";
    private static String wellDoneBehaviour = "";
    private static String finished;
    // List of all available emotions
    private List<String> mEmotionsNameList = new ArrayList<String>();
    // Name and group of the selected emotion
    private String mEmotionName = "";
    private String mEmotionGroup = "";

    // Last emotion. Used to be sure that next emotion will be different
    private String mLastEmotionName = "";

    // Number of emotions to show in the Grid View
    private int EMOTIONS;

    // Grid View adapter
    private EmotionsAdapter emotionsAdapter = null;

    private View rootView;

    private Therapy therapy = null;
    private Statistics statistics;
    private int mTrial = 0;
    private boolean VALID_SAME_GROUP;
    private boolean USING_ROBOT;


    public EmotionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        EMOTIONS = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_EMOTIONS_NUMBER, "6"));

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        VALID_SAME_GROUP = sharedPref.getBoolean("pref_emotions_valid_same_group", true);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        USING_ROBOT = sharedPref.getBoolean("pref_emotions_using_robot", true);

        rootView = inflater.inflate(R.layout.fragment_emotions, container, false);
        TextView header = (TextView) rootView.findViewById(R.id.emotions_header);
        if (USING_ROBOT) {
            header.setText(getResources().getString(R.string.emotions_header));

            // Make the video frame disappear
            rootView.findViewById(R.id.frame_video).setVisibility(View.GONE);
        } else {
            header.setText(getResources().getString(R.string.emotions_header_no_robot));
        }

        // Load Emotions XML
        // Now we are going to load directly a file. In future maybe we will offer a dialog
        therapy = Utilities.loadTherapy(getActivity(), "Emotions");

        if (savedInstanceState != null) {
            restoreSavedValues(savedInstanceState);
        } else {
            emotionsAdapter = null;

            // The first time the robot executes the emotion
            statistics = new Statistics(therapy.getEmotions().size(), EMOTIONS, VALID_SAME_GROUP, USING_ROBOT);
            mTrial = 0;
        }

        // Create the view to show the emotions
        createEmotionsView();

        if (USING_ROBOT && savedInstanceState == null) {
            // Maybe the children are using the videos
            executeEmotion();
        } else {
            playVideo();
        }

        // Set click listener for the 'next' button
        rootView.findViewById(R.id.emotions_next_button).setOnClickListener(this);

        return rootView;
    }

    private void playVideo() {
        // Play emotion video
        final VideoView video = (VideoView) rootView.findViewById(R.id.emotions_video);

        int videoResource = 0;
        if (mEmotionName.equalsIgnoreCase("teleurgesteld"))
            videoResource = R.raw.disappointed;
        if (mEmotionName.equalsIgnoreCase("droevig"))
            videoResource = R.raw.mournful;
        if (mEmotionName.equalsIgnoreCase("afgewezen"))
            videoResource = R.raw.rejected;
        if (mEmotionName.equalsIgnoreCase("vrolijk"))
            videoResource = R.raw.cheerful;
        if (mEmotionName.equalsIgnoreCase("opgewonden"))
            videoResource = R.raw.excited;
        if (mEmotionName.equalsIgnoreCase("enthousiast"))
            videoResource = R.raw.enthusiastic;
//        if (mEmotionName.equalsIgnoreCase("razend"))
//            videoResource = R.raw.mad;
        if (mEmotionName.equalsIgnoreCase("nijdig"))
            videoResource = R.raw.mad;
        if (mEmotionName.equalsIgnoreCase("kwaad"))
            videoResource = R.raw.angry;
        if (mEmotionName.equalsIgnoreCase("doodsbang"))
            videoResource = R.raw.terrified;
        if (mEmotionName.equalsIgnoreCase("geschrokken"))
            videoResource = R.raw.startled;
        if (mEmotionName.equalsIgnoreCase("onzeker"))
            videoResource = R.raw.insecure;

        String fileName = "android.resource://" + getActivity().getPackageName() + "/" + String.valueOf(videoResource);
        Uri uri = Uri.parse(fileName);

        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(video);

        video.setMediaController(mediaController);
        video.setVideoURI(uri);
        video.start();

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video.start();
            }
        });
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mEmotionName = savedInstanceState.getString(EMOTION_NAME);
        mLastEmotionName = savedInstanceState.getString(LAST_EMOTION_NAME);
        mEmotionGroup = savedInstanceState.getString(EMOTION_GROUP);
        mEmotionsNameList = savedInstanceState.getStringArrayList(EMOTION_LIST);

        statistics = savedInstanceState.getParcelable(STATISTICS);
        mTrial = savedInstanceState.getInt(TRIAL);

        setVisibility(rootView.findViewById(R.id.emotions_next_button), savedInstanceState.getInt(IS_NEXT_BUTTON_VISIBLE));
        setVisibility(rootView.findViewById(R.id.emotions_gridview), savedInstanceState.getInt(IS_EMOTION_GRIDVIEW_VISIBLE));
        setVisibility(rootView.findViewById(R.id.emotions_congratulate_label), savedInstanceState.getInt(IS_EMOTION_CONGRATULATE_VISIBLE));

        ((TextView) rootView.findViewById(R.id.emotions_header)).setText(
                savedInstanceState.getString(HEADER_TEXT));

        List<Integer> states = savedInstanceState.getIntegerArrayList(ADAPTER_STATES);
        emotionsAdapter = new EmotionsAdapter(getActivity(), mEmotionsNameList, states);
    }

    private void setVisibility(View view, int visible) {
        switch (visible) {
            case View.GONE:
                view.setVisibility(View.GONE);
                break;
            case View.VISIBLE:
                view.setVisibility(View.VISIBLE);
                break;
            case View.INVISIBLE:
                view.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void executeEmotion() {
        if (mEmotionName != null && !mEmotionName.isEmpty()) {
            Emotion emotion = therapy.getEmotionByName(mEmotionName);
            new EmotionsAsyncTask().execute(emotion.getBehaviour().getFullName());
        }
    }

    private void createEmotionsView() {
        // Is this the first time this view is created?
        if (mEmotionName.isEmpty() && mEmotionsNameList.size() == 0) {
            // Generate a random list of emotions
            List<Emotion> emotions = Utilities.generateRandomEmotions(getActivity(), EMOTIONS);
            for (Emotion e : emotions) {
                mEmotionsNameList.add(e.getName());
            }

            // Get a random emotion
            Emotion emotion = Utilities.getRandomEmotion(emotions, mLastEmotionName);
            mEmotionName = emotion.getName();
            mEmotionGroup = emotion.getGroup();
            mLastEmotionName = mEmotionName;

            // Take note that this will be a new trial
            statistics.newTrial(mTrial, mEmotionName);

            //hide the 'next' button
            rootView.findViewById(R.id.emotions_next_button).setVisibility(View.INVISIBLE);

            // Set all states to 'normal'
            List<Integer> states = new ArrayList<Integer>();
            for (int i = 0; i < mEmotionsNameList.size(); i++) {
                states.add(NORMAL);
            }
            emotionsAdapter = new EmotionsAdapter(getActivity(), mEmotionsNameList, states);
        }

        // Populate the GridView with mEmotionList
        final GridView emotionsView = (GridView) rootView.findViewById(R.id.emotions_gridview);
//        final ListView emotionsView = (ListView) rootView.findViewById(R.id.emotions_gridview);
        emotionsView.setAdapter(emotionsAdapter);

//        // Set the items separation
//        final ViewTreeObserver observer = emotionsView.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int height = ((RelativeLayout) emotionsView.getParent()).getHeight();
//                int rowHeight = Utilities.getItemHeightOfListView(emotionsView, emotionsAdapter.getCount());
//                int separation = (height - rowHeight) / emotionsAdapter.getCount();
//                if (separation > 0) {
//                    emotionsView.setDivider(getResources().getDrawable(android.R.color.transparent));
//                    emotionsView.setDividerHeight(separation);
//                }
//
//                emotionsView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            emotionsView.setNumColumns(EMOTIONS / 3);
        }

        emotionsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the emotion for the selected item
                Emotion emotion = therapy.getEmotionByName(((TextView) view).getText().toString());

                // Check the answer
                boolean rightAnswer;
                if (VALID_SAME_GROUP) {
                    rightAnswer = emotion.getName().equalsIgnoreCase(mEmotionName) ||
                            emotion.getGroup().equalsIgnoreCase(mEmotionGroup);
                } else {
                    rightAnswer = emotion.getName().equalsIgnoreCase(mEmotionName);
                }

                if (rightAnswer) {
                    // Do something
                    if (USING_ROBOT) {
                        wellDoneBehaviour = emotion.getBehaviour().getAldebaranPackage() + "/" + "WellDone";
                        finished = emotion.getBehaviour().getAldebaranPackage() + "/" + "Finished";
                        new EmotionsAsyncTask().execute(wellDoneBehaviour);
                    } else {
                        Toast.makeText(getActivity(), "Je hebt het goed!", Toast.LENGTH_SHORT).show();
                        // Show 'next' button only when working with videos. If not, we do it after
                        // receive an answer from the robot
                        rootView.findViewById(R.id.emotions_next_button).setVisibility(View.VISIBLE);
                    }

                    // Increment number of exercises
                    mTrial = mTrial + 1;

                    // Set the button as right
                    emotionsAdapter.setState(position, RIGHT);
                    emotionsAdapter.discardElements();
                } else {
                    // Increment fails in statistics
                    statistics.incrementFail(mTrial, mEmotionName);

                    // Disable the button
                    emotionsAdapter.setState(position, WRONG);
                }

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EMOTION_NAME, mEmotionName);
        outState.putString(EMOTION_GROUP, mEmotionGroup);
        outState.putStringArrayList(EMOTION_LIST, (ArrayList<String>) mEmotionsNameList);
        outState.putInt(IS_NEXT_BUTTON_VISIBLE, rootView.findViewById(R.id.emotions_next_button).getVisibility());
        outState.putInt(IS_EMOTION_GRIDVIEW_VISIBLE, rootView.findViewById(R.id.emotions_gridview).getVisibility());
        outState.putInt(IS_EMOTION_CONGRATULATE_VISIBLE, rootView.findViewById(R.id.emotions_congratulate_label).getVisibility());
        outState.putString(HEADER_TEXT, String.valueOf(((TextView) rootView.findViewById(R.id.emotions_header)).getText()));

        outState.putIntegerArrayList(ADAPTER_STATES, (ArrayList<Integer>) emotionsAdapter.getStates());

        // Statistics
        outState.putInt(TRIAL, mTrial);
        outState.putParcelable(STATISTICS, statistics);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emotions_next_button:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int totalTrials = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_EMOTIONS_EXERCISES, "5"));

                if (mTrial < totalTrials) {
                    // Lets do another trial
                    mEmotionName = "";
                    mEmotionsNameList.clear();
                    createEmotionsView();
                    if (USING_ROBOT) {
                        executeEmotion();
                    } else {
                        playVideo();
                    }

                } else {
                    // The trials are finished
                    if (USING_ROBOT) {
                        new EmotionsAsyncTask().execute(finished);
                    }

                    // Hide gridview
                    rootView.findViewById(R.id.emotions_gridview).setVisibility(View.GONE);
                    rootView.findViewById(R.id.frame_video).setVisibility(View.GONE);
                    rootView.findViewById(R.id.emotions_congratulate_label).setVisibility(View.VISIBLE);

                    // Hide (again) the 'next' button
                    rootView.findViewById(R.id.emotions_next_button).setVisibility(View.GONE);

                    // Set new header text
                    ((TextView) rootView.findViewById(R.id.emotions_header)).setText(getResources().getString(R.string.emotions_header_done));
                    showStatistics();
                }
                break;
        }
    }

    private void showStatistics() {
        // Log for debugging
        Log.v(LOG_TAG, statistics.getSummary());

        // Append to statistics file
        try {
            File file = new File(getActivity().getFilesDir().toString() + "/statistics.csv");
            boolean exists = file.exists();

            FileOutputStream outputStream = getActivity().openFileOutput("statistics.csv",
                    Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE | Context.MODE_APPEND);

            if (!exists) {
                outputStream.write(statistics.getCsvHeader().getBytes());
            }
            outputStream.write(statistics.getAsCsv().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class EmotionsAsyncTask extends AsyncTask<String, Void, Void> {
        private JSONObject response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Communicator comm = new Communicator(getActivity());

            String behaviour = params[0];
            response = comm.setBehaviour(behaviour);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (response == null) return;

            try {
                if (response.getString("result").equalsIgnoreCase("True")
                        && response.getString("parameter").equalsIgnoreCase(wellDoneBehaviour)
                        && USING_ROBOT) {
                    rootView.findViewById(R.id.emotions_next_button).setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
