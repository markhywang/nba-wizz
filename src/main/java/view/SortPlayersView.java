package view;

import interface_adapter.sort_players.SortController;
import interface_adapter.sort_players.SortViewModel;
import interface_adapter.sort_players.SortState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class SortPlayersView extends JPanel implements PropertyChangeListener {

    public final String viewName = "sort_players";

    private final SortController controller;
    private final SortViewModel viewModel;

    private final JTable table;
    private final DefaultTableModel tableModel;


    private static final String[] COLUMN_NAMES = {
            "Name", "Pos", "Age", "Team", "Season",
            "G", "MP", "FG%", "3P%", "FT%",
            "TRB", "AST", "TOV", "STL", "BLK", "PF", "PTS"
    };


    public SortPlayersView(SortController controller, SortViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Filter & Sort");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Click column header to sort
        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint());
                controller.onColumnHeaderClicked(columnIndex);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SortState state = viewModel.getState();

        List<String[]> rows = state.getTableData();

        // Clear existing rows
        tableModel.setRowCount(0);

        if (rows != null) {
            for (String[] row : rows) {
                Object[] data = new Object[COLUMN_NAMES.length];
                for (int i = 0; i < COLUMN_NAMES.length; i++) {
                    if (row != null && i < row.length) {
                        data[i] = row[i];
                    } else {
                        data[i] = "";
                    }
                }
                tableModel.addRow(data);
            }
        }

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }
    }
}
