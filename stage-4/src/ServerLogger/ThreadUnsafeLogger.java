package ServerLogger;

import Message.Message;
import MessageQueue.MessageQueue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ThreadUnsafeLogger implements Logger {
  private final MessageQueue logQ;
  private final List<Message> logContents;
  private final Thread timingThread;
  private final File file;
  private int TIME_LIM;

  public ThreadUnsafeLogger(MessageQueue logQ, File file) {
    this.logQ = logQ;
    this.file = file;
    this.logContents = new ArrayList<>();
    this.TIME_LIM = 60;

    this.timingThread = new Thread(() -> {
        while(true){
          try {
            Thread.sleep(TIME_LIM*1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          this.write();
        }
    });
  }

  public ThreadUnsafeLogger(MessageQueue logQ, File file, int TIME_LIM) {
    this.logQ = logQ;
    this.file = file;
    this.TIME_LIM = TIME_LIM;
    this.logContents = new ArrayList<>();

    this.timingThread = new Thread(() -> {
      while(true){
        try {
          Thread.sleep(TIME_LIM*1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        this.write();
      }
    });
  }


  @Override
  public void run() {
    timingThread.start();
    log();
  }

  public void log() {
    while(true) {
      Message m = logQ.poll();
      if(m != null) {
//        System.out.println("We got a message to log");
        logContents.add(m);
      }
    }
  }

  private void write() {
    //no need to write nothing
    System.out.println("starting to write");
    if(logContents.isEmpty()) return;

    //sort the messages by the order they were sent
    System.out.println(logContents.size());
    logContents.sort(Comparator.comparing(Message::getSendDate));

    //think the try automatically closes fw considering the call to fw.close() is greyed out
    try (FileWriter fw = new FileWriter(file))  {
      for(Message m : logContents) {
        System.out.println(m.toString());
        fw.write(m + System.lineSeparator());
        fw.flush();
      }

      System.out.println("successful write");
      logContents.clear();
    } catch (IOException e) {
      System.out.println("IOException in log");
//        e.printStackTrace();
    }
    System.out.println("finished");
  }
}