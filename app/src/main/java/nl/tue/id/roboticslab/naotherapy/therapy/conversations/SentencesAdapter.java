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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.ArrayList;
import java.util.List;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.therapy.Sentence;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 19-3-14.
 */
public class SentencesAdapter extends BaseAdapter implements UndoAdapter {
    private static final String LOG_TAG = SentencesAdapter.class.getSimpleName();
    private List<Sentence> mData;
    private List<Boolean> mCheckedState = new ArrayList<Boolean>();
    private Context mContext;

    public SentencesAdapter(List<Sentence> data, Context context) {
        mContext = context;
        mData = data;
        for (int i = 0; i < mData.size(); i++) mCheckedState.add(i, false);
    }

    public List<Sentence> getData() {
        return mData;
    }

    public void insertSentence(int position, Sentence sentence) {
        mData.add(position, sentence);
        mCheckedState.add(position, false);
        notifyDataSetChanged();
    }

    public void setChecked(int position, boolean status) {
        mCheckedState.set(position, false);
//        mCheckedState.set(position, status);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        CheckedTextView textView;
        TextView button;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Sentence getItem(int index) {
        return mData.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }

    public void remove(int position) {
        mData.remove(position);
        mCheckedState.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate the layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sentence_row, null);

            // Set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.textView = (CheckedTextView) convertView.findViewById(R.id.conversations_sentence);
            viewHolder.button = (TextView) convertView.findViewById(R.id.sentence_edit_button);

            // Store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(mData.get(position).getSentence());

//        if (mCheckedState.get(position)) {
//            viewHolder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
//            viewHolder.textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
//            viewHolder.button.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
//        }

        Drawable icon = null;
        switch (mData.get(position).getAttitude()) {
            case Sentence.NORMAL:
                if (mCheckedState.get(position)) {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_normal_small_selected);
                } else {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_normal_small);
                }
                break;
            case Sentence.POSITIVE:
                if (mCheckedState.get(position)) {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_positive_small_selected);
                } else {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_positive_small);
                }
                break;
            case Sentence.NEGATIVE:
                if (mCheckedState.get(position)) {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_negative_small_selected);
                } else {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_negative_small);
                }
                break;
            case Sentence.SAD:
                if (mCheckedState.get(position)) {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_sad_small_selected);
                } else {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_sad_small);
                }
                break;
            case Sentence.HAPPY:
                if (mCheckedState.get(position)) {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_happy_small_selected);
                } else {
                    icon = mContext.getResources().getDrawable(R.drawable.attitude_happy_small);
                }
                break;
        }
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        final int index = position;
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog(mData.get(index), index);
            }
        });

        return convertView;
    }

    private void editDialog(final Sentence sentence, final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.new_sentence_dialog_title);
        builder.setMessage(R.string.new_sentence_dialog_message);

        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflator = inflater.inflate(R.layout.dialog_new_sentence, null);

        // Setting some widget for sentence editing
        EditText sentenceWidget = (EditText) inflator.findViewById(R.id.dialog_sentence);
        sentenceWidget.setText(sentence.getSentence());

        AttitudeRadioGroup attitudeGroup = (AttitudeRadioGroup) inflator.findViewById(R.id.attitude_radio_group);
        attitudeGroup.checkAttitude(sentence.getAttitude());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflator);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText sentenceWidget = (EditText) ((AlertDialog) dialog).findViewById(R.id.dialog_sentence);
                AttitudeRadioGroup attitudeGroup = (AttitudeRadioGroup) ((AlertDialog) dialog).findViewById(R.id.attitude_radio_group);

                String text = sentenceWidget.getText().toString();
                int attitude = Sentence.getAttitudeAsInt(attitudeGroup.getAttitude());
                String language = "Dutch";

                sentence.setAttitude(attitude);
                sentence.setLanguage(language);
                sentence.setSentence(text);

                mData.set(index, sentence);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
            }
        });

        builder.show();
    }
}
