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

package nl.tue.id.roboticslab.naotherapy.therapy;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 15-9-14.
 */
public class Emotion {
    private String mGroup;
    private String mName;
    private Behaviour mBehaviour;

    public Emotion(String group, String name) {
        mGroup = group;
        mName = name;
    }

    public void setBehaviour(Behaviour behaviour) {
        mBehaviour = behaviour;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Emotion &&
                (this.mGroup.equals(((Emotion) object).getGroup())) &&
                (this.mName.equals(((Emotion) object).getName())) &&
                (this.mBehaviour.equals(((Emotion) object).getBehaviour()));
    }

    public String getGroup() {
        return mGroup;
    }

    public String getName() {
        return mName;
    }

    public Behaviour getBehaviour() {
        return mBehaviour;
    }

    public String toString() {
        return mName;
    }
}
