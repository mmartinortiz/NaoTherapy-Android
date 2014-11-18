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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;
import android.widget.TextView;

import nl.tue.id.roboticslab.naotherapy.R;
import nl.tue.id.roboticslab.naotherapy.therapy.Sentence;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 15-10-14.
 */
public class AttitudeRadioGroup extends RadioGroup {
    private String mAttitude;
    private Context mContext;
    private TextView mLabel;

    public AttitudeRadioGroup(Context context) {
        super(context);
        mContext = context;
        mAttitude = "";

        init();
    }

    public AttitudeRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttitude = "";

        init();
    }

    private void init() {
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getAttitude();
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLabel = (TextView) super.findViewById(R.id.tts_emotion_label);
        check(R.id.attitude_normal_radiobutton);
    }

    public String getAttitude() {
        switch (getCheckedRadioButtonId()) {
            case R.id.attitude_normal_radiobutton:
                mAttitude = Sentence.getAttitudeAsString(Sentence.NORMAL);
                break;
            case R.id.attitude_negative_radiobutton:
                mAttitude = Sentence.getAttitudeAsString(Sentence.NEGATIVE);
                break;
            case R.id.attitude_sad_radiobutton:
                mAttitude = Sentence.getAttitudeAsString(Sentence.SAD);
                break;
            case R.id.attitude_positive_radiobutton:
                mAttitude = Sentence.getAttitudeAsString(Sentence.POSITIVE);
                break;
            case R.id.attitude_happy_radiobutton:
                mAttitude = Sentence.getAttitudeAsString(Sentence.HAPPY);
                break;
        }
        setAttitudeLabel();
        return mAttitude;
    }

    public void setAttitudeLabel() {
        mLabel.setText(mAttitude.toUpperCase());
    }

    public void checkAttitude(int attitude) {
        switch (attitude) {
            case Sentence.NORMAL:
                this.check(R.id.attitude_normal_radiobutton);
                break;
            case Sentence.POSITIVE:
                this.check(R.id.attitude_positive_radiobutton);
                break;
            case Sentence.NEGATIVE:
                this.check(R.id.attitude_negative_radiobutton);
                break;
            case Sentence.HAPPY:
                this.check(R.id.attitude_happy_radiobutton);
                break;
            case Sentence.SAD:
                this.check(R.id.attitude_sad_radiobutton);
                break;
        }
        getAttitude();
    }
}
