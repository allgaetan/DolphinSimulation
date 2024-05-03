package NaiveModel;

import java.util.List;

import fr.emse.fayol.maqit.simulator.SimFactory;
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
        environment = new ColorGridEnvironment(sp.rows, sp.columns, sp.debug, sp.seed);
    }

    @Override
    public void createGoal() { 
    }

    @Override
    public void createObstacle() {
        for (int i = 0; i < sp.nbobstacle; i++) {
            Fish fish = new Fish("fish" + i, sp.field, sp.debug, environment.getPlace(), sp.colorobstacle, sp.rows, sp.columns, sp.nbobstacle, environment);
            addNewComponent(fish);
        }  
    }

    @Override
    public void createRobot() {
        for (int i = 0; i < sp.nbrobot; i++) {
            Dolphin dolphin = new Dolphin("dolphin" + i, sp.field, sp.debug, environment.getPlace(), sp.colorrobot, sp.rows, sp.columns, environment);
            addNewComponent(dolphin);   
        } 
    }

    @Override
    public void schedule() {
        List <Robot> lr = environment.getRobot();
        for (int i = 0; i < sp.step; i++){
            System.out.println ("Step: " + i);
            System.out.println("Number of fish left : " + (environment.getRobot().size()-sp.nbrobot) + "/" + sp.nbobstacle);
            for (Robot t: lr) {
                for (Robot t2: lr) {
                    for (Message m : ((InteractionRobot) t2).sentMessages) {
                        if (t.getName() != t2.getName()) {
                            ((InteractionRobot) t).handleMessage(m);
                        }
                    }
                }

                int[] posr = t.getLocation();
                Cell[][] neighbors = environment.getNeighbor(t.getX(), t.getY(), t.getField());
                if (t instanceof Dolphin) { // Dolphin behavior
                    Dolphin d = (Dolphin) t;
                    d.setNeighbors(neighbors); // Sets the neighbor grid of the dolphin according to its field of perception
                    d.getFishTarget(); // Gets fish target by checking the closest fish in the perception field
                    d.move(1); // Handles movement (this includes collision handling, i.e., eating a fish or avoiding collision with another dolphin)
                } else if (t instanceof Fish) { // Fish behavior
                    Fish f = (Fish) t;
                    f.move(1);   
                }
                updateEnvironment(posr, t.getLocation());
            }
            refreshGW();
            try {
                Thread.sleep(sp.waittime);
            } catch(InterruptedException ie) {
                System.out.println(ie);
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
        hs.createRobot();
        hs.createObstacle();
        hs.initializeGW();
        hs.schedule();  
    }
}
