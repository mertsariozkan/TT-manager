package com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsPage extends CustomFrame {
    public SettingsPage() {
        super("Arıza ve sipariş ayarları");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        DatabaseOperations databaseOperations = new DatabaseOperations();
        ResultSet faults = databaseOperations.getFaultsDetail();
        ResultSet orders = databaseOperations.getOrdersDetail();
        JPanel panel3;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(""));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setBounds(0, 0, getWidth(), getHeight());
        panel1.setBorder(BorderFactory.createTitledBorder("Arıza düzenle"));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.setBounds(0, 0, getWidth(), getHeight());
        panel2.setBorder(BorderFactory.createTitledBorder("Sipariş düzenle"));
        try {
            while (faults.next()) {
                panel3 = new JPanel();
                panel3.setLayout(new GridLayout(1, 3));
                JLabel label = new JLabel(faults.getString("name"));
                JTextField faultPerc = new JTextField();
                faultPerc.setText(String.valueOf(faults.getInt("percentage")));
                JTextField faultPri = new JTextField();
                faultPri.setText(String.valueOf(faults.getInt("priority")));
                JButton button = new JButton("Kaydet");
                button.addActionListener(actionEvent -> {
                    String sqlString = "UPDATE Categories SET percentage=" + faultPerc.getText() + ", priority=" + faultPri.getText() + " WHERE name='" + label.getText() + "';";
                    PreparedStatement statement = null;
                    try {
                        statement = databaseOperations.conn.prepareStatement(sqlString);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                panel3.add(label);
                panel3.add(faultPerc);
                panel3.add(faultPri);
                panel3.add(button);
                panel1.add(panel3);
            }
            while (orders.next()) {
                panel3 = new JPanel();
                panel3.setLayout(new GridLayout(1, 4));
                JLabel label = new JLabel(orders.getString("name"));
                JTextField orderPerc = new JTextField();
                orderPerc.setText(String.valueOf(orders.getInt("percentage")));
                JTextField orderPri = new JTextField();
                orderPri.setText(String.valueOf(orders.getInt("priority")));
                JButton button = new JButton("Kaydet");
                button.addActionListener(actionEvent -> {
                    String sqlString = "UPDATE Categories SET percentage=" + orderPerc.getText() + ", priority=" + orderPri.getText() + " WHERE name='" + label.getText() + "';";
                    PreparedStatement statement = null;
                    try {
                        statement = databaseOperations.conn.prepareStatement(sqlString);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                panel3.add(label);
                panel3.add(orderPerc);
                panel3.add(orderPri);
                panel3.add(button);
                panel2.add(panel3);
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }

        JButton back = new JButton("Geri dön");
        back.addActionListener(actionEvent -> {
            setVisible(false);
            new JobEnterPage();
        });


        panel.add(panel1);
        panel.add(panel2);
        add(panel);
        add(back);
        setVisible(true);
    }
}
