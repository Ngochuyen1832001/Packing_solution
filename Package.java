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
import java.util.Comparator;
public class Package
{
    String PackageID;
    double weight;
    int value;

    public Package(String PackageID, double weight, int value)
    {
        this.PackageID = PackageID;
        this.weight = weight;
        this.value = value;
    }
    
    public static Comparator<Package> weight_comparator_asc = new Comparator<Package>()
    {
        public int compare(Package pack1, Package pack2)
        {
            double w1 = pack1.weight;
            double w2 = pack2.weight;
            
            if(w1 <= w2)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    };
}
