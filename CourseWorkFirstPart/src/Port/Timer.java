package Port;

public class Timer implements Runnable{
    private int nowTime = 0;

    @Override
    public void run() {
        nowTime++;
    }

    public int getTime() {
        return nowTime;
    }
}
