package gadget;

import edu.princeton.cs.introcs.StdOut;

public class Edge {

    public static double WMIN = 0.05;
    public static double WMAX = 1.00; // 权值界限
    private double LEARNRATE = 0.0125; // 学习速率
    private double fplus = -0.075; 
    private double fminus = 0.075; 
    private double bplus = 0.075; 
    private double bminus = -0.15;
    private double fbboth = 1.5; // 不同spike的权值变化系数
    private double weight = WMAX; // 权值 WMIN~WMAX之间
    public int x, y; // 连接的坐标 层无关

    // 初始化权值到最大值
    public Edge(int x, int y) {
        this.x = x;
        this.y = y;        
    }
    
    // 获取权值
    public double weight() {
        return weight;
    }
    
    /**更新权值 
     * 当两者全为真时 权值大幅增加
     * 仅满足
     * @param back = true时 权值减小
     * @param forward = true时 权值不变
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
