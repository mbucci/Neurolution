/**
 * Perceptron Edge
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */
public class Edge
{
    private int inputNode, outputNode;
    private double weight;

    /**
     * Constructor for objects of class Edge. Keeps track of input node ID, 
     * output node ID and the weight of the edge between them.
     */
    public Edge(int in, int out, double weight) {
        this.inputNode = in;
        this.outputNode = out;
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return String.format(this.inputNode+" -- %.2f -- "+this.outputNode, this.weight);
    }
    
    public int getInput() { return this.inputNode; }
    public int getOutput() { return this.outputNode; }
    public double getWeight() { return this.weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
