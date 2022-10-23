import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER:" + clientUsername + " has connected  the chat");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
//
//    public void sendCloseMsg(String messageToSend) {
//        if (messageToSend.startsWith("@sendUser")) {
//            String closeChatUser = messageToSend.substring(10);
//            try {
//                for (ClientHandler clientHandler : clientHandlers) {
//                    if (clientHandler.clientUsername.equals(closeChatUser)) {
//                        clientHandler.bufferedWriter.write(messageToSend);
//                        clientHandler.bufferedWriter.newLine();
//                        clientHandler.bufferedWriter.flush();
//                    }
//                }
//            }catch (IOException e){
//                closeEverything(socket, bufferedReader, bufferedWriter);
//            }
//        }
//    }


    public void broadcastMessage(String messageToSend) {
        if (messageToSend.startsWith("@sendUser")) {
            String closeChatUser = messageToSend.substring(10);
            try {
                for (ClientHandler clientHandler : clientHandlers) {
                    if (clientHandler.clientUsername.equals(closeChatUser)) {
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
        else {
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }


    public void removeClientHandlers() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER:" + clientUsername + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandlers();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
