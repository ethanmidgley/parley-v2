package ServerLogger;

import Message.Message;
import MessageQueue.MessageQueue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeLogger implements Logger {
  private final MessageQueue logQ;
  private final List<Message> logContents;
  private final Thread timingThread;
  private final File file;
  private final Lock logLock;
  private final int TIME_LIM;

  public ThreadSafeLogger(MessageQueue logQ, File file) {
    this.logQ = logQ;
    this.file = file;
    this.TIME_LIM = 60;
    this.logContents = new ArrayList<>();

    //Andrew's GOAT'd timer
    this.timingThread = new Thread(() -> {
      while(true) {
        try {
          Thread.sleep(TIME_LIM * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        this.write();
      }
    });

    this.logLock = new ReentrantLock();
  }

  public ThreadSafeLogger(MessageQueue logQ, File file, int TIME_LIM) {
    this.logQ = logQ;
    this.file = file;
    this.TIME_LIM = TIME_LIM;
    this.logContents = new ArrayList<>();

    //Andrew's GOAT'd timer
    this.timingThread = new Thread(() -> {
      while(true) {
        try {
          Thread.sleep(TIME_LIM * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        this.write();
      }
    });

    this.logLock = new ReentrantLock();
  }


  @Override
  public void run() {
    this.timingThread.start();
    log();
  }

  //writer to our log in memory
  public void log() {
    while(true) {
      Message m = logQ.poll();
      if(m != null) {
        logLock.lock();
        logContents.add(m);
        logLock.unlock();
      }
    }
  }

  //reader of our log in memory
  //its called write it's still writing to non-volatile storage
  private void write() {
    //no need to write nothing
    logLock.lock();
//    System.out.println("starting to write");
    try{
      if(logContents.isEmpty()) return;

      //sort the messages by the order they were sent
      logContents.sort(Comparator.comparing(Message::getSendDate));

      //think the try automatically closes fw considering the call to fw.close() is greyed out
      //TODO: set append flag to false when testing/demonstrating ->  easier to read
      try (FileWriter fw = new FileWriter(file,true))  {
        for(Message m : logContents) {
          fw.write(m.toString() + System.lineSeparator());
        }
//        System.out.println("successful write");
        logContents.clear();
      } catch (IOException e) {
        System.out.println("IOException in log");
        e.printStackTrace();
      }
    } finally {
      logLock.unlock();
    }
//    System.out.println("finished");
  }
}