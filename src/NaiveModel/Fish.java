package NaiveModel;

import java.awt.Color;
import java.util.Random;

import fr.emse.fayol.maqit.simulator.components.ColorInteractionRobot;
import fr.emse.fayol.maqit.simulator.components.Message;
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

    @Override
    public void move(int arg0) {
        this.readMessages();
        Cell[][] grid = environment.getGrid();
        //BORDER BEHAVIOR
        //On tire un nombre random, si c'est 1 (sur 10) on met un changement random de direction
        //seulement si loin d'un mur
        //upper border = 1, right border = 2, bottom border = 3, left border = 4 
        Random Random = new Random();
        int randomMovement = Random.nextInt(10);
        int border = border(field, this.x, this.y, grid);
        if ((border==0) && (randomMovement==1)){
                this.randomMovement();
                return;
        }
        else if (border>0){
            if (border==4){
                this.setCurrentOrientation(orientation.down);
                this.moveForward();
                return;
            }
            else if (border==3){
                this.setCurrentOrientation(orientation.left);
                this.moveForward();
                return;
            }
            else if (border==2){
                this.setCurrentOrientation(orientation.up);
                this.moveForward();
                return;
            }
            else {
                this.setCurrentOrientation(orientation.right);
                this.moveForward();
                return;
            }
        }
        this.moveForward();
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
        this.moveForward();
    }  

    public int border(int field, int x, int y, Cell[][] grid) //If the fiish is near (field distance) of a border
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
}