package com.example;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zeromq.ZMQ;

import java.time.LocalDateTime;

@SpringBootApplication
public class Sink implements CommandLineRunner {

    public static void main(String[] args) {

        SpringApplication.run(Sink.class, args);

    }

    @Override
    public void run(String... strings) throws Exception {

        //  Prepare our context and socket
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket receiver = context.socket(ZMQ.PULL);
        receiver.bind("tcp://*:5558");

        //  Wait for start of batch
        String string = new String(receiver.recv(0));

        //  Start our clock now
        long tstart = System.currentTimeMillis();

        //  Process 100 confirmations
        int task_nbr;
        int total_msec = 0;     //  Total calculated cost in msecs
        for (task_nbr = 0; task_nbr < 100; task_nbr++) {
            string = new String(receiver.recv(0)).trim();
            if ((task_nbr / 10) * 10 == task_nbr) {
                System.out.print(":");
            } else {
                System.out.print(".");
            }
        }
        //  Calculate and report duration of batch
        long tend = System.currentTimeMillis();

        System.out.println("\nTotal elapsed time: " + (tend - tstart) + " msec");
        receiver.close();
        context.term();

    }
}