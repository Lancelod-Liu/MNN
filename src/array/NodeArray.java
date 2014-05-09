package array;

import java.util.ArrayList;

import node.Node;
import edu.princeton.cs.algs4.Bag;
import gadget.Edge;
import gadget.Integrator;

public class NodeArray {
    private int Width;
    private int Height; // 宽度高度
    private Node[][] nodes; // 本层的Node对象
    
    public NodeArray(int W, int H) {
        Width = W;
        Height = H;
        nodes = new Node[Width][Height];
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                nodes[i][j] = new Node(i, j, W, H);
            }
    }
    
    public double[][] int_states() {
        double[][] states = new double[Width][Height];
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                states[i][j] = nodes[i][j].state()/Integrator.THRESHOLD;
                if (states[i][j] >= 1.0) states[i][j] = 1.0;
                if (states[i][j] <= -1.0) states[i][j] = -1.0;
            }
        return states;
    }
    
    public double[][] spike_states() {
        double[][] states = new double[Width][Height];
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                if (nodes[i][j].spiked)
                    states[i][j] = 1.0; // 黑色
                else
                    states[i][j] = -1.0; // 白色    
            }
        return states;
    }
    
    public Bag<Edge> edges() { 
        Bag<Edge> edges = new Bag<Edge>();       
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                for (Edge e : nodes[i][j].edgeToON)
                    edges.add(e);
                for (Edge e : nodes[i][j].edgeToOFF)
                    edges.add(e);
            }
        return edges;
    }
    
    public Node Node(int i, int j) { return nodes[i][j]; }
    
    /**
     * 按照左上→右下的顺序获取感知域图像
     * */
    public Iterable<double[][]> receptiveFields() {
        ArrayList<double[][]> receptiveFields = new ArrayList<double[][]>();
        
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                receptiveFields.add(nodes[i][j].receptiveField());
            }
        
        return receptiveFields;
    }
    
    /**
     * @param angles 单位是弧度
     * @param images 测试图像集合
     * */
    public double Selectivity(Iterable<double[][]> images, double[] angles) {
        double sum = 0.0;
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                sum += nodes[i][j].Selectivity(images, angles);
            }
        sum /= (Width * Height); //对所有的Si求平均
        return sum;
    }
    
    /** 调用所有的Node收集所有的edge输入
     * @param stateON 感知ON层的spike信息
     * @param stateOFF 感知OFF层的spike信息
     * */
    public void update(boolean[][] stateON, boolean[][] stateOFF) {
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                nodes[i][j].update(stateON, stateOFF, this);
            }
    }
    
    @Override
    public String toString() 
    {
        String s = new String();
        
        s += "NodeArray :\n";
        s += "\t";
        for (int i = 0; i < Width; i++) {
            s += Integer.toString(i) + "\t";
        }
        s += "\n";
        for (int i = 0; i < Width; i++) {
            s += Integer.toString(i) + "\t";
            for (int j = 0; j < Height; j++) {
                s += nodes[i][j].toString();
                s += "\t";
            }
            s += "\n";
        }
        
        return s;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
