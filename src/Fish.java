import java.awt.Color;
import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.environment.GridEnvironment;

public class Fish extends ColorInteractionRobot {

    public GridEnvironment environment;

    protected Fish(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns, GridEnvironment environment) {
        super(name, field, debug, pos, rgb, rows, columns);
        this.environment = environment;
    }

    public void handleMessage(Message arg0) {
    }

    @Override
    public void move(int arg0) {
    }
}
