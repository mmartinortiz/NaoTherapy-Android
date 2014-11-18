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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.Utilities;
import nl.tue.id.roboticslab.naotherapy.therapy.Sentence;

public class ConversationsActivity extends Activity implements View.OnClickListener,
        TextToSpeechFragment.SentenceSent {

    SentencesFragment sentencesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_conversations);

        getActionBar().setTitle(getString(R.string.therapy_conversations_label));
//        setContentView(R.layout.activity_conversations);
        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction()
                    .add(R.id.tts_container, new TextToSpeechFragment())
                    .commit();

            // Add the fragment to the 'sentences_container' FrameLayout
            sentencesFragment = new SentencesFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.sentences_container, sentencesFragment).commit();

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.sentences_container, new SentencesFragment())
//                    .commit();
            getFragmentManager().beginTransaction()
                    .add(R.id.therapy_container, new BehavioursFragment())
                    .commit();

//            Create a new Fragment to be placed in the activity layout
//            Fragment fragment = new TextToSpeechFragment();
//
//            Add the fragment to the 'tts_container' FrameLayout
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.add(R.id.tts_container, fragment, TTS_TAG).commit();
//
//            // Add the fragment to the 'sentences_container' FrameLayout
//            fragment = new SentencesFragment();
//            transaction = fragmentManager.beginTransaction();
//            transaction.add(R.id.sentences_container, fragment, SENTENCES_TAG).commit();
//
//            // Add the fragment to the 'postures_container' FrameLayout
//            behavioursFragment = new BehavioursFragment();
//            transaction = fragmentManager.beginTransaction();
//            transaction.add(R.id.therapy_container, behavioursFragment, BEHAVIOURS_TAG).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Utilities.hideSoftKeyboard(this);
    }

    @Override
    public void addSentence(Sentence sentence) {
        sentencesFragment.addSentence(sentence);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_conversations, container, false);
            return rootView;
        }
    }
}
