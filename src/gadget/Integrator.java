package gadget;

public class Integrator {

    public static final double THRESHOLD = 18.0;
    private final double DECAYPERIOD = 6.0; // 衰退周期
    private double state = 0.0; // 积分器状态值
    public boolean overload; // Node是否应该产生spike
    
    public Integrator() {}
    
    // 带系数积分
    public boolean add(double d, double coeff) {
        state += d * coeff;
        if (isFull()) {
            // 把state设置为负阈值 并设置spiked变量
            state = -THRESHOLD;
            overload = true;
        }
        else {
            overload = false;
        }

        return overload; // 不需要继续加了
    }
    
    // 返回积分器是否超过阈值
    private boolean isFull() {
        return (state >= THRESHOLD);
    }
    
    // 模拟神经元遗忘
    public void decay() {
        state -= (state / DECAYPERIOD);
    }
    
    // 状态值
    public double state() { return state; }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
