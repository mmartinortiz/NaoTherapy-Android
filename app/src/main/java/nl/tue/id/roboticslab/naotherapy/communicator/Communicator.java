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

package nl.tue.id.roboticslab.naotherapy.communicator;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import nl.tue.id.roboticslab.naotherapy.Utilities;
import nl.tue.id.roboticslab.naotherapy.therapy.Behaviour;

/**
 * Created by Manuel Martín-Ortiz <mmartinortiz@gmail.com> on 12-9-14.
 */
public class Communicator {
    private Context mContext;

    public static final String COMMAND_SAY = "say";
    public static final String COMMAND_GET = "get";
    public static final String PARAMETER_AVAILABLE_LANGUAGES = "available_languages";
    public static final String PARAMETER_CURRENT_LANGUAGE = "current_language";
    public static final String PARAMETER_AVAILABLE_BEHAVIOURS = "available_behaviours";

    public Communicator(Context context) {
        mContext = context;
    }

    public JSONObject sendMessage(JSONObject message) {
        JSONObject response = null;

        try {
            // Setup
            String ip = Utilities.getRobotIp(mContext);
            if (ip.isEmpty()) return response;
            Socket socket = new Socket(ip, 12345);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // Send message
            byte[] outBuffer = message.toString().getBytes("UTF-8");
            out.write(outBuffer);
            out.flush();

            // receive message
            byte[] inBuffer = new byte[1024];
            int size = in.read(inBuffer);

            if (size > 0) {
                String JSONString = new String(inBuffer, "UTF-8");
                response = new JSONObject(JSONString);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void manageException(Exception e) {
        e.printStackTrace();
    }

    public JSONObject updateBatteryLevel() {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "get");
            message.put("parameter", "battery");
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject say(String sentence, String attitude) {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "say");
            message.put("sentence", sentence);
            message.put("attitude", attitude);
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject setVolume(double volume) {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "set");
            message.put("parameter", "volume");
            message.put("value", volume);
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject setBehaviour(Behaviour behaviour) {
        return setBehaviour(behaviour.getFullName());
    }

    public JSONObject setBehaviour(String behaviourName) {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "set");
            message.put("parameter", "behaviour");
            message.put("value", behaviourName);
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject removeStiffness() {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "set");
            message.put("parameter", "stiffness");
            message.put("value", "off");
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject setLanguage(String language) {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "set");
            message.put("parameter", "language");
            message.put("value", language);
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject stopAllBehaviours() {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "stop");
            message.put("behaviour", "all");
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject stopBehaviour(Behaviour behaviour) {
        return stopBehaviour(behaviour.getFullName());
    }

    public JSONObject stopBehaviour(String behaviour) {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "stop");
            message.put("behaviour", behaviour);
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    private JSONObject updateAvailableBehaviours() {
        // Retrieve available behaviours from the robot
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "get");
            message.put("parameter", "available_behaviours");
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject updateAvailableLanguages() {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "get");
            message.put("parameter", "available_languages");
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }

    public JSONObject getCurrentLanguage() {
        JSONObject message = new JSONObject();
        JSONObject response = null;
        try {
            message.put("command", "get");
            message.put("parameter", "current_language");
            response = this.sendMessage(message);
        } catch (Exception e) {
            manageException(e);
        }
        return response;
    }
}
