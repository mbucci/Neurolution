/**
 * Sets up a NN problem
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */

import java.util.*;
import java.util.regex.*;
import java.io.*;

@SuppressWarnings("unchecked")

public class Problem
{
    private int numProblems;         //number of problem clauses
    private int numAttributes;       //number of attributes per clause
    private List<Clause> problem;    //Problem data structure
    
    
    /**
     * Constructor, handles reading a file and problem construction 
     */
    public Problem(File f) {
        this.problem = new ArrayList<Clause>();
        this.numProblems = 0;
        this.numAttributes = 0;
        readFile(f);
    }
    
    
    //Reads in a problem file
    public void readFile(File f) {
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            double[] maxValues = null;
            String line = null;

            String pattern = "(\"[a-z,A-Z]\").*";

            // reader.readLine();  // skip first line
            
            while ((line = reader.readLine()) != null) {
                //Ignore any alpha lines
                if (line.matches(".*[a-zA-Z]+.*")) {
                    StringTokenizer attrTokens = new StringTokenizer(line, ";", false);
                    this.numAttributes = attrTokens.countTokens() - 1;

                    maxValues = new double[this.numAttributes];
                    continue; 
                } 

                int quality = 0;
                ArrayList<Double> data = new ArrayList<Double>();

                StringTokenizer tokens = new StringTokenizer(line, ";", false);
                int count = 0;
                int lastToken = tokens.countTokens() - 1;

                while (tokens.hasMoreTokens()) {
                    double temp = Double.parseDouble(tokens.nextToken());
                    
                    if (count == lastToken) {
                        quality = (int) temp;
                    } else {
                        if (temp > maxValues[count]) maxValues[count] = temp;
                        data.add(temp);
                    }
                    count++;
                }

                Clause newClause = new Clause(quality, data);
                this.problem.add(newClause);
                this.numProblems++;
                        
            }
            reader.close();

            //Normalize all attributes to be between 0 and 1 using the max value of a given attribute
            for (int i = 0; i < this.problem.size(); i++) {
                Clause temp = this.problem.get(i);
                ArrayList<Double> normedVal = new ArrayList<Double>();


                for (int j = 0; j < this.numAttributes; j++) {
                    double newValue;
                    if (temp.getAttributes().get(j) == 0.0 && maxValues[j] == 0.0) {
                        newValue = 0.0;
                    } else if (maxValues[j] == 0.0) {
                        // Something's wrong
                        System.out.println("ERROR");
                        break;
                    } else { newValue  = temp.getAttributes().get(j) / maxValues[j]; }
                    normedVal.add(newValue);
                }
                Clause upClause = new Clause(temp.getQuality(), normedVal);
                this.problem.set(i, upClause);
            }
            
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", f);
            e.printStackTrace();
        }
    }

    public void print() {
        for (Clause c : problem) {
            c.print();
            System.out.println("\n");
        }
    }    
    
    public int getNumProblems() { return this.numProblems; }
    public int getNumAttributes() { return this.numAttributes; }
    public ListIterator<Clause> getIterator() { return this.problem.listIterator(); }
}
