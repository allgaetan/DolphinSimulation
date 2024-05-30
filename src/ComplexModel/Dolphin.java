package ComplexModel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.components.Orientation;
import fr.emse.fayol.maqit.simulator.components.Robot;
import fr.emse.fayol.maqit.simulator.components.SituatedComponent;
import fr.emse.fayol.maqit.simulator.environment.Cell;
import fr.emse.fayol.maqit.simulator.environment.GridEnvironment;

/**
 * Complex Model for the Dolphin class
 * @author Gaétan Allaire 
 * @author Karim Bekhti
 * @author Kim-Tchoy Du
 */
public class Dolphin extends ColorInteractionRobot {

    private Map<Integer, int[]> fishPositions; // Map of the positions of the fish in the field of perception
    public GridEnvironment environment;
    private Cell[][] neighbors; // Neighbor cells
    public int[] closestFishPosition; // Position of the closest fish
    public int fishCaught; // Number of fish caught by this dolphin
    private Map<Integer, Integer> targetedFish; // Map of the IDs of the already targeted fish with their distance to their prey

    protected Dolphin(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns, GridEnvironment environment) {
        super(name, field, debug, pos, rgb, rows, columns);
        this.environment = environment;
        this.targetedFish = new HashMap<>();
        this.fishCaught = 0; 
    }

    @Override
    /**
     * Handles the parsing of the messages with the template : "ID;distance"
     */ 
    public void handleMessage(Message msg) { 
        String message = msg.getContent();
        Integer ID = Integer.valueOf(message.split(";")[0]);
        Integer distance = Integer.valueOf(message.split(";")[1]);
        this.targetedFish.put(ID, distance);
    }

    /**
     * Sets the neighbor grid of the dolphin according to the field of perception
     * @param Cell[][] neighbors
     */
    public void setNeighbors(Cell[][] neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Chooses a target between the fish in the field of perception of the dolphin
     * In this model, the dolphin uses the messages received from the other dolphins to pick a target that is not targeted yet.
     */
    public void getFishTarget() {
        this.fishPositions = new HashMap<>();

        for (Cell[] line : neighbors) {
            for (Cell cell : line) {
                if (cell != null) {
                    SituatedComponent onCellComponent = cell.getContent();
                    if (onCellComponent instanceof Robot) {
                        Robot onCellRobot = (Robot) onCellComponent;
                        if (onCellRobot.getName().startsWith("fish")) {
                            this.fishPositions.put(onCellRobot.getId(), onCellRobot.getLocation());
                        }
                    }
                }
            }
        }
        this.closestFishPosition = getClosestFishPosition();
    }

    /**
     * Returns the position of the closest fish in the field of perception of the dolphin
     * In this model, the dolphin uses the messages received from the other dolphins to pick a target that is not targeted yet.
     * It also ensures that, if the closest fish is already targeted, the dolphin that has it as a target is closer than the dolphin choosing a target.
     * @return closestFishPosition
     */
    private int[] getClosestFishPosition() {
        int min = Integer.MAX_VALUE;
        int dx, dy, distance;
        int[] closestFishPosition = null;
        Integer selectedTargetID = null;

        for (Map.Entry<Integer, int[]> entry : this.fishPositions.entrySet()) {
            Integer fishID = entry.getKey();
            int[] fishPosition = entry.getValue();
            dx = fishPosition[0] - this.getX();
            dy = fishPosition[1] - this.getY();
            distance = dx*dx + dy*dy;

            boolean isBetterTarget = false;
            if (this.targetedFish.containsKey(fishID)) {
                int otherDistance = this.targetedFish.get(fishID);
                if (distance < otherDistance) {
                    isBetterTarget = true;
                }
            } else {
                isBetterTarget = true;
            }

            if (isBetterTarget && distance < min) {
                min = distance;
                closestFishPosition = fishPosition;
                selectedTargetID = fishID;
            }
        }
        // Send message
        if (selectedTargetID != null) {
            sendMessage(new Message(getId(), selectedTargetID + ";" + min));
        }
        return closestFishPosition;
    }

    /**
     * Returns the distance between the dolphin and a fish
     * @param fishPosition
     * @return
     */
    public double calculateDistance(int[] fishPosition) {
        int dx = fishPosition[0] - this.getX();
        int dy = fishPosition[1] - this.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    /**
     * Main moving logic of the dolphin in the naive model.
     * If a closest fish exists (i.e. if at least one fish is in the field of perception of the dolphin), the dolphin will take the direction that brings it the closest to its target.
     * The dolphin handles collision with another entity with the handleCollision method.
     * If no fish are in the field of perception of the dolphin, it just keeps moving forward.
     * @param arg0 
     */
    public void move(int arg0) {
        this.updatePerception(this.neighbors);   
        Cell[][] grid = environment.getGrid();
        Cell nextCell = null;

        // Orientation decision process
        if (closestFishPosition != null) {
            int dx = closestFishPosition[0] - this.getX();
            int dy = closestFishPosition[1] - this.getY();
            if (Math.abs(dx) >= Math.abs(dy)) {
                if (dx > 0) {
                    this.setCurrentOrientation(Orientation.down);
                    nextCell = grid[this.getX()+1][this.getY()];  
                } else {
                    this.setCurrentOrientation(Orientation.up);
                    nextCell = grid[this.getX()-1][this.getY()];
                }
            } else {
                if (dy > 0) {
                    this.setCurrentOrientation(Orientation.right);
                    nextCell = grid[this.getX()][this.getY()+1];
                } else {
                    this.setCurrentOrientation(Orientation.left);
                    nextCell = grid[this.getX()][this.getY()-1];
                }
            } 
        }
        
        // Moving process : Border handling
        int border = border(this.getX(), this.getY(), grid);
        if (border > 0) { 
            if (border == 4) {
                this.setCurrentOrientation(Orientation.down);
                nextCell = grid[this.getX()+1][this.getY()];  
            } else if (border == 3) {
                this.setCurrentOrientation(Orientation.left);
                nextCell = grid[this.getX()][this.getY()-1];
            } else if (border == 2) {
                this.setCurrentOrientation(Orientation.up);
                nextCell = grid[this.getX()-1][this.getY()];
            } else if (border == 1) {
                this.setCurrentOrientation(Orientation.right);
                nextCell = grid[this.getX()][this.getY()+1];
            }
            this.updatePerception(this.neighbors);  
            this.moveForward();
            return;
        }
        // Moving process : Collision handling
        if (nextCell != null) {
            this.handleCollision(nextCell);
            this.updatePerception(this.neighbors);  
            this.moveForward();
            return;
        }
        // Moving process : General movement
        if (this.freeForward() && this.freeBackward() && this.freeLeft() && this.freeRight()) {
            this.updatePerception(this.neighbors);  
            this.moveForward();
            return;
        }
        return;
    }

    /**
     * Collisions are handled by checking the type of the object in the cell that is forward to the dolphin.
     * If the entity is a fish, the fish is "eaten" and the content of the cell is removed.
     * If the entity is a dolphin, it turns away to avoid collision.
     * @param nextCell
     */
    private void handleCollision(Cell nextCell) {
        SituatedComponent onCellComponent = nextCell.getContent();
        if (onCellComponent != null) {
            if (onCellComponent instanceof Robot) {
                Robot onCellRobot = (Robot) onCellComponent;
                if (onCellRobot.getName().startsWith("fish")) {       
                    nextCell.removeContent();
                    this.turnLeft();
                    this.fishCaught++; // Increment fish caught count
                } else if (onCellRobot.getName().startsWith("dolphin")) {
                    this.turnLeft();
                    this.turnLeft();
                }
                return;
            }
        }
    }

    /**
     * Returns an integer to detect if the dolphin is on a border :
     * 0 means the dolphin is not on a border
     * 1 means the dolphin is on the left border
     * 2 means the dolphin is on the bottom border
     * 3 means the dolphin is on the right border
     * 4 means the dolphin is on the top border
     * @param x
     * @param y
     * @param grid
     * @return int
     */
    private int border(int x, int y, Cell[][] grid) { 
        if (x == 0) {
            return 4;
        } else if (y == 0) {
            return 1;
        } else if (x == grid.length-1) {
            return 2;
        } else if (y == grid.length-1) {
            return 3;
        } else {
            return 0;
        }
    }
}
