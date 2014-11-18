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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.tue.id.roboticslab.naotherapy.R;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 15-9-14.
 */
public class EmotionsAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mData = new ArrayList<String>();
    private List<Integer> mStates;
    private static final String LOG_TAG = EmotionsAdapter.class.getSimpleName();

    static class ViewHolder {
        TextView textView;
    }

    public EmotionsAdapter(Context c, List<String> data, List<Integer> states) {
        mData = data;
        mContext = c;
        mStates = states;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate the layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.emotion_item, parent, false);

            // Set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.emotion_textview);

            // Store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mData.get(position));

        switch (mStates.get(position)) {
            case EmotionsFragment.NORMAL:
                viewHolder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                viewHolder.textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                break;
            case EmotionsFragment.RIGHT:
                viewHolder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.orange_light));
                viewHolder.textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                break;
            case EmotionsFragment.WRONG:
                viewHolder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.gray_wrong));
                viewHolder.textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                break;
            case EmotionsFragment.DISCARDED:
                viewHolder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.gray_discarded));
                viewHolder.textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                break;
        }

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        boolean enabled = false;

        if (mStates.get(position) == EmotionsFragment.NORMAL)
            enabled = true;

        // Return true for clickable, false for not
        return enabled;
    }

    public void setState(int position, int state) {
        mStates.set(position, state);
        notifyDataSetChanged();
    }

    public List<Integer> getStates() {
        return mStates;
    }

    public void discardElements() {
        for (int i = 0; i < mStates.size(); i++) {
            if (mStates.get(i) == EmotionsFragment.NORMAL)
                mStates.set(i, EmotionsFragment.DISCARDED);
        }
        notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackground(ViewHolder viewHolder, int id) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            viewHolder.textView.setBackgroundDrawable(mContext.getResources().getDrawable(id));
        } else {
            viewHolder.textView.setBackground(mContext.getResources().getDrawable(id));
        }
    }
}
