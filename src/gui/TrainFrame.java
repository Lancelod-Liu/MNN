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
    private JButton btn_train1 = new JButton("训练1次!");
    private JButton btn_train9 = new JButton("训练9次!");
    private JButton btn_train1k = new JButton("训练1k次!");
    private JButton btn_train10k = new JButton("训练1W次!");
    private int w = 16, h = 16;
    private MNN mnn = new MNN(w, h);

    private ImageDisplayer rcptDisp = new ImageDisplayer(w, h); // 显示感知域图像
    private ImageDisplayer trainDisp = new ImageDisplayer(); // 显示训练图像
    private ImageDisplayer spikeDisp = new ImageDisplayer(); // 显示顶层spike状态
    private ImageDisplayer stateDisp = new ImageDisplayer(); // 显示顶层积分器状态
    private ImageDisplayer tonSpikeDisp = new ImageDisplayer(); // 显示T层spike状态
    private ImageDisplayer toffSpikeDisp = new ImageDisplayer(); // 显示T层spike状态
    private StatsDisplayer statsDisp = new StatsDisplayer(); // 显示统计数据
    
    
    
    public TrainFrame() {
        // TODO Auto-generated constructor stub
        mnn.tic.makeTestImages(24); // 生成用于计算性能指标的24张图像
        rcptDisp.setPosition(220, 0);
        trainDisp.setPosition(700, 0);
        spikeDisp.setPosition(880, 0);
        stateDisp.setPosition(1060, 0);
        tonSpikeDisp.setPosition(790, 220);
        toffSpikeDisp.setPosition(970, 220);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        btn_train1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 在按钮被按下并释放的动作
                mnn.oneCycle();  
                disp();
            }
        });
        btn_train9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 在按钮被按下并释放的动作
                int c = 9;
                while(c-- > 0)
                    mnn.oneCycle();  
                disp();
            }
        });
        btn_train1k.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 在按钮被按下并释放的动作
                int c = 1000;
                while(c-- > 0)
                    mnn.oneCycle();  
                disp();
            }
        });
        btn_train10k.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 在按钮被按下并释放的动作
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
        spikeDisp.setMSG("激发图[黑色:激发, 白色:未激发]");
                
        tonSpikeDisp.showImage(mnn.transArrayOn.spike_states(), w, h, "" + mnn.cycle, 10);
        tonSpikeDisp.setMSG("ON层状态 - 黑色: Spiked");
        
        toffSpikeDisp.showImage(mnn.transArrayOff.spike_states(), w, h, "" + mnn.cycle, 10);
        toffSpikeDisp.setMSG("OFF层状态 - 黑色: Spiked");
        
        stateDisp.showImage(mnn.nodeArray.int_states(), w, h, "" + mnn.cycle, 10);
        stateDisp.setMSG("积分器[颜色越深值越高]");
        
        trainDisp.showImage(mnn.photoArray.image(), w, h, "" + mnn.cycle, 10); 
        trainDisp.setMSG(String.format("训练图像 - selectivity: %3.2f \n", mnn.nodeArray.Selectivity(mnn.tic.testImgs, mnn.tic.testImgAngles)));
        
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
