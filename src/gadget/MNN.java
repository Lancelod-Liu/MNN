package gadget;
import node.Node;
import array.NodeArray;
import array.PhotocellArray;
import array.TransducerArray;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Stopwatch;
import enums.NodeMode;
import enums.TrainResult;
import gui.ImageDisplayer;
import gui.StatsDisplayer;

public class MNN {
    private int maxcycle = 6000000;
    
    public int cycle = 0; // ѭ��������
    public double selectivity; // ����ѡ����
    private double selectivityThrd = 5.0; // ����ѡ����
    private int Width; // ͼ����
    private int Height; // ͼ��߶�
    public PhotocellArray photoArray; // ��Ƭ��
    public TransducerArray transArrayOn, transArrayOff; // ���в�
    public NodeArray nodeArray; // ����
    public TestImageConstructor tic;
    
    public MNN(int W, int H) {
        // ���캯��, ��ʼ����͸�
        Width= W;
        Height = H;
        tic = new TestImageConstructor(W, H);
    }
    
    /**
     * ʵ��һ��ѵ��
     * @return TrainResult.SUCESS -> Selectivity�ﵽ��
     * @return TrainResult.FAIL  -> ���ѵ�������ﵽ��
     * @return TrainResult.GOON -> ����Ҫѵ��
     * */
    public TrainResult oneCycle() {
        // ��һ�ε���
        if (cycle == 0) {
            photoArray = new PhotocellArray(Width, Height);
            transArrayOn = new TransducerArray(Width, Height, NodeMode.ON);
            transArrayOff = new TransducerArray(Width, Height, NodeMode.OFF);
            nodeArray = new NodeArray(Width, Height);
        }
        
        cycle++;
        if (cycle % 9 == 1) {
            photoArray.update(); // ���´���ͼ��[checked]

            transArrayOn.update(photoArray); // ���»�ȡactivity�����ø���[checked]
            transArrayOff.update(photoArray);
            // selectivity = nodeArray.Selectivity(tic.testImgs, tic.testImgAngles);
        }
        transArrayOn.update(); // ����״̬[checked]
        transArrayOff.update();
        // ���¶���, ������Ȩֵ����[need to check]
        nodeArray.update(transArrayOn.states(), transArrayOff.states()); 
        
        /*if (selectivity > selectivityThrd) {
            StdOut.printf("Train over, selectivity: %3.2f\n", selectivity);
            return TrainResult.SUCESS;
        }*/
        if (cycle > maxcycle)
            return TrainResult.FAIL;
        
        return TrainResult.GOON;
    }
    
    /**
     * ���ض������е�Ȩֵ, ���������ڲ���ͻ��.
     * */    
    public double[] weights() {
        Bag<Edge> edges = nodeArray.edges();
        double[] weights = new double[edges.size()];
        int cnt = 0;
        for (Edge e : edges)
            weights[cnt++] = e.weight();
        return weights;
    }    
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int w = 16, h = 16;
        MNN mnn = new MNN(w, h);
        mnn.tic.makeTestImages(24); // �������ڼ�������ָ���24��ͼ��
        Stopwatch st = new Stopwatch(); // ��ʱ��
        
        ImageDisplayer rcptDisp = new ImageDisplayer(w, h); // ��ʾ��֪��ͼ��
        ImageDisplayer trainDisp = new ImageDisplayer(); // ��ʾѵ��ͼ��
        ImageDisplayer spikeDisp = new ImageDisplayer(); // ��ʾ����spike״̬
        ImageDisplayer stateDisp = new ImageDisplayer(); // ��ʾ���������״̬
        ImageDisplayer tonSpikeDisp = new ImageDisplayer(); // ��ʾT��spike״̬
        ImageDisplayer toffSpikeDisp = new ImageDisplayer(); // ��ʾT��spike״̬
        StatsDisplayer statsDisp = new StatsDisplayer(); // ��ʾͳ������
        
        rcptDisp.setPosition(220, 0);
        trainDisp.setPosition(700, 0);
        spikeDisp.setPosition(880, 0);
        stateDisp.setPosition(1060, 0);
        tonSpikeDisp.setPosition(790, 220);
        toffSpikeDisp.setPosition(970, 220);
        
        while (mnn.oneCycle() == TrainResult.GOON) {
            if (mnn.cycle % 1000 == 0) {
                spikeDisp.showImage(mnn.nodeArray.spike_states(), w, h, "" + mnn.cycle, 8);
                spikeDisp.setMSG("Node��״̬ - ��ɫ: Spiked");
                
                tonSpikeDisp.showImage(mnn.transArrayOn.spike_states(), w, h, "" + mnn.cycle, 8);
                tonSpikeDisp.setMSG("ON��״̬ - ��ɫ: Spiked");
                
                toffSpikeDisp.showImage(mnn.transArrayOff.spike_states(), w, h, "" + mnn.cycle, 8);
                toffSpikeDisp.setMSG("OFF��״̬ - ��ɫ: Spiked");
                
                stateDisp.showImage(mnn.nodeArray.int_states(), w, h, "" + mnn.cycle, 8);
                stateDisp.setMSG("������ - ��ɫԽ��ֵԽ��");
                
                trainDisp.showImage(mnn.photoArray.image(), w, h, "" + mnn.cycle, 8); 
                trainDisp.setMSG(String.format("ѵ��ͼ�� - selectivity: %3.2f", mnn.nodeArray.Selectivity(mnn.tic.testImgs, mnn.tic.testImgAngles)));
                
                rcptDisp.showImage(mnn.nodeArray.receptiveFields(), 
                        (2 * Node.RecpRadix + 1), (2 * Node.RecpRadix + 1), mnn.cycle, 4);
                rcptDisp.setMSG(String.format("%d cycles done!\tTime cost: %2d min %2d sec.", mnn.cycle, (int) st.elapsedTime()/60, (int) st.elapsedTime()%60));
                
                statsDisp.showDistribution(mnn.weights());
                
            }
        }

    }

}
