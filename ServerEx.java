import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerEx {
    private static final int nPort = 1234;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(nPort)) {
            System.out.println("Server start.. (port#=" + nPort + ")\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("-> new client connection: " + clientSocket.getInetAddress());

                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }


    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String clientCommand;
                while ((clientCommand = in.readLine()) != null) {
                    System.out.println("   [received] " + clientCommand);

                    String response = calculateExpression(clientCommand);

                    out.println(response);
                    System.out.println("   [send] " + response);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected..");
            } finally {
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        }


        private String calculateExpression(String commandLine) {
            String[] parts = commandLine.trim().split(Protocol.DELIMITER);

            if (parts.length != 3) {
                return Protocol.createResponse(Protocol.R_CODE_CLIENT_ERROR,
                        Protocol.R_TYPE_INVALID_ARGS,
                        "Incorrect command format");
            }

            String command = parts[0].toUpperCase();

            try {
                double operand1 = Double.parseDouble(parts[1]);
                double operand2 = Double.parseDouble(parts[2]);
                double result;

                switch (command) {
                    case Protocol.CMD_ADD: result = operand1 + operand2; break;
                    case Protocol.CMD_SUB: result = operand1 - operand2; break;
                    case Protocol.CMD_MUL: result = operand1 * operand2; break;
                    case Protocol.CMD_DIV:
                        if (operand2 == 0) {
                            return Protocol.createResponse(Protocol.R_CODE_CLIENT_ERROR,
                                    Protocol.R_TYPE_DIV_BY_ZERO,
                                    "Error: divided by zero");
                        }
                        result = operand1 / operand2; break;
                    default:
                        return Protocol.createResponse(Protocol.R_CODE_CLIENT_ERROR,
                                Protocol.R_TYPE_UNKNOWN_CMD,
                                "Command not supported: " + command);
                }

                return Protocol.createResponse(Protocol.R_CODE_OK, Protocol.R_TYPE_ANSWER, String.valueOf(result));

            } catch (NumberFormatException e) {
                return Protocol.createResponse(Protocol.R_CODE_CLIENT_ERROR,
                        Protocol.R_TYPE_INVALID_ARGS,
                        "Operands must be valid numbers.");
            }
        }
    }
}