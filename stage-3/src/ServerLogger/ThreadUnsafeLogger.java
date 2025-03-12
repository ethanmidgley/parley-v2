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
  private MessageQueue logQ;
  private List<Message> log_contents;
  private File file;
  private static final int TIME_LIM = 60;

  public ThreadUnsafeLogger(MessageQueue logQ, File file) {
    this.logQ = logQ;
    this.file = file;
    this.log_contents = new ArrayList<>();
  }

  @Override
  public void run() {
    log();
  }

  public void log() {
    while(true) {
      long t_0 = System.currentTimeMillis();
      long seconds_passed = 0;


//    TS PMO probs don't work :( | will most likely remove | just said TSPMO for funnies
      try {
        Thread.sleep(500);  // Reduce CPU usage
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      //will store the data to non-volatile storage roughly every TIME_LIM seconds
      while(seconds_passed < TIME_LIM) {
        //FIXME: in the event that head == tail this might pause the loop
        Message m = logQ.poll();
        if(m != null) log_contents.add(m);


        seconds_passed = (t_0 - System.currentTimeMillis()) / 1000;
      }

      //no need to write nothing
      if(log_contents.isEmpty()) continue;

      //sort the messages by the order they were sent
      log_contents.sort(Comparator.comparing(Message::getSendDate));

      //think the try automatically closes fw considering the call to fw.close() is greyed out
      try (FileWriter fw = new FileWriter(file)) {
        for(Message m : log_contents) {
          fw.write(m.toString() + System.lineSeparator());
        }
        System.out.println("successful write");
      } catch (IOException e) {
        System.out.println("the fuck????");
        e.printStackTrace();
      }
    }
  }
}
