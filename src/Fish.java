import java.awt.Color;
import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;

public class Fish extends ColorInteractionRobot {

    protected Fish(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns) {
        super(name, field, debug, pos, rgb, rows, columns);
    }

    public void handleMessage(Message arg0) {
    }

    @Override
    public void move(int arg0) {
        //sendMessage(new Message(getId(), x+";"+y));
    }
}
