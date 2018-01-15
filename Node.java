public class Node
{    
    public Node left, right;
    public String featureName;
    public int  a ,b, counter;
    public double entropy;
    public boolean data;
    public Boolean classOutput = null;
    
    public Node(int a, int b, double entropy, boolean data, String featureName){
        left = null;
        right = null;
        this.data = data;
        this.a = a;
        this.b = b;
        this.entropy = entropy;
        this. featureName = featureName;
    }
}