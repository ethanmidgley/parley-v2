package OnlineCount;

import java.util.concurrent.locks.ReentrantLock;

public class TSOnlineCount implements OnlineCount {
    
    private Integer count;
    private ReentrantLock lock;

    public TSOnlineCount() {
        this.count = 0;
        this.lock = new ReentrantLock();
    }

    public void increment() {
        lock.lock();
        try{
            count++;
        }
        finally{
            lock.unlock();
        }
    }

    public void decrement() {
        lock.lock();
        try{
            count--;
        }
        finally{
            lock.unlock();
        }
    }

    public String get() {
        return String.valueOf(count);
    }
}
