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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;

import org.json.JSONObject;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.Utilities;
import nl.tue.id.roboticslab.naotherapy.communicator.Communicator;
import nl.tue.id.roboticslab.naotherapy.therapy.Behaviour;
import nl.tue.id.roboticslab.naotherapy.therapy.Therapy;
import nl.tue.id.roboticslab.naotherapy.therapy.TherapyController;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 25-3-14.
 */
public class BehavioursFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    BehavioursAdapter adapter;
    protected ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_conversations_actions, container, false);

        Therapy therapy = new TherapyController(getActivity()).getTherapy("Conversations");

        adapter = new BehavioursAdapter(therapy.getBehaviours(), getActivity());
        ListView actions = (ListView) rootView.findViewById(R.id.therapy_fragment_elements);
        actions.setAdapter(adapter);
        actions.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Utilities.hideSoftKeyboard(getActivity());

        if (((CheckedTextView) view).isChecked()) {
            // Start the behaviour
            ((CheckedTextView) view).setChecked(true);
            new BehavioursAsyncTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, COMMAND_START, ((Behaviour) adapter.getItem(position)).getFullName());
        } else {
            // Stop the behaviour
            new BehavioursAsyncTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, COMMAND_STOP, ((Behaviour) adapter.getItem(position)).getFullName());
        }
    }

    public void toggleActionButton(String actionName) {
        ListView listView = (ListView) rootView.findViewById(R.id.therapy_fragment_elements);
        String[] name = actionName.split("/");

        for (int i = 0; i < adapter.getCount(); i++) {
            CheckedTextView view = (CheckedTextView) adapter.getView(i, null, null);

            if (view.getText().toString().equalsIgnoreCase(name[1])) {
                listView.setItemChecked(i, false);
                listView.invalidateViews();
            }
        }
    }

    private class BehavioursAsyncTask extends AsyncTask<String, Void, Void> {
        private JSONObject response = null;
        private String behaviourName = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Communicator comm = new Communicator(getActivity());

            String command = params[0];
            behaviourName = params[1];

            if (command.equalsIgnoreCase(COMMAND_START)) {
                response = comm.setBehaviour(behaviourName);
            }

            if (command.equalsIgnoreCase(COMMAND_STOP)) {
                response = comm.stopBehaviour(behaviourName);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            toggleActionButton(behaviourName);
        }
    }
}
