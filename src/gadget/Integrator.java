package gadget;

public class Integrator {

    public static final double THRESHOLD = 18.0;
    private final double DECAYPERIOD = 6.0; // ˥������
    private double state = 0.0; // ������״ֵ̬
    public boolean overload; // Node�Ƿ�Ӧ�ò���spike
    
    public Integrator() {}
    
    // ��ϵ������
    public boolean add(double d, double coeff) {
        state += d * coeff;
        if (isFull()) {
            // ��state����Ϊ����ֵ ������spiked����
            state = -THRESHOLD;
            overload = true;
        }
        else {
            overload = false;
        }

        return overload; // ����Ҫ��������
    }
    
    // ���ػ������Ƿ񳬹���ֵ
    private boolean isFull() {
        return (state >= THRESHOLD);
    }
    
    // ģ����Ԫ����
    public void decay() {
        state -= (state / DECAYPERIOD);
    }
    
    // ״ֵ̬
    public double state() { return state; }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
