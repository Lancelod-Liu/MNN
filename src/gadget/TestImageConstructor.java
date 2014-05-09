package gadget;

import java.util.ArrayList;

import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

public class TestImageConstructor {
    private int Width; // 图像宽度
    private int Height; // 图像高度
    public ArrayList<double[][]> testImgs = new ArrayList<double[][]>(); // 测试图像
    public double[] testImgAngles; // 测试图像对应的角度, 单位为弧度
    
    public TestImageConstructor(int W, int H) {
        // 构造函数, 初始化每个图像的宽和高
        Width= W;
        Height = H;
    }
    
    public void makeTestImages(int n) {
        // 生成n张测试图像
        testImgAngles = new double[n];
        for (int i = 0; i < n; i++) {
            // 生成 0 ~ 2 PI 的随机角度
            double angle = StdRandom.uniform(0.0, 2 * Math.PI); 
            testImgAngles[i] = angle;
            testImgs.add(makeTestImage(angle));
        } 
        StdOut.printf("%d Test Images created!\n", testImgs.size());
    }
    
    private double[][] makeTestImage(double angle) {
        // 根据角度生成一张测试图像
        double[][] image = new double[Width][Height];
        double k = Math.tan(angle);
        double adj = 1.0;
        // tan函数 二三象限反相问题修正
        if (angle > Math.PI/2 && angle < Math.PI/2 * 3)
            adj = -1.0;
        
        for (int i = 0; i < Width; i++) 
            for (int j = 0; j < Height; j++) {
                // 方向顺时针一侧是黑色1 逆时针一侧是白色-1 分界线是灰色0
                // y > f(x) 顺时针一侧 黑色1
                if (j > -k * (i - Width/2) + Height/2) {
                    image[i][j] = adj;
                }
                // y > f(x) 逆时针一侧 白色-1
                else if (j < -k * (i - Width/2) + Height/2) {
                    image[i][j] = -adj;
                }
                else {
                    image[i][j] = 0.0;
                }
            }
        return image;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
