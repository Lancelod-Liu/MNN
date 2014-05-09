package gui;

import gadget.MNN;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import node.Node;

public class TrainFrame extends JFrame{
    private JFrame frame = new JFrame(); 
    private JButton btn_train1 = new JButton("ѵ��1��!");
    private JButton btn_train9 = new JButton("ѵ��9��!");
    private JButton btn_train1k = new JButton("ѵ��1k��!");
    private JButton btn_train10k = new JButton("ѵ��1W��!");
    private int w = 16, h = 16;
    private MNN mnn = new MNN(w, h);

    private ImageDisplayer rcptDisp = new ImageDisplayer(w, h); // ��ʾ��֪��ͼ��
    private ImageDisplayer trainDisp = new ImageDisplayer(); // ��ʾѵ��ͼ��
    private ImageDisplayer spikeDisp = new ImageDisplayer(); // ��ʾ����spike״̬
    private ImageDisplayer stateDisp = new ImageDisplayer(); // ��ʾ���������״̬
    private ImageDisplayer tonSpikeDisp = new ImageDisplayer(); // ��ʾT��spike״̬
    private ImageDisplayer toffSpikeDisp = new ImageDisplayer(); // ��ʾT��spike״̬
    private StatsDisplayer statsDisp = new StatsDisplayer(); // ��ʾͳ������
    
    
    
    public TrainFrame() {
        // TODO Auto-generated constructor stub
        mnn.tic.makeTestImages(24); // �������ڼ�������ָ���24��ͼ��
        rcptDisp.setPosition(220, 0);
        trainDisp.setPosition(700, 0);
        spikeDisp.setPosition(880, 0);
        stateDisp.setPosition(1060, 0);
        tonSpikeDisp.setPosition(790, 220);
        toffSpikeDisp.setPosition(970, 220);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        btn_train1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �ڰ�ť�����²��ͷŵĶ���
                mnn.oneCycle();  
                disp();
            }
        });
        btn_train9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �ڰ�ť�����²��ͷŵĶ���
                int c = 9;
                while(c-- > 0)
                    mnn.oneCycle();  
                disp();
            }
        });
        btn_train1k.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �ڰ�ť�����²��ͷŵĶ���
                int c = 1000;
                while(c-- > 0)
                    mnn.oneCycle();  
                disp();
            }
        });
        btn_train10k.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �ڰ�ť�����²��ͷŵĶ���
                int c = 10000;
                while(c-- > 0)
                    mnn.oneCycle();  
                disp();
            }
        });
        
        frame.setLocation(50, 600);
        frame.setLayout(new FlowLayout());
        frame.add(btn_train1);
        frame.add(btn_train9);
        frame.add(btn_train1k);
        frame.add(btn_train10k);
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
    }
    
    private void disp() {
        spikeDisp.showImage(mnn.nodeArray.spike_states(), w, h, "" + mnn.cycle, 10);
        spikeDisp.setMSG("����ͼ[��ɫ:����, ��ɫ:δ����]");
                
        tonSpikeDisp.showImage(mnn.transArrayOn.spike_states(), w, h, "" + mnn.cycle, 10);
        tonSpikeDisp.setMSG("ON��״̬ - ��ɫ: Spiked");
        
        toffSpikeDisp.showImage(mnn.transArrayOff.spike_states(), w, h, "" + mnn.cycle, 10);
        toffSpikeDisp.setMSG("OFF��״̬ - ��ɫ: Spiked");
        
        stateDisp.showImage(mnn.nodeArray.int_states(), w, h, "" + mnn.cycle, 10);
        stateDisp.setMSG("������[��ɫԽ��ֵԽ��]");
        
        trainDisp.showImage(mnn.photoArray.image(), w, h, "" + mnn.cycle, 10); 
        trainDisp.setMSG(String.format("ѵ��ͼ�� - selectivity: %3.2f \n", mnn.nodeArray.Selectivity(mnn.tic.testImgs, mnn.tic.testImgAngles)));
        
        rcptDisp.showImage(mnn.nodeArray.receptiveFields(), 
                (2 * Node.RecpRadix + 1), (2 * Node.RecpRadix + 1), mnn.cycle, 4);
        rcptDisp.setMSG(String.format("%d cycles done!", mnn.cycle));
        
        statsDisp.showDistribution(mnn.weights());
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        TrainFrame tf = new TrainFrame();
    }

}
