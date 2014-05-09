package array;

import edu.princeton.cs.introcs.StdOut;
import enums.NodeMode;
import gui.ImageDisplayer;
import node.TransducerNode;

public class TransducerArray {
    private int Width;
    private int Height; // ��ȸ߶�
    private TransducerNode[][] nodes; // �����Node����
    private boolean[][] states;
    
    public TransducerArray(int W, int H, NodeMode mode) {
        // TODO Auto-generated constructor stub
        Width = W;
        Height = H;
        nodes = new TransducerNode[Width][Height];
        states = new boolean[Width][Height];
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                nodes[i][j] = new TransducerNode(i, j, mode);
            }
    }

    // ����ָ��λ�õ�Node
    public TransducerNode Node(int i, int j) {
        return nodes[i][j];
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
    
    // ��ȡ״̬��Ϣ
    public boolean[][] states() {
        return states;
    }
    
    // ��������TNode���и���
    public void update(PhotocellArray photocellArray) {
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                nodes[i][j].update(photocellArray);
                states[i][j] = nodes[i][j].spiked;
            }
    }
    
    // ��������TNode���и���
    // �޲���ʽ�����ڸ���״̬, �����޸�activity�͸���
    public void update() {
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                nodes[i][j].resetState();
                states[i][j] = nodes[i][j].spiked;
            }
    }
    
    @Override
    public String toString() 
    {
        String s = new String();
    
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
        PhotocellArray pa = new PhotocellArray(16, 16);
        TransducerArray ta = new TransducerArray(16, 16, NodeMode.ON);
        
        ImageDisplayer trainDisp = new ImageDisplayer(); // ��ʾѵ��ͼ��
        trainDisp.showImage(pa.image(), 16, 16, "Train", 8);
        ta.update(pa);
        StdOut.println(ta.toString());
    }

}
