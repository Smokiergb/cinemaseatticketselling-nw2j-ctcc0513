package com.mycompany.cinemaseatticketselling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Random;

public class Cinemaseatticketselling extends JFrame {

    private JCheckBox[][] seatCheckBoxes; 
    private Hashtable<String, Seat> seatInventory; 
    private Hashtable<String, Purchase> purchases; 
    private JTextArea salesDisplay;
    private JTextField nameField;
    private JButton sellButton, viewInventoryButton;

    public Cinemaseatticketselling () {
        
        setTitle("Cinema Seat Ticket Selling");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        seatInventory = new Hashtable<>();
        purchases = new Hashtable<>();
        initializeSeatInventory();
        JPanel formPanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        JPanel seatPanel = new JPanel(new GridLayout(5, 5, 5, 5));
        seatCheckBoxes = new JCheckBox[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String seatId = "Seat " + (i * 5 + j + 1);
                seatCheckBoxes[i][j] = new JCheckBox(seatId + " ($" + seatInventory.get(seatId).price + ")");
                seatPanel.add(seatCheckBoxes[i][j]);
            }
        }
        seatPanel.setBorder(BorderFactory.createTitledBorder("Select Your Seats"));

        sellButton = new JButton("Sell Tickets");
        sellButton.addActionListener(new SellTicketsHandler());
        viewInventoryButton = new JButton("View Inventory");
        viewInventoryButton.addActionListener(new InventoryHandler());
        salesDisplay = new JTextArea(10, 30);
        salesDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(salesDisplay);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Sales Details"));
        add(formPanel, BorderLayout.NORTH);
        add(seatPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(sellButton);
        bottomPanel.add(viewInventoryButton);
        add(bottomPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.EAST);
        setVisible(true);
    }

    private void initializeSeatInventory() {
        int seatCounter = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String seatId = "Seat " + seatCounter++;
                seatInventory.put(seatId, new Seat(seatId, false, 15));
            }
        }
    }

    private String generateReferenceNumber() {
        Random random = new Random();
        return String.format("REF%05d", random.nextInt(100000));
    }

    private class SellTicketsHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                salesDisplay.setText("Please enter name before selling tickets.");
                return;
            }

            StringBuilder salesInfo = new StringBuilder("Tickets Sold to " + name + ":\n");
            boolean anySold = false;

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    String seatId = "Seat " + (i * 5 + j + 1);
                    if (seatCheckBoxes[i][j].isSelected()) {
                        Seat seat = seatInventory.get(seatId);
                        if (!seat.isSold) {
                            seat.isSold = true; 
                            String referenceNumber = generateReferenceNumber();
                            purchases.put(referenceNumber, new Purchase(name, seatId, referenceNumber, true));
                            salesInfo.append(seatId)
                                    .append(" ($").append(seat.price)
                                    .append(") - Reference: ").append(referenceNumber).append("\n");
                            anySold = true;
                        } else {
                            salesInfo.append(seatId).append(" (Already Sold)\n");
                        }
                    }
                }
            }

            if (!anySold) {
                salesDisplay.setText("No tickets were sold. Please select available seats.");
            } else {
                salesDisplay.setText(salesInfo.toString());
            }
            
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    seatCheckBoxes[i][j].setSelected(false);
                }
            }
        }
    }

    private class InventoryHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame inventoryFrame = new JFrame("Seat Inventory");
            inventoryFrame.setSize(400, 400);
            inventoryFrame.setLayout(new BorderLayout());
            JTextArea inventoryDisplay = new JTextArea();
            inventoryDisplay.setEditable(false);
            StringBuilder inventoryInfo = new StringBuilder("Seat Inventory:\n");

            for (Seat seat : seatInventory.values()) {
                inventoryInfo.append(seat.id)
                        .append(" - ")
                        .append(seat.isSold ? "Sold" : "Available")
                        .append(" ($").append(seat.price).append(")\n");
            }

            inventoryInfo.append("\nPurchase Details:\n");
            for (Purchase purchase : purchases.values()) {
                inventoryInfo.append("Name: ").append(purchase.name)
                        .append(", Seat: ").append(purchase.seatId)
                        .append(", Reference: ").append(purchase.referenceNumber)
                        .append(", Status: ").append(purchase.isAvailable ? "Available" : "Unavailable")
                        .append("\n");
            }

            inventoryDisplay.setText(inventoryInfo.toString());
            inventoryFrame.add(new JScrollPane(inventoryDisplay), BorderLayout.CENTER);
            inventoryFrame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        new Cinemaseatticketselling ();
    }

    private static class Seat {
        String id;
        boolean isSold;
        int price;

        Seat(String id, boolean isSold, int price) {
            this.id = id;
            this.isSold = isSold;
            this.price = price;
        }
    }

    private static class Purchase {
        String name;
        String seatId;
        String referenceNumber;
        boolean isAvailable;

        Purchase(String name, String seatId, String referenceNumber, boolean isAvailable) {
            this.name = name;
            this.seatId = seatId;
            this.referenceNumber = referenceNumber;
            this.isAvailable = isAvailable;
        }
    }
}