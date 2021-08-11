import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Jogo aberto na porta 1234");
        System.out.println("Esperando mensagem do cliente");
        Socket socket = serverSocket.accept();
        System.out.println("Cliente " + socket.getInetAddress().getHostAddress() + " conectado");

        List<String> listaPalavras = new ArrayList<>();

        BufferedReader leitorPalavras = new BufferedReader(new FileReader("src/palavras.txt"));

        String linhaTexto;

        while ((linhaTexto = leitorPalavras.readLine()) != null) {
            listaPalavras.add(linhaTexto);
        }

        leitorPalavras.close();

        Random random = new Random();

        String palavra = listaPalavras.get(random.nextInt(listaPalavras.size()));

        System.out.println("A palavra da partida e: " + palavra);

        int erros = 0;

        final int MAX_ERROS = 6;

        DataInputStream entrada = new DataInputStream(socket.getInputStream());

        DataOutputStream saida = new DataOutputStream(socket.getOutputStream());

        saida.writeInt(palavra.length());

        Set<String> letrasJaTentadas = new HashSet<>();

        while (erros < MAX_ERROS) {
            String letra = entrada.readUTF();

            System.out.println("Letra digitada pelo jogador agora: " + letra);

            if (letrasJaTentadas.contains(letra)) {
                saida.writeUTF(Evento.USADO.name());
                System.out.println("Jogador enviou uma letra ja usada");
                continue;
            }

            letrasJaTentadas.add(letra);

            if (!palavra.contains(letra)) {
                erros++;
                saida.writeUTF(Evento.INCORRETO.name());
                System.out.println("Jogador errou");
            } else {
                saida.writeUTF(Evento.CORRETO.name());
                saida.writeUTF(linhaResultado(palavra, letrasJaTentadas));
                System.out.println("Jogador acertou");
                saida.writeBoolean(ganhouPartida(palavra, letrasJaTentadas));
            }

            saida.writeUTF(letrasJaTentadas.toString());

            System.out.println("Jogador ja usou as letras: " + letrasJaTentadas);
            System.out.println("=====================");
        }

        saida.writeUTF(Evento.PERDEU.name());
        System.out.println("Jogador perdeu");

        entrada.close();
        saida.close();

        socket.close();
        serverSocket.close();
    }

    private static boolean ganhouPartida(String palavra, Set<String> letrasUsadas) {
        int acertos = 0;

        for (int i = 0; i < palavra.length(); i++) {
            if (letrasUsadas.contains(Character.toString(palavra.charAt(i)))) {
                acertos++;
            }
        }

        return palavra.length() == acertos;
    }

    private static String linhaResultado(String palavra, Set<String> letrasUsadas) {
        StringBuilder novoResultado = new StringBuilder();
        for (int i = 0; i < palavra.length(); i++) {
            if (letrasUsadas.contains(Character.toString(palavra.charAt(i)))) {
                novoResultado.append(palavra.charAt(i));
            } else {
                novoResultado.append("_");
            }
        }
        return novoResultado.toString();
    }
}