package com.example.vp3.JFrames;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UpdateMahasiswaFrame {
    private MahasiswaFrame parentFrame;
    private int mahasiswaId;

    public UpdateMahasiswaFrame(MahasiswaFrame parentFrame, int mahasiswaId, String nama, String nim) {
        this.parentFrame = parentFrame;
        this.mahasiswaId = mahasiswaId;
        SwingUtilities.invokeLater(() -> createAndShowGUI(nama, nim));
    }

    private void createAndShowGUI(String nama, String nim) {
        JFrame newFrame = new JFrame("Update Mahasiswa");
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Nama:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(nama, 15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameField, gbc);

        JLabel nimLabel = new JLabel("NIM:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(nimLabel, gbc);

        JTextField nimField = new JTextField(nim, 15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nimField, gbc);

        JButton submitButton = new JButton("Update");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String updatedNama = nameField.getText();
                String updatedNim = nimField.getText();
                updateMahasiswaInDatabase(mahasiswaId, updatedNama, updatedNim);
                parentFrame.refreshTableData();
                newFrame.dispose();
            }
        });
        panel.add(submitButton, gbc);

        newFrame.getContentPane().add(panel, BorderLayout.CENTER);
        newFrame.pack();
        newFrame.setVisible(true);
        newFrame.setBounds(100, 100, 300, 200);
    }

    private void updateMahasiswaInDatabase(int id, String nama, String nim) {
        String url = "jdbc:mysql://localhost/mahasiswaku?user=root&password=";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("UPDATE mahasiswa SET nama = ?, nim = ? WHERE id = ?")) {
            stmt.setString(1, nama);
            stmt.setString(2, nim);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void logSQLException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }
}
