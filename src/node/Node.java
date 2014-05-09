package node;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.introcs.StdOut;
import gadget.Edge;
import gadget.Integrator;

import java.util.ArrayList;

import array.NodeArray;

public class Node {     
    public static final int RecpRadix = 3; // ��֪��Χ
    private final int ExcRadix = 1; 
    private final int InhRadix = 2; // �̼������Ʒ�Χ[������]
    private double FP = 1.0; // ϵ��
    private double FE = 1.0; 
    private double FI = -0.5;
    //private double FB = 0.0;
    public double angle = 0.0; // ѵ�����ĽǶ�
    private int x, y; // ������Ϣ
    private Integrator integrator = new Integrator();
    public boolean spiked = false; // �Ƿ�spike
    public Bag<Edge> edgeToON;
    public Bag<Edge> edgeToOFF; // ���ӵ�transducer���Edge


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
    
    /** ��ȡ��֪���Ӧ������Ϣ
     * �±��Ӧ���Ա��ڵ�Ϊ���� ���Ͻ�Ϊ(0,0)��λ��
     * ���ܰ�����ЧEDGE ����API��������д���
     * ���ô�API�Ŀͻ�����Ҫע���java.lang.ArrayIndexOutOfBoundsException()�Ĵ���
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
    
    /* ץȡͼ���֪���Ӧ�����ص�, ���Ը�֪��ON��OFF��Ȩֵ����� 
     * �����R->a,b,fi->Si ����Si
     * angles ��λ�ǻ���
     * */
    public double Selectivity(Iterable<double[][]> images, double[] angles) {
        double pixel = 0.0;
        double R = 0.0;
        double a = 0.0;
        double b = 0.0;
        int k = ((ArrayList<double[][]>)images).size();
        int count = 0;
        double[][] rfon = receptiveFieldON(); 
        double[][] rfoff = receptiveFieldOFF(); // ��ȡ��֪��Ȩֵ
        
        for (double[][] image : images) {
            // ��ȡk��ͼƬ ���� k ��R
            for (int i = x - RecpRadix; i <= x + RecpRadix; i++)
                for (int j = y - RecpRadix; j <= y + RecpRadix; j++) {
                    try {
                        // rf[][]��������� image[][]�Ǿ������� ��Ҫת��
                        // �Ѿ�������ת��Ϊ�������
                        int m = i - x + RecpRadix;
                        int n = j - y + RecpRadix;
                        pixel = step(image[i][j]) * rfon[m][n] + step(-image[i][j]) * rfoff[m][n];
                        R += pixel;
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException e) {
                        // �������Խ��                
                        continue;
                    }
                }
            a += R * Math.cos(angles[count]);
            b += R * Math.sin(angles[count]);
            count++;
        }
        // ����selectivity �� angle
        if (a >= 0) angle = Math.atan(b/a);
        if (a < 0) angle = Math.PI + Math.atan(b/a);
        double S = Math.sqrt(a * a + b * b) / k;
        return S;
    }
    
    private double step(double d) {
        if (d >= 0.0) return d;
        else return 0.0;
    }
    
    // ���ػ�����״̬
    public double state() { return integrator.state(); }
    
    public double angle() { return angle;}
    
    /**
     * ����һ��cycle����
     * ��ȷ������״̬, �����Ȩֵ
     * [�������Ժ�T���Spikeͬ��]��
     * ��cycle������spikeҪ����cycle����Ч
     * �������Ԫ��spike����[�´�cycle����spike]
     * @param stateON ����cycle��T��״̬
     * @param stateOFF ����cycle��T��״̬
     * */
    public void update(boolean[][] stateON, boolean[][] stateOFF, NodeArray nodeArray) {
        // �޸�Ȩֵ, ��APIʹ�õ�����һѭ����spiked
        modifyEdge(stateON, stateOFF);
        // �޸���Ԫ״̬
        if (spiked) {
            // spiked Ϊ true, ˵���ϸ�cycle���������, ����cycleΪspikeģʽ
            // ���Ա��ָ��²��ռ�����, ������Ҫ���л�����˥��
            integrator.decay();
            spiked = false;
        }
        else {
            // ��һ��cycleû��spike, ��cycleΪprocessģʽ
            // ˥��������, �ռ�����
            integrator.decay();
            collectPriorInput(stateON, stateOFF);
            collectExctieInput(nodeArray);
            collectInhibitInput(nodeArray);
            collectOutput();
            if (integrator.overload) {
                // ����������, ˵����cycle�������㹻ʹ�û���������spikeģʽ
                // ��λ spiked, Ϊ��һcycle����Ȩֵ�ṩ�ο�
                // ��integrator.overload��������cycle����integrator.add����, �ռ�����ʱ������
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
                // �������Խ��                
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
                // �������Խ��                
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
                    // �������Խ��                
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
                    // �������Խ��                
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
                // �������Խ��                
                continue;
            }
        }
        // off
        for (Edge e : edgeToOFF) {
            try {
                e.update(stateOFF[e.x][e.y], spiked);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                // �������Խ��                
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
