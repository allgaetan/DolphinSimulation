package NaiveModel;

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

public class Dolphin extends ColorInteractionRobot {

    private Map<Integer, int[]> fishPositions;
    public GridEnvironment environment;
    private Cell[][] neighbors;
    private int[] closestFishPosition;

    protected Dolphin(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns, GridEnvironment environment) {
        super(name, field, debug, pos, rgb, rows, columns);
        this.environment = environment;
    }

    @Override
    public void handleMessage(Message msg) {
    }

    public void setNeighbors(Cell[][] neighbors) {
        this.neighbors = neighbors;
    }

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
        closestFishPosition = getClosestFishPosition();
    }

    private int[] getClosestFishPosition() {
        int min = Integer.MAX_VALUE;
        int[] closestFishPosition = null;
        for (int[] fishPosition : this.fishPositions.values()) {
            int dx = fishPosition[0] - this.getX();
            int dy = fishPosition[1] - this.getY();
            int distance = dx*dx + dy*dy;
            if (distance < min) {
                min = distance;
                closestFishPosition = fishPosition;
            }
        }
        return closestFishPosition;
    }

    @Override
    public void move(int arg0) {
        Cell[][] grid = environment.getGrid();
        Cell currentCell = grid[this.getX()][this.getY()];
        Cell nextCell = null; // This will contain the next cell according to the direction decision if the movement is allowed
        Orientation orientation = getCurrentOrientation();

        if (closestFishPosition != null) {
            // Computes width and height distance to the closest fish
            int dx = closestFishPosition[0] - this.getX();
            int dy = closestFishPosition[1] - this.getY();
            // Chooses the optimal orientation considering the vertical and horizontal distance
            if (Math.abs(dx) >= Math.abs(dy)) {
                if (dx > 0) {
                    if (orientation.toString() == "up") {
                        this.turnLeft();
                        this.turnLeft();
                    }
                    if (orientation.toString() == "left") {
                        this.turnLeft();
                    }
                    if (orientation.toString() == "right") {
                        this.turnRight();
                    }
                    nextCell = grid[this.getX()+1][this.getY()];
                } else {
                    if (orientation.toString() == "down") {
                        this.turnLeft();
                        this.turnLeft();
                    }
                    if (orientation.toString() == "left") {
                        this.turnRight();
                    }
                    if (orientation.toString() == "right") {
                        this.turnLeft();
                    }
                    nextCell = grid[this.getX()-1][this.getY()];
                }
            } else {
                if (dy > 0) {
                    if (orientation.toString() == "left") {
                        this.turnLeft();
                        this.turnLeft();
                    }
                    if (orientation.toString() == "down") {
                        this.turnLeft();
                    }
                    if (orientation.toString() == "up") {
                        this.turnRight();
                    }
                    nextCell = grid[this.getX()][this.getY()+1];
                } else {
                    if (orientation.toString() == "right") {
                        this.turnLeft();
                        this.turnLeft();
                    }
                    if (orientation.toString() == "down") {
                        this.turnRight();
                    }
                    if (orientation.toString() == "up") {
                        this.turnLeft();
                    }
                    nextCell = grid[this.getX()][this.getY()-1];
                }
            }
            this.updatePerception(this.neighbors);    
            handleCollision(nextCell);
        }
        this.moveForward(); // Keeps moving forward anyway if no fish are in the field
    }

    private void handleCollision(Cell nextCell) {
        SituatedComponent onCellComponent = nextCell.getContent();
        if (onCellComponent != null) {
            if (onCellComponent instanceof Robot) {
                Robot onCellRobot = (Robot) onCellComponent;
                if (onCellRobot.getName().startsWith("fish")) {       
                    nextCell.removeContent();
                } else if (onCellRobot.getName().startsWith("dolphin")) {
                    this.turnLeft();
                    this.turnLeft();
                }
            }
        }
    }
}
