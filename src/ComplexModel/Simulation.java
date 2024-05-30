package ComplexModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.emse.fayol.maqit.simulator.SimFactory;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.components.Robot;
import fr.emse.fayol.maqit.simulator.configuration.IniFile;
import fr.emse.fayol.maqit.simulator.configuration.SimProperties;
import fr.emse.fayol.maqit.simulator.environment.Cell;
import fr.emse.fayol.maqit.simulator.environment.ColorGridEnvironment;

public class Simulation extends SimFactory {

    private int totalFishCaught;
    private double totalDistance;
    private int totalDistancesMeasured;
    private BufferedWriter writer;

    public Simulation(SimProperties sp) {
        super(sp);
        // Initialize metrics
        this.totalFishCaught = 0;
        this.totalDistance = 0.0;
        this.totalDistancesMeasured = 0;
        // Write metrics to a CSV file
        try {
            writer = new BufferedWriter(new FileWriter("complex_model_metrics.csv"));
            writer.write("Step;TotalFishCaught;AverageFishCaughtPerDolphin;AverageDistanceToFish\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createEnvironment() {
        environment = new ColorGridEnvironment(sp.rows, sp.columns, sp.debug, sp.seed);
    }

    @Override
    public void createGoal() { 
    }

    @Override
    public void createObstacle() { // Spawns fish
        for (int i = 0; i < sp.nbobstacle; i++) {
            Fish fish = new Fish("fish" + i, sp.field, sp.debug, environment.getPlace(), sp.colorobstacle, sp.rows, sp.columns, sp.nbobstacle, environment);
            addNewComponent(fish);
        }  
    }

    @Override
    public void createRobot() { // Spawns dolphins
        for (int i = 0; i < sp.nbrobot; i++) {
            Dolphin dolphin = new Dolphin("dolphin" + i, 10*sp.field, sp.debug, environment.getPlace(), sp.colorrobot, sp.rows, sp.columns, environment);
            addNewComponent(dolphin);   
        } 
    }

    @Override
    public void schedule() {
        for (int i = 0; i < sp.step; i++){
            List <Robot> lr = environment.getRobot();
            int fishLeft = (environment.getRobot().size()-sp.nbrobot);
            if (fishLeft == 0) {
                System.out.println("No fish left, end of the simulation");
                break;
            }
            System.out.println ("Step: " + i);
            System.out.println("Number of fish left : " + fishLeft + "/" + sp.nbobstacle);
            int stepFishCaught = 0;
            double stepTotalDistance = 0.0;
            int stepDistancesMeasured = 0;
            
            for (Robot t: lr) {
                int[] posr = t.getLocation();
                Cell[][] neighbors = environment.getNeighbor(t.getX(), t.getY(), t.getField());
                // Dolphin behavior
                if (t instanceof Dolphin) { 
                    Dolphin d = (Dolphin) t;
                    d.setNeighbors(neighbors); // Sets the neighbor grid of the dolphin according to its field of perception
                    // Communication handling
                    for (Robot t2: lr) {
                        if (t2 instanceof Dolphin) { 
                            Dolphin d2 = (Dolphin) t2;
                            for (Message m : d2.sentMessages) {
                                if (d.getName() != d2.getName()) {
                                    d.handleMessage(m);
                                }
                            }
                        }           
                    }
                    d.getFishTarget(); // Gets fish target by checking the closest fish in the perception field
                    d.move(1); // Handles movement (this includes collision handling, i.e., eating a fish or avoiding collision with another dolphin)
                    
                    // Keep track of metrics
                    stepFishCaught += d.fishCaught;
                    if (d.closestFishPosition != null) {
                        stepTotalDistance += d.calculateDistance(d.closestFishPosition);
                        stepDistancesMeasured++;
                    }
                } 
                // Fish behavior
                else if (t instanceof Fish) { 
                    Fish f = (Fish) t;
                    f.setNeighbors(neighbors); // Sets the neighbor grid of the fish according to its field of perception
                    f.move(1); // Handles movement (this includes collision handling) 
                }
                // Environment update
                updateEnvironment(posr, t.getLocation());
            }

            // Updating global metrics
            totalFishCaught = stepFishCaught;
            totalDistance = stepTotalDistance;
            totalDistancesMeasured = stepDistancesMeasured;
            // Processes data for visualization
            double avgFishCaughtPerDolphin = totalFishCaught / (double) sp.nbrobot;
            double avgDistanceToFish = (totalDistancesMeasured > 0) ? totalDistance / totalDistancesMeasured : 0.0;
            // Write the data to the CSV file
            try {
                writer.write(i + ";" + totalFishCaught + ";" + avgFishCaughtPerDolphin + ";" + avgDistanceToFish + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
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
