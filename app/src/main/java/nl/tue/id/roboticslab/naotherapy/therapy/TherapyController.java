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

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 9-4-14.
 */
public class TherapyController {

    private static final String EXTENSION = ".xml";
    public static final String THERAPIES_DIRECTORY = "therapies";
    private static final String THERAPY = "therapy";
    private static final String EMOTION = "emotion";
    private static final String BEHAVIOUR = "behaviour";
    private static final String SENTENCE = "sentence";
    private List<Therapy> therapies = new ArrayList<Therapy>();
    private Context context;

    public TherapyController(Context context) {
        this.context = context;

    }

    public Therapy getTherapy(String therapyName) {
        // If the therapy exists, return a therapy object with all the data
        // if not, returns a new therapy with such name

        // Create a new therapy
        Therapy therapy;

        // Get a file for the therapy
        File therapyFile = getFileForTherapy(therapyName);

        if (therapyFile.exists()) {
            // If the therapy exists, fill the attributes from the file
            therapy = loadFromFile(therapyFile);
        } else {
            therapy = new Therapy();
            // If not just assign a name
            therapy.setName(therapyName);
        }

        return therapy;
    }

    public boolean delete(String therapyName) {
        File therapyFile = getFileForTherapy(therapyName);

        return therapyFile.delete();
    }

    public boolean delete(Therapy therapy) {
        return delete(therapy.getName());
    }

    public void save(Therapy therapy) {
        // This method overwrite any previous file with the same name. Do not ask for overwrite
        try {
            // Get a file for the therapy. The file has the same file that the therapy
            File therapyFile = getFileForTherapy(therapy);

            // Create the output file
            FileOutputStream myFile = new FileOutputStream(therapyFile);

            // Create serializer
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);

            //start Document
            xmlSerializer.startDocument("UTF-8", true);

            //open tag <therapy>
            xmlSerializer.startTag("", "therapy");
            xmlSerializer.attribute("", "name", therapy.getName());
            xmlSerializer.attribute("", "type", therapy.getType());
            xmlSerializer.attribute("", "language", therapy.getLanguage());

            // Create one tag 'behaviour' for each behaviour
            if (!therapy.getBehaviours().isEmpty()) {
                for (Behaviour behaviour : therapy.getBehaviours()) {
                    xmlSerializer.startTag("", "behaviour");
                    xmlSerializer.attribute("", "name", behaviour.getSimpleName());
                    xmlSerializer.attribute("", "package", behaviour.getAldebaranPackage());
                    xmlSerializer.endTag("", "behaviour");
                }
            }

            // Create one tag 'predefined_sentence' for each Sentence
            if (!therapy.getSentences().isEmpty()) {
                for (Sentence sentence : therapy.getSentences()) {
                    xmlSerializer.startTag("", "sentence");
                    xmlSerializer.attribute("", "sentence", sentence.getSentence());
                    xmlSerializer.attribute("", "language", sentence.getLanguage());
                    xmlSerializer.attribute("", "attitude", sentence.getAttitudeAsString());
                    xmlSerializer.endTag("", "sentence");
                }
            }

            xmlSerializer.endTag("", "therapy");
            xmlSerializer.endDocument();

            // Close the file
            myFile.write(writer.toString().getBytes());
            myFile.flush();
            myFile.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());

        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    public List<String> getAvailableTherapies() {
        final List<String> therapies = new ArrayList<String>();
        File dir = context.getDir(THERAPIES_DIRECTORY, Context.MODE_PRIVATE);

        // Check if they contain a therapy
        for (String candidate : dir.list()) {
            if (candidate.endsWith(EXTENSION)) {
                // Remove extension from file
                candidate = candidate.replace(EXTENSION, "");
                // @TODO:
                // Check that the candidate is a properly formed therapy

                // Add candidate to valid therapies
                therapies.add(candidate);
            }
        }

        return therapies;
    }

    private File getFileForTherapy(String name) {
        File basedir = context.getDir(THERAPIES_DIRECTORY, MODE_PRIVATE);
        String filename = name + EXTENSION;

        return new File(basedir, filename);
    }

    private File getFileForTherapy(Therapy therapy) {
        return getFileForTherapy(therapy.getName());
    }

    private Therapy loadFromFile(File therapyFile) {
        Therapy therapy = new Therapy();
        Emotion emotion = null;
        Behaviour behaviour = null;
        Sentence sentence = null;

        // Load data from XML
        XmlPullParserFactory pullParserFactory;
        try {
            // Create factory
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            FileInputStream in = new FileInputStream(therapyFile);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (tagName.equalsIgnoreCase(THERAPY)) {
                            String name = parser.getAttributeValue("", "name");
                            String type = parser.getAttributeValue("", "type");
                            String language = parser.getAttributeValue("", "language");
                            therapy.setName(name);
                            therapy.setType(type);
                            therapy.setLanguage(language);
                        }

                        if (tagName.equalsIgnoreCase(BEHAVIOUR)) {
                            String name = parser.getAttributeValue("", "name");
                            String pkg = parser.getAttributeValue("", "package");
                            if (pkg == null) pkg = "";
                            behaviour = new Behaviour(name, pkg);
                        }

                        if (tagName.equalsIgnoreCase(SENTENCE)) {
                            String text = parser.getAttributeValue("", "sentence");
                            String language = parser.getAttributeValue("", "language");
                            String attitude = parser.getAttributeValue("", "attitude");

                            sentence = new Sentence(text, language, Sentence.getAttitudeAsInt(attitude));
                        }

                        if (tagName.equalsIgnoreCase(EMOTION)) {
                            String group = parser.getAttributeValue("", "group");
                            String name = parser.getAttributeValue("", "name");

                            // Create a new instance of emotion
                            emotion = new Emotion(group, name);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase(BEHAVIOUR)) {
                            therapy.addBehaviour(behaviour);
                            if (emotion != null) {
                                emotion.setBehaviour(behaviour);
                            }
                        }
                        if (tagName.equalsIgnoreCase(EMOTION)) {
                            therapy.addEmotion(emotion);
                        }
                        if (tagName.equalsIgnoreCase(SENTENCE)) {
                            therapy.addSentence(sentence);
                        }
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return therapy;
    }
}
