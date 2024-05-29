package ComplexModel;

import java.awt.Color;
import java.util.Random;

import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
import fr.emse.fayol.maqit.simulator.components.Orientation;
import fr.emse.fayol.maqit.simulator.components.Robot;
import fr.emse.fayol.maqit.simulator.components.SituatedComponent;
import fr.emse.fayol.maqit.simulator.environment.Cell;
import fr.emse.fayol.maqit.simulator.environment.GridEnvironment;

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
        fish_coo = new int[nbobstacle][2]; //KB
        //System.out.println(nbobstacle); //KB
    }

    @Override
    public void handleMessage(Message arg0) {
    }

    public void setNeighbors(Cell[][] neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void move(int arg0) {
        this.updatePerception(this.neighbors);  
        this.readMessages();
        Cell[][] grid = environment.getGrid();

        /*Neighbors */
        int field =8;
        for (int i = 0; i < 2*field; i++) {
            for (int j = 0; j < 2*field; j++) {
                x_test = getX() - (i - field);
                y_test = getY() - (j - field);
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
                Cell cell = grid[x_test][y_test];
                SituatedComponent cellContent = cell.getContent();
                ColorInteractionRobot cellRobot = (ColorInteractionRobot) cellContent;
                if (cellContent != null){
                    name = cellRobot.getName().charAt(0);
                    //System.out.println(cellRobot.getName().charAt(0));
                    //Si c'est un fish : on récupère son orientation
                    if (name == 'f'){
                        this.setCurrentOrientation(cellRobot.getCurrentOrientation());
                    }
                }
            }
        }
        
        /*BORDER AND RANDOM*/
        this.updatePerception(this.neighbors);    
        int border = border(3, this.x, this.y, grid);
        Random Random = new Random();
        int randomMovement = Random.nextInt(5);
        moveBorder(border);
        if ((border==0) && (randomMovement<=1)){
            this.randomMovement();
        
            //System.out.println("Random");
        }

        /* Next cell detection */
        Cell nextCell;
        Orientation nextOr;
        if (this.getCurrentOrientation()==Orientation.up){
            nextCell = grid[this.x][this.y+1];   
            nextOr = Orientation.down;
        }
        else if (this.getCurrentOrientation()==Orientation.down){
            nextCell = grid[this.x][this.y-1];  
            nextOr = Orientation.up; 
        }
        else if (this.getCurrentOrientation()==Orientation.left){
            nextCell = grid[this.x-1][this.y];   
            nextOr = Orientation.right;
        }
        else {
            nextCell = grid[this.x+1][this.y];   
            nextOr = Orientation.left;
        }

        SituatedComponent onCellComponent = nextCell.getContent();
            if (onCellComponent != null) {
                if (onCellComponent instanceof Robot) {
                    Robot onCellRobot = (Robot) onCellComponent;
                    if (onCellRobot.getName().startsWith("fish")) {       
                        this.setCurrentOrientation(nextOr);
                    }
                }
            }
        /* Effective movements */
        
        else if ((border!=0)){
            this.moveForward();
        }
        else {
            this.moveForward();
            return;
        }
        
        //System.out.println("Border");
        
        /*
        this.updatePerception(this.neighbors);  
        this.readMessages();
        Cell[][] grid = environment.getGrid();
        Cell nextCell = null;
        this.updatePerception(this.neighbors);    

        //BORDER BEHAVIOR
        //On tire un nombre random, si c'est 1 (sur 10) on met un changement random de direction
        //seulement si loin d'un mur
        //upper border = 1, right border = 2, bottom border = 3, left border = 4 
        Random Random = new Random();
        int randomMovement = Random.nextInt(10);
        int border = border(4, this.x, this.y, grid);
        if ((border==0) && (randomMovement==1)){
                this.randomMovement();
                //System.out.println("Random");
                return;
        }
        else if (border>0){
            //System.out.println("Border");
            if (border==4){
                this.setCurrentOrientation(Orientation.down);
                this.MoveWithCondition();
                return;
            }
            else if (border==3){
                this.setCurrentOrientation(Orientation.left);
                this.MoveWithCondition();
                return;
            }
            else if (border==2){
                this.setCurrentOrientation(Orientation.up);
                this.MoveWithCondition();
                return;
            }
            else {
                this.setCurrentOrientation(Orientation.right);
                this.MoveWithCondition();
                return;
            }
        }
        this.MoveWithCondition();
        return;
        /*
        //GROUP BEHAVIOR
        for (int i = 0; i < 2*field; i++) {
            for (int j = 0; j < 2*field; j++) {
                x_test = getX() - (i - field);
                y_test = getY() - (j - field);
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
                Cell cell = grid[x_test][y_test];
                SituatedComponent cellContent = cell.getContent();
                ColorInteractionRobot cellRobot = (ColorInteractionRobot) cellContent;
                if (cellContent != null){
                    name = cellRobot.getName().charAt(0);
                    //System.out.println(cellRobot.getName().charAt(0));
                    //Si c'est un fish : on récupère son orientation
                    if (name == 'f'){
                        this.setCurrentOrientation(cellRobot.getCurrentOrientation());
                    }
                }
            }
        }
            */
    }

    public void randomMovement(){
        this.randomOrientation();
        //this.MoveWithCondition();
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
