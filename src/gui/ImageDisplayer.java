package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import array.PhotocellArray;
import edu.princeton.cs.introcs.StdOut;

public class ImageDisplayer {
    private int Width; // ����ͼ�����
    private int Height; // ����ͼ�����
    private JFrame frame = new JFrame(); // ������ʾ�����frame
    private JTextField msg = new JTextField(); // ������ʾ״̬
    private JLabel label = new JLabel(); // ������ʾ������Ƭ
    private JLabel[][] labels; // ������ʾͼ���label
    
    public ImageDisplayer() {
        // ���캯��, ��ʼ�����岼��
        Width= 1;
        Height = 1;
        msg.setEditable(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add("North", label);
        frame.add("South", msg);
    }
    
    public ImageDisplayer(int W, int H) {
        // ���캯��, ��ʼ�����岼��
        Width= W;
        Height = H;
        msg.setEditable(false);
        JPanel panel = new JPanel(); // ����ͼ�����������
        panel.setLayout(new GridLayout(Height, Width, 2, 2)); // ʹ�ø��Ӳ���
        frame.setLayout(new BorderLayout()); // ʹ�÷�λ����
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        labels = new JLabel[Width][Height];
        for (int i = 0; i < Width; i++) 
            for (int j = 0; j < Height; j++) {
                labels[i][j] = new JLabel();
                panel.add(labels[i][j]);
            }
        panel.setBackground(Color.YELLOW);
        frame.add("North", panel);
        frame.add("South", msg);
    }
    
    public void setPosition(int x, int y) {
        frame.setLocation(x, y);
    }
    
    public void setMSG(String s) {
        msg.setText(s);
        
    }
    
    /**
     * @param image: ͼ������ֵ, -1 Ϊ��, 0 Ϊ��, 1 Ϊ��
     * @param message: ͼ��ĸ�����Ϣ
     * */
    public void showImage(double[][] image, int W, int H, Object message, int scale) {
        int realW = scale * W;
        int realH = scale * H;
        // ��frame����ʾһ�Ų���ͼ��
        BufferedImage img = new BufferedImage(realW, realH, BufferedImage.TYPE_BYTE_GRAY);
        
        for (int i = 0; i < W; i++) 
            for (int j = 0; j < H; j++) {
                // 1 -> 0 -> ��ɫ  
                // -1 -> 255 ->��ɫ
                int grayscale = (int) ((1 - image[i][j]) * 255 / 2.0 );
                for (int m = 0; m < scale; m++) 
                    for (int n = 0; n < scale; n++) {
                        img.setRGB(i * scale + m, j * scale + n, new Color(grayscale, grayscale ,grayscale).getRGB());      
                    }
            }
        label.setIcon(new ImageIcon(img));
        
        if (message instanceof Double)
            frame.setTitle(String.format("ˮƽ�н�: %3.2f��", message));
        if (message instanceof String)
            frame.setTitle((String) message);
        frame.pack();
        if (!frame.isVisible()) 
            frame.setVisible(true);            
    }
    /**
     * @param image: ͼ������ֵ, -1 Ϊ��, 0 Ϊ��, 1 Ϊ��
     * @param message: ������Ϣ
     * @param scale: �Ŵ�ϵ��
     * */
    public void showImage(Iterable<double[][]> images, int W, int H, Object message, int scale) {
        // ��frame����ʾ���Ų���ͼ��
        int lbcount = 0; // ͼ�������
        
        try {
            // �������ͼ������Ƿ����
            if (((ArrayList<double[][]>)images).size() != Width * Height) 
                throw new java.lang.IllegalArgumentException();
        }
        catch (java.lang.IllegalArgumentException e) {
            StdOut.printf("Number of input images mismatch! Expected: %d, Input: %d\n", Width * Height, ((ArrayList<double[][]>)images).size());
            return;
        }
        // ����ͼ��
        int realW = scale * W;
        int realH = scale * H;
        for (double[][] image : images) {
            BufferedImage img = new BufferedImage(realW, realH, BufferedImage.TYPE_BYTE_GRAY);
            for (int i = 0; i < W; i++) 
                for (int j = 0; j < H; j++) {
                    // 1 -> 0 -> ��ɫ  
                    // -1 -> 255 ->��ɫ
                    int grayscale = (int) ((1 - image[i][j]) / 2.0 * 255);
                    for (int m = 0; m < scale; m++) 
                        for (int n = 0; n < scale; n++) {
                            img.setRGB(i * scale + m, j * scale + n, new Color(grayscale, grayscale ,grayscale).getRGB());  
                        }
                }
                labels[lbcount % Width][lbcount / Width].setIcon(new ImageIcon(img)); // ��ʼ��ÿ���ڵ��Ӧ�ĸ�֪��ͼ��
                lbcount++;
        }
        frame.pack();
        frame.setTitle(String.format("%s", message.toString()));
        if (!frame.isVisible()) 
            frame.setVisible(true);
    }
    
/*    *//**
     * Returns a JLabel containing this picture, for embedding in a JPanel,
     * JFrame or other GUI widget.
     *//*
    public JLabel getJLabel(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        return new JLabel(icon);
    }
    */

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        ImageDisplayer trainDisp = new ImageDisplayer();// ��ʾѵ��ͼ��
        PhotocellArray pt = new PhotocellArray(16, 16);
        trainDisp.showImage(pt.image(), 16, 16, "ѵ��ͼ��", 10);
    }

}
