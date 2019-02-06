package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class JobEnterPage extends CustomFrame {

    private CustomFrame customFrame;
    private ArrayList<JTextField> faultNumbers;
    private ArrayList<JTextField> orderNumbers;
    private ResultSet resultSet;
    private ArrayList<Integer> sendingFaultNumbers;
    private ArrayList<Integer> sendingOrderNumbers;

    public JobEnterPage() {
        super("Giriş");
        int pageWidth = getWidth();
        int pageHeight = getHeight();
        setLayout(null);
        DatabaseOperations databaseOperations = new DatabaseOperations();

        orderNumbers = new ArrayList<>();
        faultNumbers = new ArrayList<>();
        sendingFaultNumbers = new ArrayList<>();
        sendingOrderNumbers = new ArrayList<>();

        JLabel faultLabel = new JLabel("Toplam arıza sayısı:");
        faultLabel.setBounds(pageWidth/20,pageHeight/20,pageWidth/5,pageHeight/30);
        JTextField faultNumber = new JTextField();
        faultNumber.setBounds(pageWidth/5,pageHeight/20,pageWidth/15,pageHeight/30);
        JPanel panel = new JPanel();
        panel.setBounds(pageWidth/20,pageHeight/10,pageWidth/3,pageHeight/5);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        try {
            resultSet = databaseOperations.getFaults();
            while (resultSet.next()) {
                JPanel panel1 = new JPanel();
                panel1.setLayout(new GridLayout(1,2));
                JLabel name = new JLabel(resultSet.getString("name")+"    ");
                JTextField value = new JTextField();
                faultNumbers.add(value);
                panel1.add(name); panel1.add(value);
                panel.add(panel1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JLabel orderLabel = new JLabel("Toplam sipariş sayısı:");
        orderLabel.setBounds(pageWidth/2,pageHeight/20,pageWidth/5,pageHeight/30);
        JTextField orderNumber = new JTextField();
        orderNumber.setBounds(pageWidth*3/4,pageHeight/20,pageWidth/15,pageHeight/30);
        JPanel panel2 = new JPanel();
        panel2.setBounds(pageWidth/2,pageHeight/10,pageWidth/3,pageHeight/5);
        panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));
        panel2.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        try {
            resultSet = databaseOperations.getOrders();
            while (resultSet.next()) {
                JPanel panel3 = new JPanel();
                panel3.setLayout(new GridLayout(1,2));
                JLabel name = new JLabel(resultSet.getString("name")+"    ");
                JTextField value = new JTextField();
                orderNumbers.add(value);
                panel3.add(name); panel3.add(value);
                panel2.add(panel3);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton autoButton = new JButton("Tahmini değer oluştur");
        autoButton.setBounds(pageWidth/3,pageHeight/3,pageWidth/5,pageHeight/30);
        autoButton.addActionListener(actionEvent -> {
            int totalFault = databaseOperations.getAutoNumber("totalfault");
            int totalOrder = databaseOperations.getAutoNumber("totalorder");
            int faultCount = Integer.parseInt(faultNumber.getText());
            int orderCount = Integer.parseInt(orderNumber.getText());
            ArrayList<Integer> generatedNumbers = databaseOperations.getAllAutoNumbers(true);
            for(int i=0;i<generatedNumbers.size();i++) {
                faultNumbers.get(i).setText(String.valueOf(Math.round((float)faultCount*(float)generatedNumbers.get(i) /(float)totalFault)));
            }
            generatedNumbers = databaseOperations.getAllAutoNumbers(false);
            for(int i=0;i<generatedNumbers.size();i++) {
                orderNumbers.get(i).setText(String.valueOf(Math.round((float)orderCount*(float)generatedNumbers.get(i) /(float)totalOrder)));
            }
        });

        JLabel teamLabel = new JLabel("Ekip sayısı:");
        teamLabel.setBounds(pageWidth/20,pageHeight*2/5,pageWidth/10,pageHeight/30);
        JTextField teamNumberField = new JTextField();
        teamNumberField.setBounds(pageWidth/6,pageHeight*2/5, pageWidth/30,pageHeight/30);
        JLabel maxLabel = new JLabel("Max. iş sayısı (1 ekip için):");
        maxLabel.setBounds(pageWidth/4,pageHeight*2/5,pageWidth/5,pageHeight/30);
        JTextField maxField = new JTextField();
        maxField.setBounds(pageWidth/2,pageHeight*2/5, pageWidth/30,pageHeight/30);

        JButton button = new JButton("Hesapla");
        button.setBounds(pageWidth/20,pageHeight/2,pageWidth/5,pageHeight/30);
        button.addActionListener(actionEvent -> {
            if(databaseOperations.checkIfUpdated()) {
                String sqlString ="UPDATE Datas SET total="+(Integer.valueOf(faultNumber.getText())+Integer.valueOf(orderNumber.getText()))+",ttnetariza=?,issariza=?,pstnthkariza=?,tvbuariza=?,ttnetyenisatis=?,ttnetnakil=?,issyenisatis=?,issnakil=?,tivibuyenisatis=?,tivibunakil=?,pstnthksiparis=? WHERE date=date('now');";
                try {
                    PreparedStatement statement = databaseOperations.conn.prepareStatement(sqlString);
                    for(int i=0;i<faultNumbers.size();i++) {
                        if(faultNumbers.get(i).getText().equals("")) {
                            sendingFaultNumbers.add(0);
                            statement.setInt(i+1,0);
                        } else {
                            sendingFaultNumbers.add(Integer.valueOf(faultNumbers.get(i).getText()));
                            statement.setInt(i+1, Integer.parseInt(faultNumbers.get(i).getText()));
                        }
                    }
                    for(int i=0;i<orderNumbers.size();i++) {
                        if(orderNumbers.get(i).getText().equals("")) {
                            sendingOrderNumbers.add(0);
                            statement.setInt(i+1+faultNumbers.size(),0);
                        } else {
                            sendingOrderNumbers.add(Integer.valueOf(orderNumbers.get(i).getText()));
                            statement.setInt(i+1+faultNumbers.size(), Integer.parseInt(orderNumbers.get(i).getText()));
                        }
                    }
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                StringBuilder sqlString = new StringBuilder("INSERT INTO Datas (" +
                        "date,total,ttnetariza,issariza,pstnthkariza,tvbuariza,ttnetyenisatis,ttnetnakil,issyenisatis,issnakil,tivibuyenisatis,tivibunakil,pstnthksiparis,totalfault,totalorder) " +
                        "VALUES (" +
                        "date('now')," + (Integer.valueOf(faultNumber.getText()) + Integer.valueOf(orderNumber.getText())));
                for (JTextField s : faultNumbers) {
                    if(s.getText().equals("")) {
                        sendingFaultNumbers.add(0);
                        sqlString.append(",0");
                    } else {
                        sendingFaultNumbers.add(Integer.valueOf(s.getText()));
                        sqlString.append("," + s.getText());
                    }
                }
                for (JTextField s : orderNumbers) {
                    if(s.getText().equals("")) {
                        sendingOrderNumbers.add(0);
                        sqlString.append(",0");
                    } else {
                        sendingOrderNumbers.add(Integer.valueOf(s.getText()));
                        sqlString.append("," + s.getText());
                    }
                }
                sqlString.append(","+faultNumber.getText()+","+orderNumber.getText()+");");
                try {
                    databaseOperations.insertData(sqlString.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            setVisible(false);
            if(!teamNumberField.getText().equals("") && !maxField.getText().equals("")) {
                new ResultPage(sendingFaultNumbers, sendingOrderNumbers, Integer.valueOf(teamNumberField.getText()), Integer.valueOf(maxField.getText()),Integer.valueOf(faultNumber.getText()),Integer.valueOf(orderNumber.getText()));
            }
        });

        JButton button1 = new JButton("Arıza ve sipariş türlerini düzenle");
        button1.setBounds(pageWidth/3,pageHeight/2,pageWidth/2,pageHeight/30);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                new SettingsPage();
            }
        });

        add(autoButton);
        add(maxField); add(maxLabel); add(button1);
        add(teamLabel);
        add(teamNumberField);
        add(orderLabel);
        add(orderNumber);
        add(faultLabel);
        add(faultNumber);
        add(panel2);
        add(panel);
        add(button);
        setVisible(true);
    }
}
