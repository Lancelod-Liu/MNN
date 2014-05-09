package gadget;

import java.util.ArrayList;

import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

public class TestImageConstructor {
    private int Width; // ͼ����
    private int Height; // ͼ��߶�
    public ArrayList<double[][]> testImgs = new ArrayList<double[][]>(); // ����ͼ��
    public double[] testImgAngles; // ����ͼ���Ӧ�ĽǶ�, ��λΪ����
    
    public TestImageConstructor(int W, int H) {
        // ���캯��, ��ʼ��ÿ��ͼ��Ŀ�͸�
        Width= W;
        Height = H;
    }
    
    public void makeTestImages(int n) {
        // ����n�Ų���ͼ��
        testImgAngles = new double[n];
        for (int i = 0; i < n; i++) {
            // ���� 0 ~ 2 PI ������Ƕ�
            double angle = StdRandom.uniform(0.0, 2 * Math.PI); 
            testImgAngles[i] = angle;
            testImgs.add(makeTestImage(angle));
        } 
        StdOut.printf("%d Test Images created!\n", testImgs.size());
    }
    
    private double[][] makeTestImage(double angle) {
        // ���ݽǶ�����һ�Ų���ͼ��
        double[][] image = new double[Width][Height];
        double k = Math.tan(angle);
        double adj = 1.0;
        // tan���� �������޷�����������
        if (angle > Math.PI/2 && angle < Math.PI/2 * 3)
            adj = -1.0;
        
        for (int i = 0; i < Width; i++) 
            for (int j = 0; j < Height; j++) {
                // ����˳ʱ��һ���Ǻ�ɫ1 ��ʱ��һ���ǰ�ɫ-1 �ֽ����ǻ�ɫ0
                // y > f(x) ˳ʱ��һ�� ��ɫ1
                if (j > -k * (i - Width/2) + Height/2) {
                    image[i][j] = adj;
                }
                // y > f(x) ��ʱ��һ�� ��ɫ-1
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
