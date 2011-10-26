
package compilador.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRenderer extends DefaultTableCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CellRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        this.setHorizontalAlignment(CENTER);

        Component component = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);

        if (column == 0) {
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setFont(component.getFont());
        }

        return component;
    }
}
