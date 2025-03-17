package OnlineCount;


public class NonTSOnlineCount implements OnlineCount {
    private Integer count;

    public NonTSOnlineCount() {
        this.count = 0;
    }

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    public String get() {
        return String.valueOf(count);
    }
}
