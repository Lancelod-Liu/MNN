package array;

import edu.princeton.cs.introcs.StdRandom;
import gui.ImageDisplayer;


public class PhotocellArray {
    private int Width;
    private int Height; // ��ȸ߶�
    private double[][] image; // ��ǰͼ���Ӧ�ľ���
    private int ww = 3, wh = 3;
    private double[][] window = new double[ww][wh];
    private double[][] window_PI = new double[ww][wh];
    
    // ������Ⱥ͸߶ȹ����Ӧ�ߴ�����ͼ��
    public PhotocellArray(int W, int H) {
        // TODO Auto-generated constructor stub
        Width = W;
        Height = H;
        image = new double[Width][Height];
        // ѵ��ģʽ�����Ǿ�����˹ƽ��������� 
        makeGaussian();
        update();
    }
    
    // ����ƽ������
    public void smooth() {
        imfilter("convolution");
    }

    // ʹ�ô��ڶ�ͼ����о��/���
    // ����1, 2 Ϊ���ڵĿ�&��
    private void imfilter(String mode) {
        if (mode.equals("convolution")) {
            image = convolution();
            return;
        }
        throw new RuntimeException("Error filter mode.\n");
    }
    // ���
    // ʹ��window_PI
    private double[][] convolution() {
        double[][] image = new double[Width][Height];
        // �����Ͻǿ�ʼ���
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
                    // ����Խ��
                    if (e instanceof ArrayIndexOutOfBoundsException)
                        continue;
                }
            }
        }
        if (result > 1.0) result = 1.0;
        if (result < -1.0) result = -1.0;
        return result;
    }
    
    // �����˹����
    // 18exp(-d^2) - 18exp(-d^2/15^2)
    // dΪ�����Ӿ������ĵľ���
    private void makeGaussian() {
        int k = 18;
        for (int i = 0; i < ww; i++) {
            int dx = (i - (ww / 2));
            for (int j = 0; j < wh; j++) { 
                int dy = (j - (wh / 2));             
                double d = Math.sqrt(dx * dx + dy * dy);
                window[i][j] = 18 * Math.exp(-d * d) - 18 * Math.exp(-d * d / (Width - 1) / (Width - 1));
                window_PI[ww - i - 1][wh - j - 1] = window[i][j]; // ��ת180��
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
    
    // ��������ͼ��
    public void update() {
        // ѵ��ģʽ�����Ǿ�����˹ƽ���������
        for (int i = 0; i < Width; i++) 
            for (int j = 0; j < Height; j++) {
                image[i][j] = StdRandom.uniform(-1.0, 1.0); // [-1, 1)
            }
        smooth();
    }
    
    // ���ض�Ӧ�������ֵ
    public double pixel(int i, int j) {
        return image[i][j];
    }
    
    public static void main(String[] args) 
    {
        // TODO Auto-generated method stub
        ImageDisplayer originDisp = new ImageDisplayer();// ��ʾѵ��ͼ��
        ImageDisplayer smoothDisp = new ImageDisplayer();// ��ʾѵ��ͼ��
        PhotocellArray pt = new PhotocellArray(16, 16);
        originDisp.showImage(pt.image(), 16, 16, "ԭʼͼ��", 10);
        smoothDisp.showImage(pt.image(), 16, 16, "ƽ��ͼ��", 10);
    }

}
