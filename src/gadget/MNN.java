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
    
    public int cycle = 0; // 循环计数器
    public double selectivity; // 总体选择性
    private double selectivityThrd = 5.0; // 期望选择性
    private int Width; // 图像宽度
    private int Height; // 图像高度
    public PhotocellArray photoArray; // 照片层
    public TransducerArray transArrayOn, transArrayOff; // 传感层
    public NodeArray nodeArray; // 顶层
    public TestImageConstructor tic;
    
    public MNN(int W, int H) {
        // 构造函数, 初始化宽和高
        Width= W;
        Height = H;
        tic = new TestImageConstructor(W, H);
    }
    
    /**
     * 实现一次训练
     * @return TrainResult.SUCESS -> Selectivity达到了
     * @return TrainResult.FAIL  -> 最大训练次数达到了
     * @return TrainResult.GOON -> 还需要训练
     * */
    public TrainResult oneCycle() {
        // 第一次调用
        if (cycle == 0) {
            photoArray = new PhotocellArray(Width, Height);
            transArrayOn = new TransducerArray(Width, Height, NodeMode.ON);
            transArrayOff = new TransducerArray(Width, Height, NodeMode.OFF);
            nodeArray = new NodeArray(Width, Height);
        }
        
        cycle++;
        if (cycle % 9 == 1) {
            photoArray.update(); // 重新创建图像[checked]

            transArrayOn.update(photoArray); // 重新获取activity并重置概率[checked]
            transArrayOff.update(photoArray);
            // selectivity = nodeArray.Selectivity(tic.testImgs, tic.testImgAngles);
        }
        transArrayOn.update(); // 重置状态[checked]
        transArrayOff.update();
        // 更新顶层, 包括了权值更新[need to check]
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
     * 返回顶层所有的权值, 不包括层内部的突触.
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
        mnn.tic.makeTestImages(24); // 生成用于计算性能指标的24张图像
        Stopwatch st = new Stopwatch(); // 定时器
        
        ImageDisplayer rcptDisp = new ImageDisplayer(w, h); // 显示感知域图像
        ImageDisplayer trainDisp = new ImageDisplayer(); // 显示训练图像
        ImageDisplayer spikeDisp = new ImageDisplayer(); // 显示顶层spike状态
        ImageDisplayer stateDisp = new ImageDisplayer(); // 显示顶层积分器状态
        ImageDisplayer tonSpikeDisp = new ImageDisplayer(); // 显示T层spike状态
        ImageDisplayer toffSpikeDisp = new ImageDisplayer(); // 显示T层spike状态
        StatsDisplayer statsDisp = new StatsDisplayer(); // 显示统计数据
        
        rcptDisp.setPosition(220, 0);
        trainDisp.setPosition(700, 0);
        spikeDisp.setPosition(880, 0);
        stateDisp.setPosition(1060, 0);
        tonSpikeDisp.setPosition(790, 220);
        toffSpikeDisp.setPosition(970, 220);
        
        while (mnn.oneCycle() == TrainResult.GOON) {
            if (mnn.cycle % 1000 == 0) {
                spikeDisp.showImage(mnn.nodeArray.spike_states(), w, h, "" + mnn.cycle, 8);
                spikeDisp.setMSG("Node层状态 - 黑色: Spiked");
                
                tonSpikeDisp.showImage(mnn.transArrayOn.spike_states(), w, h, "" + mnn.cycle, 8);
                tonSpikeDisp.setMSG("ON层状态 - 黑色: Spiked");
                
                toffSpikeDisp.showImage(mnn.transArrayOff.spike_states(), w, h, "" + mnn.cycle, 8);
                toffSpikeDisp.setMSG("OFF层状态 - 黑色: Spiked");
                
                stateDisp.showImage(mnn.nodeArray.int_states(), w, h, "" + mnn.cycle, 8);
                stateDisp.setMSG("积分器 - 颜色越深值越高");
                
                trainDisp.showImage(mnn.photoArray.image(), w, h, "" + mnn.cycle, 8); 
                trainDisp.setMSG(String.format("训练图像 - selectivity: %3.2f", mnn.nodeArray.Selectivity(mnn.tic.testImgs, mnn.tic.testImgAngles)));
                
                rcptDisp.showImage(mnn.nodeArray.receptiveFields(), 
                        (2 * Node.RecpRadix + 1), (2 * Node.RecpRadix + 1), mnn.cycle, 4);
                rcptDisp.setMSG(String.format("%d cycles done!\tTime cost: %2d min %2d sec.", mnn.cycle, (int) st.elapsedTime()/60, (int) st.elapsedTime()%60));
                
                statsDisp.showDistribution(mnn.weights());
                
            }
        }

    }

}
