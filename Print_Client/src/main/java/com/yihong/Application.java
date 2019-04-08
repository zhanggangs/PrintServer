package com.yihong;

import com.yihong.util.Print;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Print print = new Print();
                print.createAndShowGUI();
            }
        });
        SpringApplication.run(Application.class, args);
    }

}

