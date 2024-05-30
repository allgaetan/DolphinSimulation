package NaiveModel;

import java.awt.Color;
import java.util.Random;

import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.components.Orientation;
import fr.emse.fayol.maqit.simulator.components.SituatedComponent;
import fr.emse.fayol.maqit.simulator.environment.Cell;
import fr.emse.fayol.maqit.simulator.environment.GridEnvironment;

/**
 * Model for the Fish class (same model for Naive and Complex)
 * @author Gaétan Allaire 
 * @author Karim Bekhti
 * @author Kim-Tchoy Du
 */
public class Fish extends ColorInteractionRobot {
    int[][] fish_coo; //Added by KB
    //private static List<int[]> fish_y = new ArrayList<>(); //Added by KB
    //Added by KB for barycenter
    int part_sum = 0; //la somme partielle
    double bar_x; //le x du barycentre
    double bar_y; //le y du barycentre
    int nb_fish; //le nombre de poisson dans la simulation
    int x_test;
    int y_test;
    public GridEnvironment environment;
    char name;
    int Voisins;
    private Cell[][] neighbors; 

    protected Fish(String name, int field, int debug, int[] pos, Color rgb, int rows, int columns, int nbobstacle, GridEnvironment environment) {
        //added a new argument here nb obstacle
        super(name, field, debug, pos, rgb, rows, columns);
        this.environment = environment;
        fish_coo = new int[nbobstacle][2];
    }

    @Override
    public void handleMessage(Message arg0) { //Fishes don't talk
    }

    public void setNeighbors(Cell[][] neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void move(int arg0) {
        this.updatePerception(this.neighbors);  
        Cell[][] grid = environment.getGrid();

        //For the next turn the fish has to choose an orientation
        //It cycles through all the detection, the priority being on the last one

        /*Neighbors */ //Detects the neighborhood.
        int field = 8;
        for (int i = 0; i < 2*field; i++) {
            for (int j = 0; j < 2*field; j++) { //Cycle through a square of 2*field
                //the center of this square is the fish
                x_test = getX() - (i - field);
                y_test = getY() - (j - field);

                //being sure we do not go outside the grid
                if (x_test <=0){
                    x_test = 0; 
                }
                if (y_test <= 0){
                    y_test = 0;
                }
                if (x_test >= grid.length){
                    x_test = grid.length-1; 
                }
                if (y_test >= grid[0].length){
                    y_test = grid[0].length-1;
                }

                //Now we get the cell content 
                Cell cell = grid[x_test][y_test];
                SituatedComponent cellContent = cell.getContent();
                ColorInteractionRobot cellRobot = (ColorInteractionRobot) cellContent;

                //If there s something, look what it is, and react to it
                if (cellContent != null){
                    name = cellRobot.getName().charAt(0);
                    //If that is a fish, we get the orientation
                    if (name=='f'){
                        this.setCurrentOrientation(cellRobot.getCurrentOrientation());
                    }
                    if (name=='d'){
                        this.setCurrentOrientation(cellRobot.getCurrentOrientation());
                        break; //highest priority, we do not look for anything else
                    }
                }
            }
        }
        
        /*BORDER AND RANDOM*/ //Each fish has a small chance to go randomly, even in shoals
        this.updatePerception(this.neighbors);
        int border = border(3, this.x, this.y, grid);
        Random Random = new Random();
        int randomMovement = Random.nextInt(5);
        moveBorder(border); //Calls upon the function allowing to avoid the border
        if ((border==0) && (randomMovement<=1)){
            this.randomMovement(); //random movement only when no borders not to get stuck
        }

        /* Effective movements */
        this.updatePerception(this.neighbors);  
        if ((border!=0)){
            this.moveForward();
        }
        else {
            this.moveForward();
            return;
        }
    }

    public void randomMovement(){ //everything in the name
        this.randomOrientation();
    }  

    public int border(int field, int x, int y, Cell[][] grid) //If the fish is near (field distance) of a border
    {
        if ((x<field)){
            return 4;
        }
        else if ((y<field)){
            return 1;
        }
        else if ((x>grid[0].length-field)){
            return 2;
        }
        else if ((y>grid[1].length-field)){
            return 3;
        }
        else{
            return 0;
        }
    }

    public void MoveWithCondition(){
        if (this.freeForward()){
            this.moveForward();
            return;
        }
        else {
            return;
        }
    }

    public void moveBorder(int border){
        if (border==4){
            this.setCurrentOrientation(Orientation.down);
            return;
        }
        else if (border==3){
            this.setCurrentOrientation(Orientation.left);
            return;
        }
        else if (border==2){
            this.setCurrentOrientation(Orientation.up);
            return;
        }
        else if (border==0){
            return;
        }
        else if (border==1){
            this.setCurrentOrientation(Orientation.right);
            return;
        }
    }
}