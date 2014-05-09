package array;

import edu.princeton.cs.introcs.StdOut;
import enums.NodeMode;
import gui.ImageDisplayer;
import node.TransducerNode;

public class TransducerArray {
    private int Width;
    private int Height; // 宽度高度
    private TransducerNode[][] nodes; // 本层的Node对象
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

    // 返回指定位置的Node
    public TransducerNode Node(int i, int j) {
        return nodes[i][j];
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
    
    // 获取状态信息
    public boolean[][] states() {
        return states;
    }
    
    // 调用所有TNode进行更新
    public void update(PhotocellArray photocellArray) {
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++) {
                nodes[i][j].update(photocellArray);
                states[i][j] = nodes[i][j].spiked;
            }
    }
    
    // 调用所有TNode进行更新
    // 无参形式仅用于更新状态, 并不修改activity和概率
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
        
        ImageDisplayer trainDisp = new ImageDisplayer(); // 显示训练图像
        trainDisp.showImage(pa.image(), 16, 16, "Train", 8);
        ta.update(pa);
        StdOut.println(ta.toString());
    }

}
