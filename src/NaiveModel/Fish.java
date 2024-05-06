package NaiveModel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.components.Orientation;
import fr.emse.fayol.maqit.simulator.components.Robot;
import fr.emse.fayol.maqit.simulator.components.SituatedComponent;
import fr.emse.fayol.maqit.simulator.environment.Cell;
import fr.emse.fayol.maqit.simulator.environment.GridEnvironment;

/**
 * Fish class
 * 
 * @author Gaétan Allaire 
 * @author Karim Bekhti
 * @author Kim-Tchoy Du
 */
public class Fish extends ColorInteractionRobot {

    private Map<Integer, int[]> dolphinPositions;
    public GridEnvironment environment;
    private Cell[][] neighbors;
    private int[] closestDolphinPosition;
    
    protected Fish(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns, int nbobstacle, GridEnvironment environment) {
        super(name, field, debug, pos, rgb, rows, columns);
        this.environment = environment;
    }

    @Override
    public void handleMessage(Message msg) {
    }

    public void setNeighbors(Cell[][] neighbors) {
        this.neighbors = neighbors;
    }

    public void getDolphinPrey() {
        this.dolphinPositions = new HashMap<>();
        for (Cell[] line : neighbors) {
            for (Cell cell : line) {
                if (cell != null) {
                    SituatedComponent onCellComponent = cell.getContent();
                    if (onCellComponent instanceof Robot) {
                        Robot onCellRobot = (Robot) onCellComponent;
                        if (onCellRobot.getName().startsWith("dolphin")) {
                            this.dolphinPositions.put(onCellRobot.getId(), onCellRobot.getLocation());
                        }
                    }
                }
            }
        }
        this.closestDolphinPosition = getClosestDolphinPosition();
    }

    private int[] getClosestDolphinPosition() {
        int min = Integer.MAX_VALUE;
        int[] closestDolphinPosition = null;
        for (int[] dolphinPosition : this.dolphinPositions.values()) {
            int dx = dolphinPosition[0] - this.getX();
            int dy = dolphinPosition[1] - this.getY();
            int distance = dx*dx + dy*dy;
            if (distance < min) {
                min = distance;
                closestDolphinPosition = dolphinPosition;
            }
        }
        return closestDolphinPosition;
    }

    @Override
    public void move(int arg0) {
        Cell[][] grid = environment.getGrid();
        Cell nextCell = null; // This will contain the next cell according to the direction decision if the movement is allowed

        /*if (closestDolphinPosition != null) {
            int dx = closestDolphinPosition[0] - this.getX();
            int dy = closestDolphinPosition[1] - this.getY();
            if (Math.abs(dx) >= Math.abs(dy)) {
                if (dx > 0) {
                    this.setCurrentOrientation(Orientation.up);
                    nextCell = grid[this.getX()+1][this.getY()];  
                } else {
                    this.setCurrentOrientation(Orientation.down);
                    nextCell = grid[this.getX()-1][this.getY()];
                }
            } else {
                if (dy > 0) {
                    this.setCurrentOrientation(Orientation.left);
                    nextCell = grid[this.getX()][this.getY()+1];
                } else {
                    this.setCurrentOrientation(Orientation.right);
                    nextCell = grid[this.getX()][this.getY()-1];
                }
            }
            this.updatePerception(this.neighbors);    
            handleCollision(nextCell);
        }*/

        Random Random = new Random();
        int randomMovement = Random.nextInt(10);
        int border = border(field, this.getX(), this.getY(), grid);

        if ((border == 0) && (randomMovement == 1)) { //randomMovement == 1
            this.randomOrientation();
            if (this.orientation == Orientation.down) {
                nextCell = grid[this.getX()+1][this.getY()];  
            } else if (this.orientation == Orientation.left) {
                nextCell = grid[this.getX()][this.getY()-1];
            } else if (this.orientation == Orientation.up) {
                nextCell = grid[this.getX()-1][this.getY()];
            } else {
                nextCell = grid[this.getX()][this.getY()+1];
            }
            this.updatePerception(this.neighbors);    
            handleCollision(nextCell);
            this.moveForward(); 
            return;
        }
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
            } else {
                this.setCurrentOrientation(Orientation.right);
                nextCell = grid[this.getX()][this.getY()+1];
            }
            this.updatePerception(this.neighbors);    
            this.handleCollision(nextCell);
            this.moveForward(); 
            return;
        }
    }

    private int border(int field, int x, int y, Cell[][] grid) { // If the fish is near (field distance) of a border
        if (x < field) {
            return 4;
        } else if (y < field) {
            return 1;
        } else if (x > grid.length-field) {
            return 2;
        } else if (y > grid.length-field) {
            return 3;
        } else {
            return 0;
        }
    }

    private void handleCollision(Cell nextCell) {
        SituatedComponent onCellComponent = nextCell.getContent();
        if (onCellComponent != null) {
            if (onCellComponent instanceof Robot) {
                this.turnLeft();
                this.turnLeft();    
            }
        }
    }
}
