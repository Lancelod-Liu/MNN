package gadget;

import edu.princeton.cs.introcs.StdOut;

public class Edge {

    public static double WMIN = 0.05;
    public static double WMAX = 1.00; // Ȩֵ����
    private double LEARNRATE = 0.0125; // ѧϰ����
    private double fplus = -0.075; 
    private double fminus = 0.075; 
    private double bplus = 0.075; 
    private double bminus = -0.15;
    private double fbboth = 1.5; // ��ͬspike��Ȩֵ�仯ϵ��
    private double weight = WMAX; // Ȩֵ WMIN~WMAX֮��
    public int x, y; // ���ӵ����� ���޹�

    // ��ʼ��Ȩֵ�����ֵ
    public Edge(int x, int y) {
        this.x = x;
        this.y = y;        
    }
    
    // ��ȡȨֵ
    public double weight() {
        return weight;
    }
    
    /**����Ȩֵ 
     * ������ȫΪ��ʱ Ȩֵ�������
     * ������
     * @param back = trueʱ Ȩֵ��С
     * @param forward = trueʱ Ȩֵ����
     * */
    public void update(boolean forward, boolean back) {
        if (forward && !back) {
            weight += fplus * LEARNRATE;
            weight += fminus * LEARNRATE;
        }
        if (!forward && back) { 
            weight += bplus * LEARNRATE;
            weight += bminus * LEARNRATE;
        }
        if (forward && back) {
            weight += fbboth * LEARNRATE;
        }
        if (weight < WMIN) weight = WMIN;
        else if (weight > WMAX) weight = WMAX;
    }
    
    @Override
    public String toString() {
        String s = String.format("Connect to (%2d,%2d): %3.2f\n", x, y, weight);

        return s;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        
    }

}
