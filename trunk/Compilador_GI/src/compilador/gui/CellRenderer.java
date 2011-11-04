
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


        if (column == 1) {
            component.setForeground(Color.blue);
            component.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        } else {
            component.setForeground(Color.black);
        }

        if (value == "ERRO" || value == "erro") {
                component.setForeground(Color.RED);
        }

        if (value == "EOF" || value == "eof " || value == "Fim de Arquivo") {
                component.setForeground(Color.GRAY);
                component.setFont(component.getFont().deriveFont(Font.BOLD));
        }

        return component;
    }
}
