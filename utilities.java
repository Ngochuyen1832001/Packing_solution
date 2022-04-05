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
import java.util.Scanner;
import java.io.*;
public class utilities
{
    static ArrayList<Package> read_csv(String filename)
    {
        ArrayList<Package> packDF = new ArrayList<Package>();
        
        try
        {
            File csv = new File(filename);
            FileReader csv_whole = new FileReader(csv);
            BufferedReader csv_row = new BufferedReader(csv_whole);
            String raw_row; int line = 0;
            //Read each row
            while((raw_row = csv_row.readLine()) != null)
            {
                if(line == 0)
                {
                    line += 1;
                    continue;
                }
                //Extract row data, and create an object (data point)
                String[] row = raw_row.split(",");
                String packageID = row[0];
                double weight = Double.parseDouble(row[1]);
                int value = Integer.parseInt(row[2]);
                Package pack = new Package(packageID, weight, value);
                //Append to the list
                packDF.add(pack);
            }
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e);
        }
        
        return packDF;
    }
    
    static void show_DataFrame(ArrayList<Package> packDF)
    {
        System.out.format("| %-12s| %-8s| %-10s|\n", "Package ID", "Weight", "Value");
        System.out.println("+-------------+---------+-----------+");
        for(Package pack : packDF)
        {
            System.out.format("| %-12s| %-8s| %-10s|\n", pack.PackageID, pack.weight, pack.value);
        }
    }
    
    static double InputVehicleCapacity()
    {
        double vehicle_capacity;
        Scanner input = new Scanner(System.in);
        while(true)
        {
            try
            {
                System.out.print("Enter your vehicle's load capacity (in kg): ");
                vehicle_capacity = Double.parseDouble(input.nextLine());
                if(vehicle_capacity <= 0)
                {
                    System.out.println("Capacity cannot be 0 or lower.");
                    continue;
                }
                return vehicle_capacity;
            }
            catch(Exception e)
            {
                System.out.println("Error: " + e);
            }
        }
    }
    
    static ArrayList<Package> filter_by_capacity(ArrayList<Package> packDF, double capacity)
    {
        //Filter and discard packages whose weight are higher than load capacity
        ArrayList<Package> filtered_packDF = packDF;
        int n = filtered_packDF.size();
        int i = 0;
        while(i < n)
        {
            double weight = filtered_packDF.get(i).weight;
            if(weight <= capacity)
            {
                ++i;
            }
            else
            {
                Package discard_pack = filtered_packDF.remove(i);
                //Recalculate the size after removal
                --n;
            }
        }
        return filtered_packDF;
    }
    
    // Method applied from 0/1 Knapsack problem
    static int[] package_selection(ArrayList<Package> packDF, double capacity, boolean visualize)
    {
        //Use tabulation approach in dynamic programming
        
        //Abort if there is no package in the list (return empty array)
        if(packDF.isEmpty())
        {
            System.out.println("Sorry, there is no package in the list, or your vehicle cannot load each of them.");
            return new int[0];
        }
        
        //Initialization steps
        int size = packDF.size();
        double[] weight = extract_weight(packDF);
        int[] value = extract_value(packDF);
        /* As capacity is floating point, step size must be set for initializing capacity steps.
        The step size can be varied for variety of capacity cases. */
        double cap_step_size;
        if(capacity > 30) //For large capacity (> 30 kg)
        {
            cap_step_size = 1.0;
        }
        else if(capacity > 3) //For medium capacity (3 - 30 kg)
        {
            cap_step_size = 0.1;
        }
        else //For small capacity (< 3 kg)
        {
            cap_step_size = 0.01;
        }
        int cap_steps = floor_and_round(capacity / cap_step_size);
        int[][] memory = new int[size + 1][cap_steps + 1];
        int[] selection_code = new int[size];
        
        // FORWARD PHASE
        //Use the memory table for maximizing the value
        for(int i = 1; i <= size; ++i)
        {
            for(int j = 1; j <= cap_steps; ++j)
            {
                //If this capacity step is not enough for storing new package,
                //keep previous value, and skip to next capacity step
                if(weight[i-1] > j*cap_step_size)
                {
                    memory[i][j] = memory[i-1][j];
                }
                //Update value if enough for new package
                else
                {
                    memory[i][j] = max(value[i-1]+memory[i-1][(int)((j*cap_step_size-weight[i-1])/cap_step_size)], memory[i-1][j]);
                }
            }
        }
        
        if(visualize)
        {
            //Visualize the table if needed
            String[] ID = extract_ID(packDF);
            visualize_table(memory, cap_step_size, ID);
            System.out.println();
        }
        
        // BACKWARD PHASE
        //Find the optimal solution of selection array
        int x = size, y = cap_steps;
        //double remain_capacity = capacity;
        while(x != 0 && y != 0)
        {
            if(memory[x][y] != memory[x-1][y])
            {
                selection_code[x-1] = 1;
                y = (int)((y*cap_step_size-weight[x-1]) / cap_step_size);
            }
            x -= 1;
        }
        
        return selection_code;
    }
    
    static String[] extract_ID(ArrayList<Package> packDF)
    {
        int n = packDF.size();
        String[] ID = new String[n];
        for(int i = 0; i < n; ++i)
        {
            ID[i] = packDF.get(i).PackageID;
        }
        return ID;
    }
    
    static double[] extract_weight(ArrayList<Package> packDF)
    {
        int n = packDF.size();
        double[] weight = new double[n];
        for(int i = 0; i < n; ++i)
        {
            weight[i] = packDF.get(i).weight;
        }
        return weight;
    }
    
    static int[] extract_value(ArrayList<Package> packDF)
    {
        int n = packDF.size();
        int[] value = new int[n];
        for(int i = 0; i < n; ++i)
        {
            value[i] = packDF.get(i).value;
        }
        return value;
    }
    
    static private int max(int a, int b)
    {
        if(a > b)
            return a;
        else
            return b;
    }
    
    static void visualize_selection_code(int[] selection_code)
    {
        int n = selection_code.length;
        System.out.print("Selection (encoded): [");
        for(int i = 0; i < n-1; ++i)
        {
            System.out.print(selection_code[i] + ", ");
        }
        System.out.println(selection_code[n-1] + "]\n");
    }
    
    static private void visualize_table(int[][] table, double cap_step_size, String[] ID)
    {
        //Define & identify shape of the table
        int[] shape = new int[2];
        shape[0] = table.length;    //# of rows
        shape[1] = table[0].length; //# of columns
        
        //Visualize
        System.out.printf("Memory table, capacity step size = %.2f kg:\n", cap_step_size);
        //Show column names (capacity steps)
        System.out.print("            |");
        for(int j = 0; j < shape[1]; ++j)
        {
            System.out.format(" %-10s|", String.format("%.2f", j*cap_step_size));
        }
        System.out.println();
        System.out.print("------------+");
        for(int j = 0; j < shape[1]; ++j)
        {
            System.out.print("-----------+");
        }
        System.out.println();
        for(int i = 0; i < shape[0]; ++i)
        {
            //Show row name every row (package ID)
            if(i > 0)
            {
                System.out.format(" %-11s|", ID[i-1]);
            }
            else
            {
                System.out.print("            |");
            }
            //Show values
            for(int j = 0; j < shape[1]; ++j)
            {
                System.out.format(" %-10s|", table[i][j]);
            }
            System.out.println();
        }
    }
    
    static void show_selection_guide(ArrayList<Package> packDF, int[] selection_code, int turn)
    {
        //Abort if the pack data is empty
        if(packDF.isEmpty())
        {
            return;
        }
        
        System.out.println("Turn " + turn + ":");
        System.out.println("Here are packages (with their IDs) you should pick this turn:");
        for(int i = 0; i < selection_code.length; ++i)
        {
            if(selection_code[i] == 1)
            {
                System.out.println(" - Package " + packDF.get(i).PackageID);
            }
        }
    }
    
    static ArrayList<Package> discard_delivered_pack(ArrayList<Package> packDF, int[] selection_code)
    {
        //Abort if the pack data is empty
        if(packDF.isEmpty())
        {
            return new ArrayList<Package>();
        }
        
        ArrayList<Package> new_packDF = packDF;
        int n = selection_code.length;
        for(int i = n-1; i >= 0; --i)
        {
            if(selection_code[i] == 1)
            {
                Package delivered_pack = new_packDF.remove(i);
            }
        }
        //Notify how many packages left
        System.out.printf("%d packages left for the next turn.\n", new_packDF.size());
        return new_packDF;
    }
    
    static double total_delivery_weight_per_turn(ArrayList<Package> packDF, int[] selection_code)
    {
        double total_weight = 0;
        int n = selection_code.length;
        for(int i = 0; i < n; ++i)
        {
            if(selection_code[i] == 1)
            {
                total_weight += packDF.get(i).weight;
            }
        }
        return total_weight;
    }
    
    static int total_values_per_turn(ArrayList<Package> packDF, int[] selection_code)
    {
        int total_value = 0;
        int n = selection_code.length;
        for(int i = 0; i < n; ++i)
        {
            if(selection_code[i] == 1)
            {
                total_value += packDF.get(i).value;
            }
        }
        return total_value;
    }
    
    static double total_weight_packages(ArrayList<Package> packDF)
    {
        double total_weight = 0;
        for(Package pack : packDF)
        {
            total_weight += pack.weight;
        }
        return total_weight;
    }
    
    static int ceil(double x)
    {
        int x_floor = (int) x;
        if(x - x_floor > 0)
        {
            return (x_floor + 1);
        }
        else
        {
            return x_floor;
        }
    }
    
    private static int floor_and_round(double x)
    {
        int x_floor = (int) x;
        double floating_point = x - x_floor;
        if(floating_point > 0.9999)
        {
            return x_floor + 1;
        }
        else
        {
            return x_floor;
        }
    }
    
    static boolean ask_for_exit()
    {
        Scanner input = new Scanner(System.in);
        char exit;
        System.out.print("Exit? (Y/N) ");
        exit = input.nextLine().charAt(0);
        return (exit == 'Y' || exit == 'y');
    }
}
