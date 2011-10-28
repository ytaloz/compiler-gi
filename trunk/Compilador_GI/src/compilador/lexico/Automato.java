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
    String lexemaAtual = "" ;

    int ponteiro = -1;           //indice do vetor de caracteres (à frente)
    int linhaAtual = 1;              //linha atual da leitura

    Estado estado = Estado.START;
    Token tokenAtual;


    public Automato(String codigoFonte)
    {
        codigoFonte = codigoFonte + " ";
        this.codigoFonte = codigoFonte.toCharArray();
    }

    public Token getToken() {
        lexemaAtual = "";
        consumirProxCaracter();

        while (estado != Estado.FIM) {
     
            switch (estado) {
                case START: {
                    estadoInicial();
                    break;
                }
                case EM_ID: {
                    reconhecerIdentificador();
                    break;
                }
                case EM_NUM: {
                }
                default: {
                    criarTokenErro();
                }
                
            }
        }
        estado = Estado.START;
        return tokenAtual; 
    }

    private void estadoInicial() {
        if (ehLetra(caracter)) {
            estado = Estado.EM_ID;
            consumirProxCaracter();
        }
        if (ehDigito(caracter)) {
            estado = Estado.EM_NUM;
            consumirProxCaracter();
        }
        if (ehEspaco(caracter)) {
            consumirEspacos();
        }
        if (ehFinalDeArquivo()) {
            estado = Estado.FIM;
            criarTokenFinalDeArquivo();
        }
    }

    private void reconhecerIdentificador()
    {
        while( ehCaracterDeIdentificador(caracter) ) {
            consumirProxCaracter();
        }
        retrocederUmCaracter();
        tokenAtual = new Token(TokenType.ID, TokenCategory.IDENTIFICADOR, lexemaAtual, linhaAtual);
        estado = Estado.FIM;
    }





    //MÉTODOS AUXILIARES

    private void consumirProxCaracter() {
        if (!ehFinalDeArquivo()) {
            ponteiro++;
            this.caracter = this.codigoFonte[ponteiro];
            this.lexemaAtual = this.lexemaAtual + caracter;            
        }
    }

    private void retrocederUmCaracter() {
        if (!ehFinalDeArquivo()) {
            ponteiro--;
            this.caracter = this.codigoFonte[ponteiro];
            this.lexemaAtual = this.lexemaAtual.substring(0, (this.lexemaAtual.length() - 1));        
        }
    }

    private void consumirEspacos() {
        while (ehEspaco(caracter)) {
            ponteiro++;
            if (caracter == '\n') linhaAtual++;
            if (!ehFinalDeArquivo()) {
                this.caracter = this.codigoFonte[ponteiro];
            }
        }
        ponteiro--;
    }

    private boolean ehDigito(char c) {
        return Character.isDigit(c);
    }

    private boolean ehLetra(char c) {
        return Character.isLetter(c);
    }

    private boolean ehEspaco(char c) {
        return (Character.isSpaceChar(c) || c == '\r' || c == '\n');
        //return !ehLetra(c);
    }

    private boolean ehDelimitador(char c) {
        return (c == ';' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}');
    }

    private boolean ehOperador(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '!' || c == '>' || c == '<' || c == '&' || c == '|');
    }

    private boolean ehUnderline(char c) {
        return c == '_';
    }

    private boolean ehCaracterDeIdentificador(char c) {
        return (ehLetra(c) || ehDigito(c) || ehUnderline(c));
    }

    private boolean ehFinalDeArquivo() {
        return ponteiro >= codigoFonte.length-1;
    }

    private void criarTokenFinalDeArquivo() {
        this.tokenAtual = new Token(TokenType.EOF, TokenCategory.EOF, "", linhaAtual);
        estado = Estado.FIM;
    }

    private void criarTokenErro() {
        while(!ehEspaco(caracter)) {
            consumirProxCaracter();
        }
        retrocederUmCaracter();
        this.tokenAtual = new Token(TokenType.ERRO, TokenCategory.ERRO, lexemaAtual, linhaAtual);
        estado = Estado.FIM;
    }

}
