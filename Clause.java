/**
 * Clause class for a Problem File
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */

import java.util.*;

public class Clause
{
    private ArrayList<Double> attributes;
    private int quality;

    /**
     * Constructor for objects of class Clause
     */
    public Clause(int qual, ArrayList<Double> attr) {
        this.quality = qual;
        this.attributes = attr;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("Quality: " + this.quality);
        for (Double a : this.attributes) ret.append(a + " ");
        return ret.toString();
    }

    public void print() {
        System.out.println("Quality: " + this.quality);
        System.out.println("Attr: " + this.attributes);
    }

    public int getQuality() { return this.quality; }
    public int getNumAttributes() { return this.attributes.size(); }
    public ArrayList<Double> getAttributes() { return this.attributes; }
}
