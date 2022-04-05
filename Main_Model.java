/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package package_selection_guide_for_shippers;

/**
 *
 * @author Admin
 */
import java.util.ArrayList;
import java.util.Collections;
public class Main_Model
{
    /**
     * @param args the command line arguments
     */
    
    // Main model here
    public static void main(String[] args)
    {
        ArrayList<Package> packDF = new ArrayList<Package>(); //use for storing package data
        String pack_fileName = "pack_data.csv";
        
        while(true)
        {
            //Import package data
            packDF = utilities.read_csv(pack_fileName);
            
            //Show the data
            System.out.println("Here is the package information:");
            utilities.show_DataFrame(packDF);
            System.out.println();
            
            //Enter load capacity of user's vehicle
            double capacity = utilities.InputVehicleCapacity();
            
            //Filter package data based on user's capacity, and sort the data
            packDF = utilities.filter_by_capacity(packDF, capacity);
            Collections.sort(packDF, Package.weight_comparator_asc);
            System.out.println("\nAfter filtering and sorting:");
            utilities.show_DataFrame(packDF);
            System.out.println();
            
            //Applying method of 0/1 Knapsack problem for selecting packages for each turn
            int turn = 0;
            do
            {
                boolean vis = false; //Visualization flag for visualizing the memory table
                int[] selection = utilities.package_selection(packDF, capacity, vis);
                utilities.visualize_selection_code(selection);
                utilities.show_selection_guide(packDF, selection, turn+1);
                System.out.println("Total delivery weight: " + utilities.total_delivery_weight_per_turn(packDF, selection) + " kg.");
                System.out.println("Total value: " + utilities.total_values_per_turn(packDF, selection) + " VND.");
                packDF = utilities.discard_delivered_pack(packDF, selection);
                System.out.println();
                ++turn;
            } while(!packDF.isEmpty());
            
            //Ask for exit
            boolean exit = utilities.ask_for_exit();
            if(exit) break;
        }
    }
}