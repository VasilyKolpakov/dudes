package com.tearulez.dudes;

/**
 * Created by Teplovoz on 6/4/2016.
 */
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.tearulez.dudes.Network.AddCharacter;
import com.tearulez.dudes.Network.Login;
import com.tearulez.dudes.Network.UpdateCharacter;
import com.tearulez.dudes.Network.Register;
import com.tearulez.dudes.Network.RegistrationRequired;
import com.tearulez.dudes.Network.RemoveCharacter;
import com.tearulez.dudes.Network.NewCharacterState;
import com.esotericsoftware.minlog.Log;

public class PositionClient {
    UI ui;
    Client client;
    String name;

    public PositionClient () {
        client = new Client();
        client.start();

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(client);

        // ThreadedListener runs the listener methods on a different thread.
        client.addListener(new ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
                if (object instanceof RegistrationRequired) {
                    Register register = new Register();
                    register.name = name;
                    register.otherStuff = ui.inputOtherStuff();
                    client.sendTCP(register);
                }

                if (object instanceof AddCharacter) {
                    AddCharacter msg = (AddCharacter)object;
                    ui.addCharacter(msg.character);
                    return;
                }

                if (object instanceof NewCharacterState) {
                    ui.updateCharacter((NewCharacterState)object);
                    return;
                }

                if (object instanceof RemoveCharacter) {
                    RemoveCharacter msg = (RemoveCharacter)object;
                    ui.removeCharacter(msg.id);
                    return;
                }
            }

            public void disconnected (Connection connection) {
                System.exit(0);
            }
        }));

        ui = new UI();

        String host = ui.inputHost();
        try {
            client.connect(5000, host, Network.port);
            // Server communication after connection can go here, or in Listener#connected().
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        name = ui.inputName();
        Login login = new Login();
        login.name = name;
        client.sendTCP(login);

        while (true) {
            int ch;
            try {
                ch = System.in.read();
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }

            UpdateCharacter msg = new UpdateCharacter();
            switch (ch) {
                case 'w':
                    msg.y = -1;
                    break;
                case 's':
                    msg.y = 1;
                    break;
                case 'a':
                    msg.x = -1;
                    break;
                case 'd':
                    msg.x = 1;
                    break;
                default:
                    msg = null;
            }
            if (msg != null) client.sendTCP(msg);
        }
    }

    static class UI {
        HashMap<Integer, Character> characters = new HashMap();

        public String inputHost () {
            String input = (String)JOptionPane.showInputDialog(null, "Host:", "Connect to server", JOptionPane.QUESTION_MESSAGE,
                    null, null, "localhost");
            if (input == null || input.trim().length() == 0) System.exit(1);
            return input.trim();
        }

        public String inputName () {
            String input = (String)JOptionPane.showInputDialog(null, "Name:", "Connect to server", JOptionPane.QUESTION_MESSAGE,
                    null, null, "Test");
            if (input == null || input.trim().length() == 0) System.exit(1);
            return input.trim();
        }

        public String inputOtherStuff () {
            String input = (String)JOptionPane.showInputDialog(null, "Other Stuff:", "Create account", JOptionPane.QUESTION_MESSAGE,
                    null, null, "other stuff");
            if (input == null || input.trim().length() == 0) System.exit(1);
            return input.trim();
        }

        public void addCharacter (Character character) {
            characters.put(character.id, character);
            System.out.println(character.name + " added at " + character.x + ", " + character.y);
        }

        public void updateCharacter (NewCharacterState msg) {
            Character character = characters.get(msg.id);
            if (character == null) return;
            character.x = msg.x;
            character.y = msg.y;
            System.out.println(character.name + " moved to " + character.x + ", " + character.y);
        }

        public void removeCharacter (int id) {
            Character character = characters.remove(id);
            if (character != null) System.out.println(character.name + " removed");
        }
    }

    public static void main (String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        new PositionClient();
    }
}