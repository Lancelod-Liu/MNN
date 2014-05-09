package gui;

import gadget.Edge;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class StatsDisplayer {

    private static JFrame frame = new JFrame();

    private static JTextArea info = new JTextArea();

    public StatsDisplayer() {
        // TODO Auto-generated constructor stub
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        info.setEditable(false);
        info.setBounds(20, 20, 200, 400);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        frame.add("Center", info);        
        frame.setTitle("Ȩֵ�ֲ�");
    }
    
    public void showDistribution(double[] a) {
        double max = Edge.WMAX;
        double min = Edge.WMIN;
        int N = 21; // �������
        double interval = (max - min) / N; // ���䳤��
        int[] itvCnt = new int[N]; // ���������
        for (double d : a) {
            int i = (int) ((d - min) / interval);
            if (i == N) i = N - 1;
            itvCnt[i]++;
        }       
        String txt = new String();
        for (int i = 0; i < N; i++) {
            txt += String.format("[%5.4f, %5.4f]: %5d��  ", min + interval * i, min + interval * (i + 1), itvCnt[i]);
            if ((i+1) % 1 == 0) txt +='\n';
        }
        info.setText("");
        info.setText(txt);
        frame.pack();
        if (!frame.isVisible()) 
            frame.setVisible(true);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
