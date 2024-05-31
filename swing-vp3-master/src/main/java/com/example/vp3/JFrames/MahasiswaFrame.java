package com.example.vp3.JFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;

public class MahasiswaFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    private static final String DATABASE_URL = "jdbc:mysql://localhost/mahasiswaku?user=root&password=";

    private Connection connect() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException ex) {
            logSQLException(ex);
            return null;
        }
    }

    private void logSQLException(SQLException ex) {
        System.err.println("SQLException: " + ex.getMessage());
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("VendorError: " + ex.getErrorCode());
    }

    public MahasiswaFrame() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        JFrame jFrame = new JFrame("Aplikasi Mahasiswa");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; 
            }
        };
        tableModel.addColumn("No");
        tableModel.addColumn("ID");
        tableModel.addColumn("Nama");
        tableModel.addColumn("NIM");
        tableModel.addColumn("Edit");
        tableModel.addColumn("Hapus");

        refreshTableData();

        table = new JTable(tableModel);
        table.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JButton("Edit")));
        table.getColumn("Hapus").setCellRenderer(new ButtonRenderer("Hapus"));
        table.getColumn("Hapus").setCellEditor(new ButtonEditor(new JButton("Hapus")));
        
       
        table.removeColumn(table.getColumnModel().getColumn(1));

        JScrollPane pane = new JScrollPane(table);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pane);

        JButton createButton = new JButton("Create Mahasiswa");
        createButton.addActionListener(e -> new CreateMahasiswaFrame(MahasiswaFrame.this));
        panel.add(createButton);

        jFrame.getContentPane().add(panel, BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setBounds(100, 100, 600, 400);
    }

    public void refreshTableData() {
        tableModel.setRowCount(0);

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM mahasiswa");
             ResultSet rs = preparedStatement.executeQuery()) {

            int no = 1; 
            while (rs.next()) {
                int id = rs.getInt(1); 
                String nama = rs.getString(2);
                String nim = rs.getString(3);
                tableModel.addRow(new Object[] { no++, id, nama, nim, "Edit", "Hapus" });
            }

        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void deleteMahasiswaFromDatabase(int id) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM mahasiswa WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        private String buttonLabel;
        
        public ButtonRenderer(String buttonLabel) {
            setOpaque(true);
            this.buttonLabel = buttonLabel;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private final JButton button;
        private String label;
        private int selectedRow;

        public ButtonEditor(JButton button) {
            this.button = button;
            this.button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            this.button.setText(label);
            this.selectedRow = row;
            return this.button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("Edit".equals(label)) {
                int id = (int) tableModel.getValueAt(selectedRow, 1); 
                String nama = (String) tableModel.getValueAt(selectedRow, 2);
                String nim = (String) tableModel.getValueAt(selectedRow, 3);
                new UpdateMahasiswaFrame(MahasiswaFrame.this, id, nama, nim);
            } else if ("Hapus".equals(label)) {
                int id = (int) tableModel.getValueAt(selectedRow, 1); 
                deleteMahasiswaFromDatabase(id);
                refreshTableData();
            }
            fireEditingStopped();
        }
    }
}
