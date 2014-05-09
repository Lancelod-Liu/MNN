package node;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.introcs.StdOut;
import gadget.Edge;
import gadget.Integrator;

import java.util.ArrayList;

import array.NodeArray;

public class Node {     
    public static final int RecpRadix = 3; // 感知域范围
    private final int ExcRadix = 1; 
    private final int InhRadix = 2; // 刺激和抑制范围[正方形]
    private double FP = 1.0; // 系数
    private double FE = 1.0; 
    private double FI = -0.5;
    //private double FB = 0.0;
    public double angle = 0.0; // 训练出的角度
    private int x, y; // 坐标信息
    private Integrator integrator = new Integrator();
    public boolean spiked = false; // 是否spike
    public Bag<Edge> edgeToON;
    public Bag<Edge> edgeToOFF; // 连接到transducer层的Edge


    public Node(int x, int y, int W, int H) {
        // TODO Auto-generated constructor stub
        this.x = x;
        this.y = y;
        edgeToON = new Bag<Edge>();
        edgeToOFF = new Bag<Edge>();
        // add edge
        for (int i = x - RecpRadix; i <= x + RecpRadix; i++)
            for (int j = y - RecpRadix; j <= y + RecpRadix; j++) {
                if (i >= 0 && i < W && j >= 0 && j < H) {
                    edgeToON.add(new Edge(i, j));
                    edgeToOFF.add(new Edge(i, j));
                }
            }
    }
    
    /** 获取感知域对应像素信息
     * 下标对应于以本节点为中心 左上角为(0,0)的位置
     * 可能包含无效EDGE 但本API不对其进行处理
     * 调用此API的客户端需要注意对java.lang.ArrayIndexOutOfBoundsException()的处理
     * */
    public double[][] receptiveField() {
        double[][] on = receptiveFieldON();
        double[][] off = receptiveFieldOFF();
        double[][] r = new double[2 * RecpRadix + 1][2 * RecpRadix + 1];
        
        for (int i = 0; i < 2 * RecpRadix + 1; i++) {
            for (int j = 0; j < 2 * RecpRadix + 1; j++) {
                r[i][j] = on[i][j] - off[i][j];
            }
        }
        
        return r;
    }
    
    private double[][] receptiveFieldON() {
        double[][] r = new double[2 * RecpRadix + 1][2 * RecpRadix + 1];
        
        for (Edge e : edgeToON) {
            int i = e.x - x + RecpRadix;
            int j = e.y - y + RecpRadix;
            r[i][j] = e.weight();
        }
        return r;
    }
    
    private double[][] receptiveFieldOFF() {
        double[][] r = new double[2 * RecpRadix + 1][2 * RecpRadix + 1];
        
        for (Edge e : edgeToOFF) {
            int i = e.x - x + RecpRadix;
            int j = e.y - y + RecpRadix;
            r[i][j] = e.weight();
        }
        return r;
    }
    
    /* 抓取图像感知域对应的像素点, 乘以感知域ON和OFF的权值后相加 
     * 计算出R->a,b,fi->Si 返回Si
     * angles 单位是弧度
     * */
    public double Selectivity(Iterable<double[][]> images, double[] angles) {
        double pixel = 0.0;
        double R = 0.0;
        double a = 0.0;
        double b = 0.0;
        int k = ((ArrayList<double[][]>)images).size();
        int count = 0;
        double[][] rfon = receptiveFieldON(); 
        double[][] rfoff = receptiveFieldOFF(); // 获取感知域权值
        
        for (double[][] image : images) {
            // 读取k张图片 计算 k 个R
            for (int i = x - RecpRadix; i <= x + RecpRadix; i++)
                for (int j = y - RecpRadix; j <= y + RecpRadix; j++) {
                    try {
                        // rf[][]是相对坐标 image[][]是绝对坐标 需要转换
                        // 把绝对坐标转换为相对坐标
                        int m = i - x + RecpRadix;
                        int n = j - y + RecpRadix;
                        pixel = step(image[i][j]) * rfon[m][n] + step(-image[i][j]) * rfoff[m][n];
                        R += pixel;
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException e) {
                        // 如果数组越界                
                        continue;
                    }
                }
            a += R * Math.cos(angles[count]);
            b += R * Math.sin(angles[count]);
            count++;
        }
        // 计算selectivity 和 angle
        if (a >= 0) angle = Math.atan(b/a);
        if (a < 0) angle = Math.PI + Math.atan(b/a);
        double S = Math.sqrt(a * a + b * b) / k;
        return S;
    }
    
    private double step(double d) {
        if (d >= 0.0) return d;
        else return 0.0;
    }
    
    // 返回积分器状态
    public double state() { return integrator.state(); }
    
    public double angle() { return angle;}
    
    /**
     * 进行一轮cycle操作
     * 先确定工作状态, 后更改权值
     * [这样可以和T层的Spike同步]→
     * 本cycle产生的spike要到下cycle才生效
     * 这符合神经元的spike定义[下次cycle发送spike]
     * @param stateON 本次cycle的T层状态
     * @param stateOFF 本次cycle的T层状态
     * */
    public void update(boolean[][] stateON, boolean[][] stateOFF, NodeArray nodeArray) {
        // 修改权值, 此API使用的是上一循环的spiked
        modifyEdge(stateON, stateOFF);
        // 修改神经元状态
        if (spiked) {
            // spiked 为 true, 说明上个cycle积分器溢出, 本次cycle为spike模式
            // 所以本轮更新不收集输入, 但仍需要进行积分器衰减
            integrator.decay();
            spiked = false;
        }
        else {
            // 上一个cycle没有spike, 本cycle为process模式
            // 衰减积分器, 收集输入
            integrator.decay();
            collectPriorInput(stateON, stateOFF);
            collectExctieInput(nodeArray);
            collectInhibitInput(nodeArray);
            collectOutput();
            if (integrator.overload) {
                // 积分器超载, 说明本cycle的输入足够使得积分器进入spike模式
                // 置位 spiked, 为下一cycle更新权值提供参考
                // 而integrator.overload会在下下cycle调用integrator.add方法, 收集输入时被重置
                spiked = true;
            }
        }
        
    }
 
    private void collectPriorInput(boolean[][] stateON, boolean[][] stateOFF) {
        // on
        for (Edge e : edgeToON) {
            try {
                if (stateON[e.x][e.y]) {
                    if (integrator.add(e.weight(), FP))
                        return;
                }
            }
            catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                // 如果数组越界                
                continue;
            }
        }
        // off
        for (Edge e : edgeToOFF) {
            try {
                if (stateOFF[e.x][e.y]) {
                    if (integrator.add(e.weight(), FP))
                        return;
                }
            }
            catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                // 如果数组越界                
                continue;
            }
        }
    }
    
    private void collectExctieInput(NodeArray nodeArray) {
        for (int i = x - ExcRadix; i <= x + ExcRadix; i++)
            for (int j = y - ExcRadix; j <= y + ExcRadix; j++) { 
                try {
                    if (nodeArray.Node(i, j).spiked) {
                        if (integrator.add(Edge.WMAX, FE))
                            return;
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                    // 如果数组越界                
                    continue;
                }    
            }
    }
    
    private void collectInhibitInput(NodeArray nodeArray) {
        for (int i = x - InhRadix; i <= x + InhRadix; i++)
            for (int j = y - InhRadix; j <= y + InhRadix; j++) { 
                try {
                    if (nodeArray.Node(i, j).spiked) {
                        if (integrator.add(Edge.WMAX, FI))
                            return;
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                    // 如果数组越界                
                    continue;
                }    
            }
    }
    
    private void collectOutput() {
        // DO NOTHING BECAUSE FB = 0
    }
    
    private void modifyEdge(boolean[][] stateON, boolean[][] stateOFF) {
        // on
        for (Edge e : edgeToON) {
            try {
                e.update(stateON[e.x][e.y], spiked);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                // 如果数组越界                
                continue;
            }
        }
        // off
        for (Edge e : edgeToOFF) {
            try {
                e.update(stateOFF[e.x][e.y], spiked);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                // 如果数组越界                
                continue;
            }
        }
    }
    
    
    @Override
    public String toString() {
        String s = String.format("%s\n", integrator.overload?"Spiked":"Not Spiked");
        
        for (int i = 0; i < 2 * RecpRadix + 1; i++) {
            for (int j = 0; j < 2 * RecpRadix + 1; j++) {
                s += String.format("%3.2f\t", receptiveField()[i][j]);
            }
            s += "\n";
        }
        
        return s;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Node n = new Node(0, 0, 16, 16);
        StdOut.print(n.toString());
        
    }

   

}
