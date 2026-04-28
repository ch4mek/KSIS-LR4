import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";
    private static final String BLACKLIST_FILE = "blacklist.txt";

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String bindAddress = DEFAULT_BIND_ADDRESS;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Некорректный номер порта: " + args[0] + ". Используется порт по умолчанию: " + port);
            }
        }

        if (args.length > 1) {
            bindAddress = args[1];
        }

        BlackList blackList = new BlackList(BLACKLIST_FILE);

        System.out.println("Прокси-сервер запущен на " + bindAddress + ":" + port);

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(bindAddress))) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Thread handlerThread = new Thread(new ClientHandler(clientSocket, blackList));
                    handlerThread.setDaemon(true);
                    handlerThread.start();
                } catch (IOException e) {
                    System.err.println("Ошибка при принятии соединения: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }
}
