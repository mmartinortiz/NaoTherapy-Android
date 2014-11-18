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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 9-4-14.
 * <p/>
 * The therapy objects will store all related operations regarded with the Therapy.
 * <p/>
 * All therapies have a name. This name will be used to store the therapy components in a file
 * <p/>
 * The therapy elements are "sentences" and "behaviours". Every time a sentence/behaviour is
 * added/removed the changes are reflected in the files.
 */
public class Therapy {
    private List<Sentence> sentences = new ArrayList<Sentence>();
    private List<Behaviour> behaviours = new ArrayList<Behaviour>();
    private List<Emotion> emotions = new ArrayList<Emotion>();
    private String name = "";
    private String type = "";
    private String language = "";

    public Therapy() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sentence> getSentencesByLanguages(String language) {
        List<Sentence> sentences = new ArrayList<Sentence>();

        for (Sentence sentence : this.sentences)
            if (sentence.getLanguage().equalsIgnoreCase(language))
                sentences.add(sentence);

        return sentences;
    }

    public boolean hasBehaviour(Behaviour behaviour) {
        return behaviours.contains(behaviour);
    }

    public boolean hasSentence(Sentence sentence) {
        return sentences.contains(sentence);
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public List<Behaviour> getBehaviours() {
        return behaviours;
    }

    public void setBehaviours(List<Behaviour> behaviours) {
        this.behaviours = behaviours;
    }

    public void addBehaviour(Behaviour behaviour) {
        if (!behaviours.contains(behaviour)) behaviours.add(behaviour);
    }

    public void addSentence(Sentence sentence) {
        if (!sentences.contains(sentence)) sentences.add(sentence);
    }

    public void addEmotion(Emotion emotion) {
        if (!emotions.contains(emotion)) emotions.add(emotion);
    }

    public void removeBehaviour(Behaviour behaviour) {
        if (behaviours.contains(behaviour)) behaviours.remove(behaviour);
    }

    public void removeSentence(Sentence sentence) {
        if (sentences.contains(sentence)) sentences.remove(sentence);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Emotion> getEmotions() {
        return emotions;
    }

    public Emotion getEmotionByName(String name) {
        Emotion emotion = null;

        for (Emotion e : emotions) {
            if (e.getName().equalsIgnoreCase(name))
                emotion = e;
        }
        return emotion;
    }

    public String getType() {
        return type;
    }

    public String getLanguage() {
        return language;
    }
}
