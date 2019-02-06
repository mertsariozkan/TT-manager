package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultPage extends CustomFrame {
    public ResultPage(ArrayList<Integer> receivedFaultNumbers, ArrayList<Integer> receivedOrderNumbers, int teamNumber, int maxPerTeam, int faultNumber, int orderNumber) {
        super("Rapor");
        setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        DatabaseOperations databaseOperations = new DatabaseOperations();
        ArrayList<Integer> necessaryJobNumbers = new ArrayList<>();

        int index = 0;
        int maxPossibleJob = teamNumber * maxPerTeam;
        int totalJob = faultNumber + orderNumber;
        int necessaryFault = 0, necessaryOrder = 0;
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(receivedFaultNumbers);
        list.addAll(receivedOrderNumbers);
        int listSize = list.size();

        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout(2, 6);
        panel.setLayout(gridLayout);
        panel.setBounds(0, getHeight() / 20, getWidth(), getHeight() / 15);
        GridLayout gridLayout1 = new GridLayout(2, 6);
        JPanel panel1 = new JPanel(gridLayout1);
        panel1.setBounds(0, getHeight() / 4 * 3, getWidth(), getHeight() / 15);
        panel.setBorder(BorderFactory.createTitledBorder("Hedef için yeterli iş sayısı"));
        panel1.setBorder(BorderFactory.createTitledBorder("Sonuç"));

        ResultSet faults = databaseOperations.getFaults();
        ResultSet orders = databaseOperations.getOrders();
        try {
            int i = 0;
            while (faults.next()) {
                String jobName = faults.getString("name");
                int resultNumber = databaseOperations.calculateNecessaryJobNumber(jobName, receivedFaultNumbers.get(index));
                necessaryJobNumbers.add(resultNumber);
                necessaryFault += resultNumber;
                panel.add(new Label(jobName + " : " + necessaryJobNumbers.get(i)){
                    @Override
                    public void setMaximumSize(Dimension maximumSize) {
                        super.setMaximumSize(getPreferredSize());
                    }
                });
                index++;
                i++;
            }
            index = 0;
            while (orders.next()) {
                String jobName = orders.getString("name");
                int resultNumber = databaseOperations.calculateNecessaryJobNumber(jobName, receivedOrderNumbers.get(index));
                necessaryJobNumbers.add(resultNumber);
                panel.add(new Label(jobName + " : " + necessaryJobNumbers.get(i)) {
                    @Override
                    public void setMaximumSize(Dimension maximumSize) {
                        super.setMaximumSize(getPreferredSize());
                    }
                });
                necessaryOrder += resultNumber;
                index++;
                i++;
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }

        ResultSet resultSet1 = databaseOperations.sortByPriorities();
        ArrayList<Integer> resultPriorities = new ArrayList<>();
        ArrayList<String> resultNames = new ArrayList<>();

        int jobleft;

        if (maxPossibleJob >= totalJob) { //do all jobs
            jobleft = totalJob;
            panel1.add(new Label("Tüm arızalar ("+faultNumber+") ve tüm siparişler ("+orderNumber+") yapılabilir."));

        } else if (maxPossibleJob >= necessaryFault + necessaryOrder && maxPossibleJob < totalJob) {
            //do all necessary jobs + some of remaining jobs
            jobleft = maxPossibleJob;
            for(int n=0;n<list.size();n++) {
                jobleft -= necessaryJobNumbers.get(n);
                System.out.println(jobleft);
                list.set(n,list.get(n)-necessaryJobNumbers.get(n));
            }
            try {
                while (resultSet1.next()) {
                    resultPriorities.add(resultSet1.getInt("priority"));
                    resultNames.add(resultSet1.getString("name"));
                }
                for (int i = 0; i < listSize; i++) {
                    int min = 1000;
                    int minIdx = 0;
                    String name = null;

                    for (int j = 0; j < resultNames.size(); j++) {
                        if (min > resultPriorities.get(j)) {
                            min = resultPriorities.get(j);
                            name = resultNames.get(j);
                            minIdx = j;
                        }
                    }
                    if (jobleft >= list.get(minIdx)) {
                        panel1.add(new Label(name + " : " + (list.get(minIdx)+necessaryJobNumbers.get(minIdx))));
                        jobleft -= list.get(minIdx);
                        System.out.println(jobleft);
                    } else {
                        panel1.add(new Label(name + " : " + (jobleft+necessaryJobNumbers.get(minIdx))));
                        list.set(minIdx, jobleft);
                        jobleft = 0;
                    }

                    necessaryJobNumbers.remove(minIdx);
                    list.remove(minIdx);
                    resultNames.remove(minIdx);
                    resultPriorities.remove(minIdx);
                }
            } catch (SQLException e) {
                e.getStackTrace();
            }
        } else {
            jobleft = maxPossibleJob;
            try {
                while (resultSet1.next()) {
                    resultPriorities.add(resultSet1.getInt("priority"));
                    resultNames.add(resultSet1.getString("name"));
                }
                for (int i = 0; i < list.size(); i++) {
                    int min = 1000;
                    int minIdx = 0;
                    String name = null;

                    for (int j = 0; j < resultNames.size(); j++) {
                        if (min > resultPriorities.get(j)) {
                            min = resultPriorities.get(j);
                            name = resultNames.get(j);
                            minIdx = j;
                        }
                    }
                    if (jobleft >= necessaryJobNumbers.get(minIdx)) {
                        panel1.add(new Label(name + " : " + necessaryJobNumbers.get(minIdx)));
                        jobleft -= necessaryJobNumbers.get(minIdx);
                        System.out.println(jobleft);
                    } else {
                        panel1.add(new Label(name + " : " + jobleft));
                        necessaryJobNumbers.set(minIdx, jobleft);
                        jobleft = 0;
                    }

                    if (jobleft == 0) {
                        break;
                    }
                    necessaryJobNumbers.remove(minIdx);
                    resultNames.remove(minIdx);
                    resultPriorities.remove(minIdx);
                }
            } catch (SQLException e) {
                e.getStackTrace();
            }
        }

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(2,3));
        panel2.add(new JLabel("Gelen arıza: " + faultNumber));
        panel2.add(new JLabel("Gelen sipariş: " + orderNumber));
        panel2.add(new JLabel("Yapılması gereken arıza: " + necessaryFault));
        panel2.add(new JLabel("Yapılması gereken sipariş: " + necessaryOrder));
        panel2.add(new JLabel("Maksimum yapılabilir iş sayısı: " + maxPossibleJob));

        JButton backButton = new JButton("Geri dön");
        backButton.addActionListener(actionEvent -> {
           setVisible(false);
           new JobEnterPage();
        });

        add(panel);
        add(panel2);
        add(panel1);
        add(backButton);
        setVisible(true);
    }
}
