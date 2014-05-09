package array;

import java.util.ArrayList;

import node.Node;
import edu.princeton.cs.algs4.Bag;
import gadget.Edge;
import gadget.Integrator;

public class NodeArray {
    private int Width;
    private int Height; // ��ȸ߶�
    private Node[][] nodes; // �����Node����
    
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
                    states[i][j] = 1.0; // ��ɫ
                else
                    states[i][j] = -1.0; // ��ɫ    
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
     * �������ϡ����µ�˳���ȡ��֪��ͼ��
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
     * @param angles ��λ�ǻ���
     * @param images ����ͼ�񼯺�
     * */
    public double Selectivity(Iterable<double[][]> images, double[] angles) {
        double sum = 0.0;
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                sum += nodes[i][j].Selectivity(images, angles);
            }
        sum /= (Width * Height); //�����е�Si��ƽ��
        return sum;
    }
    
    /** �������е�Node�ռ����е�edge����
     * @param stateON ��֪ON���spike��Ϣ
     * @param stateOFF ��֪OFF���spike��Ϣ
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
