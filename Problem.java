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
            String line;
            String pattern = "(\"[a-z,A-Z]+\")";
            
            while ((line = reader.readLine()) != null) {
                //Ignore any alpha lines
                if (Pattern.matches(pattern, line)) {
                    StringTokenizer attrTokens = new StringTokenizer(line, ";", true);
                    this.numAttributes = attrTokens.countTokens();
                    maxValues = new double[this.numAttributes];
                    continue; 
                } 
               
                int quality = 0;
                ArrayList<Double> data = new ArrayList<Double>();

                StringTokenizer tokens = new StringTokenizer(line, ";", true);
                int count = 0;
                while (tokens.hasMoreTokens()) {
                    double temp = Double.parseDouble(tokens.nextToken());
                    if (temp > maxValues[count]) maxValues[count] = temp;
                    data.add(temp);
                    count++;
                }

                quality = data.get(data.size() - 1).intValue();
                data.remove(data.size() - 1);

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
                    double newValue  = temp.getAttributes().get(j) / maxValues[j];
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
    
    
    public int getNumProblems() { return this.numProblems; }
    public int getNumAttributes() { return this.numAttributes; }
    public ListIterator<Clause> getIterator() { return this.problem.listIterator(); }
}
