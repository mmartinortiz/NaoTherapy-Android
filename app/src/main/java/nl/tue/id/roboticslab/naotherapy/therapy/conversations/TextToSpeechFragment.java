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

package nl.tue.id.roboticslab.naotherapy.therapy.conversations;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONObject;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.Utilities;
import nl.tue.id.roboticslab.naotherapy.communicator.Communicator;
import nl.tue.id.roboticslab.naotherapy.therapy.Sentence;
import nl.tue.id.roboticslab.naotherapy.therapy.TherapyController;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 18/02/14.
 */
public class TextToSpeechFragment extends Fragment implements
        View.OnClickListener {

    private static final String LOG_TAG = TextToSpeechFragment.class.getSimpleName();
    private View rootView;
    String currentLanguage;
    SentenceSent mCallback;

    private static final String COMMAND_SAY = "say";

    public interface SentenceSent {
        public void addSentence(Sentence sentence);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_conversations_tts, container, false);

        rootView.findViewById(R.id.tts_action_button).setOnClickListener(this);
        rootView.findViewById(R.id.tts_edittext).setOnClickListener(this);

        TherapyController controller = new TherapyController(getActivity());
        currentLanguage = controller.getTherapy("Conversations").getLanguage();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SentenceSent) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onClick(View view) {
        EditText editText = (EditText) rootView.findViewById(R.id.tts_edittext);

        switch (view.getId()) {
            case R.id.tts_action_button:
            case R.id.tts_edittext:
                String text = editText.getText().toString();
                if (!text.isEmpty()) {
                    String attitude = ((AttitudeRadioGroup) rootView.findViewById(R.id.attitude_radio_group)).getAttitude();
                    new TtsAsyncTask().execute(COMMAND_SAY, text, attitude);
                    editText.setText("");
                    Utilities.hideSoftKeyboard(getActivity());

                    mCallback.addSentence(new Sentence(text, currentLanguage, Sentence.getAttitudeAsInt(attitude)));
                }
                break;
            default:

                break;
        }
    }

    private class TtsAsyncTask extends AsyncTask<String, Void, Void> {
        private JSONObject response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.hideSoftKeyboard(getActivity());
        }

        @Override
        protected Void doInBackground(String... params) {
            Communicator comm = new Communicator(getActivity());

            String command = params[0];

            if (command.equalsIgnoreCase(COMMAND_SAY)) {
                String text = params[1];
                String attitude = params[2];
                comm.say(text, attitude);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
        }
    }
}
