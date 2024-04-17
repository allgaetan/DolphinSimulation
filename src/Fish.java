import java.awt.Color;

import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;

public class Fish extends ColorInteractionRobot {

    protected Fish(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns) {
        super(name, field, debug, pos, rgb, rows, columns);
    }

    @Override
    public void handleMessage(Message arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void move(int arg0) {
        this.readMessages();
        if (this.freeForward()) {
            this.moveForward();
        }
        else {
            this.turnRight();
        }
        sendMessage(new Message(getId(), "Coordinates [" + x + "," + y + "]"));
    }
}
