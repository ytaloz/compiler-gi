/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.gui.Janela;
import compilador.token.Token;
import compilador.token.TokenType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public class AnalisadorLexico {

    Janela janela;

    List<Token> tokens = new ArrayList<Token>();

    public AnalisadorLexico()
    {
        janela = new Janela();
        janela.setVisible(true);
    }

    


}
