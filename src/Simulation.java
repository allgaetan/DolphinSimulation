import java.util.List;

import fr.emse.fayol.maqit.simulator.SimFactory;
import fr.emse.fayol.maqit.simulator.components.ColorObstacle;
import fr.emse.fayol.maqit.simulator.components.InteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.components.Robot;
import fr.emse.fayol.maqit.simulator.configuration.IniFile;
import fr.emse.fayol.maqit.simulator.configuration.SimProperties;
import fr.emse.fayol.maqit.simulator.environment.Cell;
import fr.emse.fayol.maqit.simulator.environment.ColorGridEnvironment;

public class Simulation extends SimFactory {

    public Simulation(SimProperties sp) {
        super(sp);
    }

    @Override
    public void createEnvironment() {
        environment = new ColorGridEnvironment(sp.rows, sp.columns, sp.debug, sp.seed );
    }

    @Override
    public void createGoal() {
        // TODO Auto-generated method stub
    }

    @Override
    public void createObstacle() {
        for (int i = 0; i < sp.nbobstacle; i++) {
            ColorObstacle co = new ColorObstacle(environment.getPlace(), sp.colorobstacle);
            addNewComponent(co);   
        }   
    }

    @Override
    public void createRobot() {
        for (int i = 0; i < sp.nbrobot; i++) {
            Dolphin dolphin = new Dolphin("dolphin" + i, sp.field, sp.debug, environment.getPlace(), sp.colorrobot, sp.rows, sp.columns);
            addNewComponent(dolphin);   
            Fish fish = new Fish("fish" + i, sp.field, sp.debug, environment.getPlace(), sp.colorobstacle, sp.rows, sp.columns);
            addNewComponent(fish);
        } 
    }

    @Override
    public void schedule() {
        List <Robot> lr = environment.getRobot();
        for (int i = 0; i < sp.step; i++){
            //System.out.println ("Step: " + i);
            for (Robot t: lr) {
                for (Robot t2: lr) {
                    for (Message m : ((InteractionRobot) t2).popSentMessages()) {
                        if (t.getId() != t2.getId()) {
                            ((InteractionRobot) t).receiveMessage(m);
                            ((InteractionRobot) t).handleMessage(m);
                            //System.out.println(m);
                        }
                    }
                }
                int[] posr = t.getLocation();
                Cell[][] p = environment.getNeighbor(t.getX(), t.getY(), t.getField ());
                t.updatePerception(p);
                t.move(1);
                updateEnvironment(posr, t.getLocation());
            }
            refreshGW();
            try {
                Thread.sleep(sp.waittime);
            } catch(InterruptedException ie) {
                System.out.println (ie);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        IniFile ifile = new IniFile("parameters/configuration.ini");
        SimProperties sp = new SimProperties(ifile);
        sp.simulationParams();
        sp.displayParams();
        Simulation hs = new Simulation(sp);
        hs.createEnvironment();
        hs.createObstacle();
        hs.createRobot();
        hs.initializeGW();
        hs.schedule();  
    }
}
