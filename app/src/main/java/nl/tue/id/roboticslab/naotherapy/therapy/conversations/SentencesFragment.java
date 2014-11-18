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

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import org.json.JSONObject;

import java.util.List;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.Utilities;
import nl.tue.id.roboticslab.naotherapy.communicator.Communicator;
import nl.tue.id.roboticslab.naotherapy.therapy.Sentence;
import nl.tue.id.roboticslab.naotherapy.therapy.Therapy;
import nl.tue.id.roboticslab.naotherapy.therapy.TherapyController;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 15-10-14.
 */
public class SentencesFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private static final String LOG_TAG = SentencesFragment.class.getSimpleName();
    private View rootView;
    private SentencesAdapter adapter;
    private int mCurrentSentence;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_conversations_sentences, container, false);

        if (rootView != null) {
            setAdapter();

            DynamicListView view = (DynamicListView) rootView.findViewById(R.id.sentences_list_view);
            view.setOnItemClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveAdapterData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utilities.hideSoftKeyboard(getActivity());

        view.setEnabled(false);
        view.invalidate();

        Log.v("", view.getClass().toString());
        adapter.setChecked(position, true);
        Sentence sentence = adapter.getItem(position);
        new SentenceAsyncTask().execute(sentence.getSentence(), sentence.getAttitudeAsString());
        mCurrentSentence = position;
    }

    private void setAdapter() {
        TherapyController controller = new TherapyController(getActivity());
        List<Sentence> sentences = controller.getTherapy("Conversations").getSentencesByLanguages("Dutch");
        adapter = new SentencesAdapter(sentences, getActivity());

        TimedUndoAdapter swipeUndoAdapter = new TimedUndoAdapter(adapter, getActivity(),
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            adapter.remove(position);
                        }
                    }
                }
        );

        DynamicListView view = (DynamicListView) rootView.findViewById(R.id.sentences_list_view);
        swipeUndoAdapter.setAbsListView(view);
        view.setAdapter(swipeUndoAdapter);
        view.enableSimpleSwipeUndo();

    }

    private void saveAdapterData() {
        TherapyController controller = new TherapyController(getActivity());
        Therapy therapy = controller.getTherapy("Conversations");
        therapy.setSentences(adapter.getData());
        controller.save(therapy);
    }

    public void addSentence(Sentence sentence) {
        adapter.insertSentence(0, sentence);
    }

    private class SentenceAsyncTask extends AsyncTask<String, Void, Void> {
        private JSONObject response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Communicator comm = new Communicator(getActivity());

            String text = params[0];
            String attitude = params[1];
            comm.say(text, attitude);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            toggleActionButton();
        }
    }

    private void toggleActionButton() {
        adapter.setChecked(mCurrentSentence, false);
    }
}
