import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.sql.SQLException;

public class BookstoreGUI extends JFrame {
    private JTextField tfId, tfName, tfAmount, tfPrice, tfSearch;
    private DefaultTableModel model;
    private DbTool db;

    public BookstoreGUI() {
        setTitle("Bookstore Management");
        setSize(760, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        try {
            db = new DbTool();
            refreshTable();
        } catch (Exception e) {
            showError("DB connect failed: " + e.getMessage());
        }
    }

    private void initUI() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfId = new JTextField(14);
        tfName = new JTextField(14);
        tfAmount = new JTextField(14);
        tfPrice = new JTextField(14);
        tfSearch = new JTextField(14);

        int y = 0;
        gbc.gridx=0; gbc.gridy=y; left.add(new JLabel("ID"), gbc);
        gbc.gridx=1; left.add(tfId, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; left.add(new JLabel("Name"), gbc);
        gbc.gridx=1; left.add(tfName, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; left.add(new JLabel("Amount"), gbc);
        gbc.gridx=1; left.add(tfAmount, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; left.add(new JLabel("Price"), gbc);
        gbc.gridx=1; left.add(tfPrice, gbc); y++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCreate = new JButton("Create");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        btnPanel.add(btnCreate); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; left.add(btnPanel, gbc); y++;
        gbc.gridwidth=1;

        gbc.gridx=0; gbc.gridy=y; left.add(new JLabel("Search"), gbc);
        gbc.gridx=1; left.add(tfSearch, gbc); y++;
        JButton btnFind = new JButton("Find");
        gbc.gridx=1; gbc.gridy=y; left.add(btnFind, gbc);

        String[] cols = {"ID","Name","Amount","Price"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, scroll);
        split.setDividerLocation(320);
        add(split, BorderLayout.CENTER);

        // Events
        btnCreate.addActionListener(e -> onCreate());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearFields());
        btnFind.addActionListener(e -> onFind());
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) fillFromTable(r);
            }
        });
    }

    private void onCreate() {
        try {
            String id = tfId.getText().trim();
            if (id.isEmpty()) { showError("ID is required"); return; }
            if (db.getBookById(id) != null) { showError("ID already exists"); return; }
            App b = new App(id, tfName.getText().trim(), parseInt(tfAmount.getText(),0), parseInt(tfPrice.getText(),0));
            if (db.insertBook(b)) { showInfo("Inserted"); refreshTable(); clearFields(); }
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onUpdate() {
        try {
            String id = tfId.getText().trim();
            if (id.isEmpty()) { showError("ID is required"); return; }
            if (db.getBookById(id) == null) { showError("ID not found"); return; }
            App b = new App(id, tfName.getText().trim(), parseInt(tfAmount.getText(),0), parseInt(tfPrice.getText(),0));
            if (db.updateBook(b)) { showInfo("Updated"); refreshTable(); }
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onDelete() {
        try {
            String id = tfId.getText().trim();
            if (id.isEmpty()) { showError("ID is required"); return; }
            App b = db.getBookById(id);
            if (b == null) { showError("ID not found"); return; }
            int r = JOptionPane.showConfirmDialog(this, "Delete " + b.getName() + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                if (db.deleteBook(id)) { showInfo("Deleted"); refreshTable(); clearFields(); }
            }
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void onFind() {
        try {
            String k = tfSearch.getText().trim();
            List<App> list = (k.isEmpty()) ? db.getAllBooks() : db.searchBooks(k);
            populate(list);
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void refreshTable() {
        try { populate(db.getAllBooks()); } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void populate(List<App> list) {
        model.setRowCount(0);
    for (App b : list) {
            model.addRow(new Object[]{b.getId(), b.getName(), b.getAmount(), b.getPrice()});
        }
    }

    private void fillFromTable(int r) {
        tfId.setText((String)model.getValueAt(r,0));
        tfName.setText((String)model.getValueAt(r,1));
        tfAmount.setText(String.valueOf(model.getValueAt(r,2)));
        tfPrice.setText(String.valueOf(model.getValueAt(r,3)));
    }

    private void clearFields() {
        tfId.setText(""); tfName.setText(""); tfAmount.setText(""); tfPrice.setText(""); tfSearch.setText("");
    }

    private int parseInt(String s, int def) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; } }
    private void showError(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private void showInfo(String m) { JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookstoreGUI().setVisible(true));
    }
}
