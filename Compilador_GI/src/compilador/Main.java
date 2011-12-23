/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import compilador.lexico.AnalisadorLexico;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Gabriel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//
//                 try {
//           UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
//        } catch (UnsupportedLookAndFeelException ex) {
//            ex.printStackTrace();
//        }

        Compilador compilador = new Compilador();
    }

}
