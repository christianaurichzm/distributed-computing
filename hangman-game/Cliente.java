import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static final String ANSI_REDEFINIR = "\u001B[0m";
    public static final String ANSI_VERDE = "\u001B[32m";
    public static final String ANSI_VERMELHO = "\u001B[31m";
    static int tamanho;
    static String cabeca = "";
    static String bracoEsquerdo = "";
    static String bracoDireito = "";
    static String tronco = "";
    static String pernaEsquerda = "";
    static String pernaDireita = "";
    static String linhaResultado = "";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1234);

        DataOutputStream saida = new DataOutputStream(socket.getOutputStream());

        int erros = 0;

        final int MAX_ERROS = 6;

        DataInputStream entrada = new DataInputStream(socket.getInputStream());

        tamanho = entrada.readInt();

        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < tamanho; i++) {
            linhaResultado += "_";
        }

        while (erros < MAX_ERROS) {
            desenha(erros);
            System.out.println("Digite uma letra:");
            String letra = sc.nextLine();
            while (letra.length() > 1) {
                printFalha("Apenas uma letra e permitida!");
                System.out.println("Tente novamente:");
                letra = sc.nextLine();
            }
            while (!letra.matches("[A-Za-z]")) {
                printFalha("Apenas letras sao permitidas!");
                System.out.println("Tente novamente:");
                letra = sc.nextLine();
            }
            saida.writeUTF(letra);
            String resultado = entrada.readUTF();
            if (resultado.equals(Evento.INCORRETO.name())) {
                printFalha("Errou");
                erros++;
            } else if (resultado.equals(Evento.CORRETO.name())) {
                linhaResultado = entrada.readUTF();
                printSucesso("Acertou");
                if (entrada.readBoolean()) {
                    desenha(-1);
                    printSucesso("Ganhou");
                    break;
                }
            } else if (resultado.equals(Evento.USADO.name())) {
                System.out.println("Letra ja utilizada");
                System.out.println("=====================");
                continue;
            }
            System.out.println("Letras ja utilizadas: " + entrada.readUTF());
            System.out.println("=====================");
        }

        if (entrada.readUTF().equals(Evento.PERDEU.name())) {
            desenha(MAX_ERROS);
            printFalha("Perdeu");
        }

        sc.close();

        entrada.close();
        saida.close();

        socket.close();
    }

    private static void desenha(int erros) {
        switch (erros) {
            case 1:
                cabeca = "0";
                break;
            case 2:
                tronco = "|";
                break;
            case 3:
                bracoEsquerdo = "/";
                break;
            case 4:
                bracoDireito = "\\";
                break;
            case 5:
                pernaEsquerda = "/";
                break;
            case 6:
                pernaDireita = "\\";
                break;
            default: break;
        }

        boolean troncoPreenchido = !tronco.equals("");
        boolean bracoEsquerdoPreenchido = !bracoEsquerdo.equals("");
        boolean bracoDireitoPreenchido = !bracoDireito.equals("");

        String barra = " |";
        String barraComTronco = " |    ";
        String barraComBracos = barraComTronco.substring(0, barraComTronco.length() - 1);

        if (troncoPreenchido && !bracoEsquerdoPreenchido && !bracoDireitoPreenchido) {
            barra = barraComTronco;
        }

        if (troncoPreenchido && bracoEsquerdoPreenchido && !bracoDireitoPreenchido) {
            barra = barraComBracos;
        }

        if (troncoPreenchido && bracoEsquerdoPreenchido && bracoDireitoPreenchido) {
            barra = barraComBracos;
        }

        System.out.println(" +----+");
        System.out.println(" |    |");
        System.out.println(" |    " + cabeca);
        System.out.println(barra + bracoEsquerdo + tronco + bracoDireito);
        System.out.println(" |   " + pernaEsquerda + " " + pernaDireita);
        System.out.println(" |");
        System.out.println(" |");
        System.out.print("_+_ ");
        System.out.println(linhaResultado);
    }

    public static void printSucesso(String mensagem) {
        System.out.println(ANSI_VERDE + mensagem + ANSI_REDEFINIR);
    }

    public static void printFalha(String mensagem) {
        System.out.println(ANSI_VERMELHO + mensagem + ANSI_REDEFINIR);
    }
}
