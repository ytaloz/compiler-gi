/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.token.Token;
import compilador.token.TokenCategory;
import compilador.token.TokenType;

/**
 *
 * @author Gabriel
 */
public class Automato {

    char[] codigoFonte;
    char caracter;        //caractere atual lido
    int p = 0;        //indice do vetor de caracteres

    Estado estado = Estado.START;


    public Automato(String codigoFonte)
    {
        this.codigoFonte = codigoFonte.toCharArray();
        this.caracter = this.codigoFonte[p];
        p++;
    }

    public Token getToken() {
        while (estado != Estado.FIM) {
            switch (estado) {
                case START: {
                    estadoInicial();
                }
                case EM_ID: {
                }
                case EM_NUM: {
                }
            }
        }
        return new Token(TokenType.SUB, TokenCategory.OPERADOR, "-"); //temporário
    }

    private void estadoInicial() {
        if (ehLetra(caracter)) {
            estado = Estado.EM_ID;
        }
        if (ehDigito(caracter)) {
            estado = Estado.EM_NUM;
        }
    }





    //MÉTODOS AUXILIARES

    private boolean ehDigito(char c)
    {
        return Character.isDigit(c);
    }

    private boolean ehLetra(char c)
    {
        return Character.isLetter(c);
    }

    private boolean ehEspaco(char c)
    {
        return Character.isSpaceChar(c);
    }

    private boolean ehDelimitador(char c)
    {
        return ( c==';' || c=='(' || c==')' || c=='[' || c==']' || c=='{' || c=='}' );
    }

    private boolean ehOperador(char c)
    {
        return ( c=='+' || c=='-' || c=='*' || c=='/' || c=='=' || c=='!' || c=='>' || c=='<'|| c=='&'|| c=='|');
    }

}
