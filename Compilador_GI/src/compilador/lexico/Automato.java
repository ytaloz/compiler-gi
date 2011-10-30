/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.TabelaDeSimbolos;
import compilador.token.Token;
import compilador.token.TokenCategory;
import compilador.token.TokenErro;
import compilador.token.TokenType;

/**
 *
 * @author Gabriel
 */
public class Automato {

    TabelaDeSimbolos simbolos;

    char[] codigoFonte;
    char caracter;                //caractere atual lido
    String lexemaAtual = "" ;

    int ponteiro = -1;           //indice do vetor de caracteres 
    int linhaAtual = 1;              //linha atual da leitura

    Estado estado = Estado.START;
    Token tokenAtual;


    public Automato(String codigoFonte, TabelaDeSimbolos simbolos)
    {
        codigoFonte = codigoFonte + " ";
        this.codigoFonte = codigoFonte.toCharArray();
        this.simbolos = simbolos;
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
                    reconhecerNumero();
                    break;
                }
                case EM_OPERADOR: {
                    reconhecerOperador();
                    break;
                }
                case EM_DELIMITADOR: {
                    reconhecerDelimitador();
                    break;
                }
                case EM_CADEIACONSTANTE: {
                    reconhecerCadeiaConstante();
                    break;
                }
                default: {
                    criarTokenErro("Simbolo Inválido: ");
                    break;
                }
                
            }
        }
        estado = Estado.START;
        return tokenAtual; 
    }

    private void estadoInicial() {
        if (ehLetra(caracter))
        {
            estado = Estado.EM_ID;
        } 
        else if (ehDigito(caracter))
        {
            estado = Estado.EM_NUM;
        } 
        else if (ehEspaco(caracter))
        {
            consumirEspacos();
        } 
        else if (ehFinalDeArquivo())
        {
            estado = Estado.FIM;
            criarTokenFinalDeArquivo();
        } 
        else if (ehOperador(caracter))
        {
            estado = Estado.EM_OPERADOR;
        } 
        else if (ehDelimitador(caracter))
        {
            estado = Estado.EM_DELIMITADOR;
        }
        else if (ehAspa(caracter))
        {
            estado = Estado.EM_CADEIACONSTANTE;
        }
        else {
            criarTokenErro("Simbolo inválido: ");
        }
    }

    private void reconhecerIdentificador()
    {
        while (ehCaracterDeIdentificador(caracter)) {
            consumirProxCaracter();
        }

        if (ehSimboloInvalido(caracter) || caracter=='!') {
            criarTokenErro("Identificador Mal Formado: ");
        } else {

            retrocederUmCaracter();
            if (simbolos.getSimbolo(lexemaAtual.trim()) != null) {
                tokenAtual = new Token(TokenType.PALAVRA_RESERVADA, TokenCategory.PALAVRA_RESERVADA, lexemaAtual.trim(), linhaAtual);
            } else {
                tokenAtual = new Token(TokenType.ID, TokenCategory.IDENTIFICADOR, lexemaAtual.trim(), linhaAtual);
            }
        }
        estado = Estado.FIM;
    }

    private void reconhecerNumero()
    {
        while (ehDigito(caracter)) {
            consumirProxCaracter();
        }

        if (ehSimboloInvalido(caracter) || ehLetra(caracter)) {
            criarTokenErro("Numero Mal Formado: ");
        } else {

            if (caracter == '.') {
                consumirProxCaracter();
                if (ehDigito(caracter)) {
                    while (ehDigito(caracter)) {
                        consumirProxCaracter();
                    }
                } else {
                    criarTokenErro("Numero Mal Formado: ");
                }
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.NUM, TokenCategory.NUMERO, lexemaAtual, linhaAtual);
                estado = Estado.FIM;
            }
            
        }
    }

    private void reconhecerOperador()
    {

        if(caracter=='+') {
            consumirProxCaracter();
            if(caracter=='+') {
                tokenAtual = new Token(TokenType.INCR, TokenCategory.OPERADOR, "++", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.ADICAO, TokenCategory.OPERADOR, "+", linhaAtual);
            }
        }
        if(caracter=='-') {
            consumirProxCaracter();
            if(caracter=='-') {
                tokenAtual = new Token(TokenType.DECR, TokenCategory.OPERADOR, "--", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.SUB, TokenCategory.OPERADOR, "-", linhaAtual);
            }
        }
        if(caracter=='*') {
            tokenAtual = new Token(TokenType.MULT, TokenCategory.OPERADOR, "*", linhaAtual);
        }
        if (caracter == '/') {
            consumirProxCaracter();
            if (caracter == '/') {
                consumirComentarioLinha();
                tokenAtual = new Token(TokenType.COMENTLINHA, TokenCategory.COMENTARIO, lexemaAtual, linhaAtual);
                linhaAtual++;
            } else if (caracter == '*') {
                consumirComentarioBloco();
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.DIV, TokenCategory.OPERADOR, "/", linhaAtual);
            }
        }
        if(caracter=='=') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.IGUAL, TokenCategory.OPERADOR, "==", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.ATRIB, TokenCategory.OPERADOR, "=", linhaAtual);
            }
        }
        if(caracter=='!') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.DIF, TokenCategory.OPERADOR, "!=", linhaAtual);
            } else {
                criarTokenErro("Simbolo Inválido: ");
            }
        }
        if(caracter=='>') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.MAIORIGUAL, TokenCategory.OPERADOR, ">=", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.MAIOR, TokenCategory.OPERADOR, ">", linhaAtual);
            }
        }
        if(caracter=='<') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.MENORIGUAL, TokenCategory.OPERADOR, "<=", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.MENOR, TokenCategory.OPERADOR, "<", linhaAtual);
            }
        }
        if(caracter=='&') {
            consumirProxCaracter();
            if(caracter=='&') {
                tokenAtual = new Token(TokenType.E, TokenCategory.OPERADOR, "&&", linhaAtual);
            } else {
                criarTokenErro("Operador Mal Formado - esperava outro '&': ");
            }
        }
        if(caracter=='|') {
            consumirProxCaracter();
            if(caracter=='|') {
                tokenAtual = new Token(TokenType.OU, TokenCategory.OPERADOR, "||", linhaAtual);
            } else {
                criarTokenErro("Operador Mal Formado - esperava outro '|': ");
            }
        }
        if(caracter=='.') {
            tokenAtual = new Token(TokenType.PONTO, TokenCategory.OPERADOR, ".", linhaAtual);
        }

        estado = Estado.FIM;
    }

    private void reconhecerDelimitador() {
        if (caracter == ';') {
            tokenAtual = new Token(TokenType.PONTOVIRGULA, TokenCategory.DELIMITADOR, ";", linhaAtual);
        }
        if (caracter == '(') {
            tokenAtual = new Token(TokenType.ABREPAR, TokenCategory.DELIMITADOR, "(", linhaAtual);
        }
        if (caracter == ')') {
            tokenAtual = new Token(TokenType.FECHAPAR, TokenCategory.DELIMITADOR, ")", linhaAtual);
        }
        if (caracter == '{') {
            tokenAtual = new Token(TokenType.ABRECHAVE, TokenCategory.DELIMITADOR, "{", linhaAtual);
        }
        if (caracter == '}') {
            tokenAtual = new Token(TokenType.FECHACHAVE, TokenCategory.DELIMITADOR, "}", linhaAtual);
        }
        if (caracter == '[') {
            tokenAtual = new Token(TokenType.ABRECOLCH, TokenCategory.DELIMITADOR, "[", linhaAtual);
        }
        if (caracter == ']') {
            tokenAtual = new Token(TokenType.FECHACOLCH, TokenCategory.DELIMITADOR, "]", linhaAtual);
        }

        estado = Estado.FIM;
    }

    private void reconhecerCadeiaConstante() {
        consumirCadeiaConstante();
        if (ehAspa(caracter)) {
            tokenAtual = new Token(TokenType.LITERAL, TokenCategory.CADEIA_CONSTANTE, lexemaAtual, linhaAtual);
        } else {
            criarTokenErro("String Não Fechada - esperava um '\"': ");
        }
        estado = Estado.FIM;
        
    }



    //MÉTODOS AUXILIARES

    private void consumirProxCaracter() {
        if (ehFinalDeArquivo()) {
            criarTokenFinalDeArquivo();
        } else {
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
            } else {
                criarTokenFinalDeArquivo();
                break;
            }
        }
        lexemaAtual = lexemaAtual + caracter;
    }

    private void consumirComentarioLinha() {
        while (caracter != '\n' && !ehFinalDeArquivo()) {
            consumirProxCaracter();
        }
    }

    private void consumirComentarioBloco() {
        while (caracter != '*' && !ehFinalDeArquivo()) {
            if(caracter == '\n') linhaAtual++;
            consumirProxCaracter();
        }
        if(caracter == '*') {
            consumirProxCaracter();
            if(caracter == '/') {
                tokenAtual = new Token(TokenType.COMENTBLOCO, TokenCategory.COMENTARIO, lexemaAtual, linhaAtual);
            } else if(caracter != '*') {
                consumirComentarioBloco();
            }
        } else {
            criarTokenErro("Fim Inesperado de Arquivo - necessário fechar o comentário de bloco: ");
        }
    }

    private void consumirCadeiaConstante() {
        consumirProxCaracter();
        while (caracter != '\n' && !ehFinalDeArquivo() && !ehAspa(caracter)) {
            consumirProxCaracter();
        }
        if(caracter == '\n') linhaAtual++;
    }

    private boolean ehDigito(char c) {
        return Character.isDigit(c);
    }

    private boolean ehLetra(char c) {
        return Character.isLetter(c);
    }

    private boolean ehEspaco(char c) {
        return (Character.isSpaceChar(c) || c == '\r' || c == '\n');
    }

    private boolean ehDelimitador(char c) {
        return (c == ';' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}');
    }

    private boolean ehOperador(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '!' || c == '>' || c == '<' || c == '&' || c == '|' || c == '.');
    }

    private boolean ehUnderline(char c) {
        return c == '_';
    }

    private boolean ehAspa(char c) {
        return c=='\"';
    }
    
    private boolean ehCaracterDeIdentificador(char c) {
        return (ehLetra(c) || ehDigito(c) || ehUnderline(c));
    }

    private boolean ehSimboloInvalido(char c) {
        return (!ehLetra(c) && !ehDigito(c) && !ehEspaco(c) && !ehDelimitador(c) && !ehOperador(c) && !ehUnderline(c) && !ehAspa(c));
    }

//    private boolean ehSimboloInvalido(char c) {
//        int ASCII = (int) c;
//        return ( ASCII>=32 && ASCII<=126 && ASCII!=34);
//    }

    private boolean ehFinalDeArquivo() {
        return ponteiro >= codigoFonte.length-1;
    }

    private void criarTokenFinalDeArquivo() {
        this.tokenAtual = new Token(TokenType.EOF, TokenCategory.EOF, "Fim de Arquivo", linhaAtual);
        estado = Estado.FIM;
    }

    private void criarTokenErro(String mensagem) {
        while(!ehEspaco(caracter) && !ehDelimitador(caracter) && !ehOperador(caracter)) {
            if(caracter == '\n') linhaAtual++;
            consumirProxCaracter();
        }
        retrocederUmCaracter();
        this.tokenAtual = new TokenErro(lexemaAtual, linhaAtual, "#" + mensagem + "\"" + lexemaAtual.trim() + "\"", ponteiro);
        estado = Estado.FIM;
    }

}
