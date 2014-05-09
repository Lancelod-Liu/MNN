package array;

import edu.princeton.cs.introcs.StdRandom;
import gui.ImageDisplayer;


public class PhotocellArray {
    private int Width;
    private int Height; // 宽度高度
    private double[][] image; // 当前图像对应的矩阵
    private int ww = 3, wh = 3;
    private double[][] window = new double[ww][wh];
    private double[][] window_PI = new double[ww][wh];
    
    // 给定宽度和高度构造对应尺寸的随机图像
    public PhotocellArray(int W, int H) {
        // TODO Auto-generated constructor stub
        Width = W;
        Height = H;
        image = new double[Width][Height];
        // 训练模式仅仅是经过高斯平滑的随机域 
        makeGaussian();
        update();
    }
    
    // 进行平滑处理
    public void smooth() {
        imfilter("convolution");
    }

    // 使用窗口对图像进行卷积/相关
    // 参数1, 2 为窗口的宽&高
    private void imfilter(String mode) {
        if (mode.equals("convolution")) {
            image = convolution();
            return;
        }
        throw new RuntimeException("Error filter mode.\n");
    }
    // 卷积
    // 使用window_PI
    private double[][] convolution() {
        double[][] image = new double[Width][Height];
        // 从左上角开始卷积
        for (int i = 0; i < Width; i++) {
            for (int j = 0; j < Height; j++) {
                image[i][j] = convolution(i, j);
            }
        }
        return image;
    }
    
    private double convolution(int i, int j) {
        double result = 0.0;
        for (int k = 0; k < ww; k++) {
            for (int l = 0; l < wh; l++) {
                try {
                    result += image[i + k - ww/2][j + l - wh/2] * window_PI[k][l];
                }
                catch (Exception e) {
                    // 数组越界
                    if (e instanceof ArrayIndexOutOfBoundsException)
                        continue;
                }
            }
        }
        if (result > 1.0) result = 1.0;
        if (result < -1.0) result = -1.0;
        return result;
    }
    
    // 构造高斯算子
    // 18exp(-d^2) - 18exp(-d^2/15^2)
    // d为到算子矩阵中心的距离
    private void makeGaussian() {
        int k = 18;
        for (int i = 0; i < ww; i++) {
            int dx = (i - (ww / 2));
            for (int j = 0; j < wh; j++) { 
                int dy = (j - (wh / 2));             
                double d = Math.sqrt(dx * dx + dy * dy);
                window[i][j] = 18 * Math.exp(-d * d) - 18 * Math.exp(-d * d / (Width - 1) / (Width - 1));
                window_PI[ww - i - 1][wh - j - 1] = window[i][j]; // 旋转180度
            }
        }
    }
    
    public double[][] image() {
        return image;
    }
    
    @Override
    public String toString() {
        String s = new String();
        
        s += "PhotoArray:\n";
        s += "\t";
        for (int i = 0; i < Width; i++) {
            s += Integer.toString(i) + "\t";
        }
        s += "\n";
        for (int i = 0; i < Width; i++) {
            s += Integer.toString(i) + "\t";
            for (int j = 0; j < Height; j++) {
                s += String.format("%4.3f\t", image[i][j]);
            }
            s += "\n";
        }
        
        return s;
    }
    
    // 重新生成图像
    public void update() {
        // 训练模式仅仅是经过高斯平滑的随机域
        for (int i = 0; i < Width; i++) 
            for (int j = 0; j < Height; j++) {
                image[i][j] = StdRandom.uniform(-1.0, 1.0); // [-1, 1)
            }
        smooth();
    }
    
    // 返回对应点的像素值
    public double pixel(int i, int j) {
        return image[i][j];
    }
    
    public static void main(String[] args) 
    {
        // TODO Auto-generated method stub
        ImageDisplayer originDisp = new ImageDisplayer();// 显示训练图像
        ImageDisplayer smoothDisp = new ImageDisplayer();// 显示训练图像
        PhotocellArray pt = new PhotocellArray(16, 16);
        originDisp.showImage(pt.image(), 16, 16, "原始图像", 10);
        smoothDisp.showImage(pt.image(), 16, 16, "平滑图像", 10);
    }

}
