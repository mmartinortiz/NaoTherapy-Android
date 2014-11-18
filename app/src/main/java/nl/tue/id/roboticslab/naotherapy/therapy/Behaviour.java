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
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 9-4-14.
 */
public class Behaviour {
    private String mName = "";
    private String mImage = "";
    private String mAldebaranPackage = "";

    public Behaviour(String name, String aldebaranPackage) {
        mName = name;
        mImage = "";
        mAldebaranPackage = aldebaranPackage;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Behaviour && (this.mName.equals(((Behaviour) object).getFullName()));
    }

    public String getFullName() {
        String fullName;
        if (mAldebaranPackage.isEmpty()) {
            fullName = mName;
        } else {
            fullName = mAldebaranPackage + "/" + mName;
        }
        return fullName;
    }

    public String getSimpleName() {
        return mName;
    }

    public String getImage() {
        return mImage;
    }

    public void setPackage(String aPackage) {
        mAldebaranPackage = aPackage;
    }

    public String getAldebaranPackage() {
        return mAldebaranPackage;
    }
}
