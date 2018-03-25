package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zeromq.ZMQ;
import java.time.LocalDateTime;

@SpringBootApplication
public class Worker implements CommandLineRunner
{

    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        ZMQ.Context context = ZMQ.context(1);

        //  Socket to receive messages on
        ZMQ.Socket receiver = context.socket(ZMQ.PULL);
        receiver.connect("tcp://ventilator:5557");

        //  Socket to send messages to
        ZMQ.Socket sender = context.socket(ZMQ.PUSH);
        sender.connect("tcp://sink:5558");

        //  Process tasks forever
        while (!Thread.currentThread ().isInterrupted ()) {
            String string = new String(receiver.recv(0)).trim();
            long msec = Long.parseLong(string);
            //  Simple progress indicator for the viewer
            System.out.flush();
            System.out.print(string + '.');

            //  Do the work
            Thread.sleep(msec);

            System.out.println("-------------------");

            //  Send results to sink
            sender.send("".getBytes(), 0);
        }
        sender.close();
        receiver.close();
        context.term();

    }
}
