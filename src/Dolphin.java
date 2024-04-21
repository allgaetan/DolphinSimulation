import java.awt.Color;
import java.util.HashMap;
import java.util.List;
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

    protected Dolphin(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns, GridEnvironment environment) {
        super(name, field, debug, pos, rgb, rows, columns);
        this.fishPositions = new HashMap<>();
        this.environment = environment;

    }

    @Override
    public void handleMessage(Message msg) {
        int emitter = msg.getEmitter();
        int[] position = parsePosition(msg.getContent());
        this.fishPositions.put(emitter, position);
    }

    private int[] parsePosition(String content) {
        String[] coordinates = content.split(";");
        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);
        return new int[] {x, y};
    }

    @Override
    public void move(int arg0) {
        //sendMessage(new Message(getId(), x+";"+y));
        int[] closestFishPosition = getClosestFishPosition();
        if (closestFishPosition != null) {
            // Computes width and height distance to the closest fish
            int dx = closestFishPosition[0] - this.getX();
            int dy = closestFishPosition[1] - this.getY();
            // Chooses the optimal orientation
            Orientation orientation = getCurrentOrientation();
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
                }
            }
            // Handles movement or eating

            if (this.freeForward()) {
                this.moveForward();
            } else {
                for (Cell[] row : environment.getNeighbor(this.getX(), this.getY(), this.field)) {
                    for (Cell cell : row) {
                        //System.out.println(cell);
                        if (cell != null && cell.getContent() != null) {
                            System.out.println(cell.getContent());
                            handleFishEating();
                        }
                    }
                }
            }
        }
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

    private void handleFishEating() {
        Cell[][] grid = environment.getGrid(); 
        Cell cell = grid[this.getX()][this.getY()];
        SituatedComponent onCellComponent = cell.getContent();
        if (onCellComponent instanceof Robot) {
            Robot onCellRobot = (Robot) onCellComponent;
            if (onCellRobot.getName().startsWith("fish")) {
                /*CellWrapper cellWrapper = new CellWrapper(cell);
                System.out.println(cell);
                System.out.println(cellWrapper);
                cellWrapper.removeContent();*/

                List <Robot> lr = environment.getRobot();
                System.out.println(lr);
                lr.remove(onCellRobot.getId());
                System.out.println(lr);
            }
        }
    }
}
