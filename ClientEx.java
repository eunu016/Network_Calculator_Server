import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientEx {
    private static final String DEFAULT_IP = "localhost";
    private static final int DEFAULT_PORT = 1234;
    private static final String SERVER_INFO_FILE = "server_info.dat";

    public static void main(String[] args) {

        String[] info = readServerInfo();
        String serverIp = info[0];
        int serverPort = Integer.parseInt(info[1]);

        try (
                Socket socket = new Socket(serverIp, serverPort);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("server connection is successful. enter the command \n");

            while (true) {
                System.out.print("> ");
                String userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("QUIT")) {
                    break;
                }

                serverOut.println(userInput);

                String serverResponse = serverIn.readLine();
                if (serverResponse == null) break;

                displayResponse(serverResponse);
            }
        } catch (IOException e) {
            System.err.println("connection error. check the server is running.");
        }
    }

    private static String[] readServerInfo() {
        try (BufferedReader br = new BufferedReader(new FileReader(SERVER_INFO_FILE))) {
            String ip = br.readLine();
            int port = Integer.parseInt(br.readLine());
            System.out.println("load server information from file: " + ip + ":" + port);
            return new String[]{ip, String.valueOf(port)};
        } catch (IOException | NumberFormatException e) {
            System.out.println(SERVER_INFO_FILE + " No file error. using default value: " + DEFAULT_IP + ":" + DEFAULT_PORT);
            return new String[]{DEFAULT_IP, String.valueOf(DEFAULT_PORT)};
        }
    }


    private static void displayResponse(String response) {
        String[] parts = response.split(Protocol.DELIMITER,3);
        if (parts.length < 3) {
            System.out.println("Invalid Server Response Format: " + response);
            return;
        }

        String code = parts[0];
        String type = parts[1];
        String value = parts[2];

        if (code.equals(Protocol.R_CODE_OK)) {
            System.out.printf("   [answer:%s] result: %s\n", type, value);
        } else if (code.equals(Protocol.R_CODE_CLIENT_ERROR)) {
            System.out.printf("   [error:%s] info: %s\n", type, value);
        } else {
            System.out.println("   [unknown code] " + response);
        }
    }
}