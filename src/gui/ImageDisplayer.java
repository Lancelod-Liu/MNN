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
    private int Width; // 横向图像个数
    private int Height; // 纵向图像个数
    private JFrame frame = new JFrame(); // 用于显示结果的frame
    private JTextField msg = new JTextField(); // 用于显示状态
    private JLabel label = new JLabel(); // 用于显示单张照片
    private JLabel[][] labels; // 用于显示图像的label
    
    public ImageDisplayer() {
        // 构造函数, 初始化整体布局
        Width= 1;
        Height = 1;
        msg.setEditable(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add("North", label);
        frame.add("South", msg);
    }
    
    public ImageDisplayer(int W, int H) {
        // 构造函数, 初始化整体布局
        Width= W;
        Height = H;
        msg.setEditable(false);
        JPanel panel = new JPanel(); // 放置图像数组的容器
        panel.setLayout(new GridLayout(Height, Width, 2, 2)); // 使用格子布局
        frame.setLayout(new BorderLayout()); // 使用方位布局
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
     * @param image: 图像像素值, -1 为白, 0 为灰, 1 为黑
     * @param message: 图像的附加信息
     * */
    public void showImage(double[][] image, int W, int H, Object message, int scale) {
        int realW = scale * W;
        int realH = scale * H;
        // 在frame里显示一张测试图像
        BufferedImage img = new BufferedImage(realW, realH, BufferedImage.TYPE_BYTE_GRAY);
        
        for (int i = 0; i < W; i++) 
            for (int j = 0; j < H; j++) {
                // 1 -> 0 -> 黑色  
                // -1 -> 255 ->白色
                int grayscale = (int) ((1 - image[i][j]) * 255 / 2.0 );
                for (int m = 0; m < scale; m++) 
                    for (int n = 0; n < scale; n++) {
                        img.setRGB(i * scale + m, j * scale + n, new Color(grayscale, grayscale ,grayscale).getRGB());      
                    }
            }
        label.setIcon(new ImageIcon(img));
        
        if (message instanceof Double)
            frame.setTitle(String.format("水平夹角: %3.2f度", message));
        if (message instanceof String)
            frame.setTitle((String) message);
        frame.pack();
        if (!frame.isVisible()) 
            frame.setVisible(true);            
    }
    /**
     * @param image: 图像像素值, -1 为白, 0 为灰, 1 为黑
     * @param message: 附加信息
     * @param scale: 放大系数
     * */
    public void showImage(Iterable<double[][]> images, int W, int H, Object message, int scale) {
        // 在frame里显示数张测试图像
        int lbcount = 0; // 图像计数器
        
        try {
            // 检测输入图像个数是否符合
            if (((ArrayList<double[][]>)images).size() != Width * Height) 
                throw new java.lang.IllegalArgumentException();
        }
        catch (java.lang.IllegalArgumentException e) {
            StdOut.printf("Number of input images mismatch! Expected: %d, Input: %d\n", Width * Height, ((ArrayList<double[][]>)images).size());
            return;
        }
        // 生成图像
        int realW = scale * W;
        int realH = scale * H;
        for (double[][] image : images) {
            BufferedImage img = new BufferedImage(realW, realH, BufferedImage.TYPE_BYTE_GRAY);
            for (int i = 0; i < W; i++) 
                for (int j = 0; j < H; j++) {
                    // 1 -> 0 -> 黑色  
                    // -1 -> 255 ->白色
                    int grayscale = (int) ((1 - image[i][j]) / 2.0 * 255);
                    for (int m = 0; m < scale; m++) 
                        for (int n = 0; n < scale; n++) {
                            img.setRGB(i * scale + m, j * scale + n, new Color(grayscale, grayscale ,grayscale).getRGB());  
                        }
                }
                labels[lbcount % Width][lbcount / Width].setIcon(new ImageIcon(img)); // 初始化每个节点对应的感知域图像
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

        ImageDisplayer trainDisp = new ImageDisplayer();// 显示训练图像
        PhotocellArray pt = new PhotocellArray(16, 16);
        trainDisp.showImage(pt.image(), 16, 16, "训练图像", 10);
    }

}
