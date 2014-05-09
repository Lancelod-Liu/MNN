package node;

import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;
import enums.Area;
import enums.NodeMode;
import array.PhotocellArray;

public class TransducerNode {
    private final double CenterRadix = 1.5;
    private final double SurroundRadix = 3.0;
    private double Kc = 1.0;
    private double Ks = -1.0;
    private int x, y; // 坐标信息
    private double activity; // 活跃度
    private double possibility; // spike概率
    public boolean spiked; // 有无spike
    
    public TransducerNode(int x, int y, NodeMode mode) {
        this.x = x;
        this.y = y;
        activity = 0.0;
        possibility = 0.0;
        if (mode == NodeMode.OFF) {
            Kc = -1.0;
            Ks = 1.0;
        }
    }
    
    // 更新活跃度和概率信息 并通过随机数确定是否spike 
    public void update(PhotocellArray photoArray) {
        int sr = (int) SurroundRadix;
        
        for (int i = x - sr; i <= x + sr; i++)
            for (int j = y - sr; j <= y + sr; j++) {
                try {
                    double a = photoArray.pixel(i, j);
                    Area l = locale(i, j);
                    switch (l) {
                        case CENTER:
                            a *= Kc;                         
                            break;
                        case SURROUND:
                            a *= Ks; 
                            break;
                        case NONE: // 不属于收集区域
                        default:
                            a = 0.0;                    
                    }
                    activity += a;
                }
                catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    // 如果数组越界                
                    continue;
                }
            }
        setPossibility();
        resetState(); // 更新possibility的值 修改spiked值
    }
    
    private Area locale(int x, int y) {
        double d = distance(x, y);
        if (d <= CenterRadix) return Area.CENTER;
        else if (d <= SurroundRadix) return Area.SURROUND;
        return Area.NONE;
        
    }
    
    private double distance(int x, int y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private void setPossibility() {
        possibility = 1 / (2 + Math.exp(7 - 0.3*activity));
    }
    
    // 重置spike信息
    public void resetState() {
        if (StdRandom.uniform() < possibility)
            spiked = true;
        else
            spiked = false;
    }
    
    @Override
    public String toString() 
    {
        String s = String.format("%4.3f", activity);        
        
        return s;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
       
        PhotocellArray pa = new PhotocellArray(16, 16);
        TransducerNode tn = new TransducerNode(0, 0, NodeMode.ON);       
        tn.update(pa);
        StdOut.println(pa.toString());
        StdOut.println(tn.toString());
    }
}
