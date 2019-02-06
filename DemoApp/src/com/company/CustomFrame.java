package com.company;

import javax.swing.*;

public class CustomFrame extends JFrame {
    public CustomFrame(String title) {
        super(title);
        setLayout(null);
        setSize(1000,800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
