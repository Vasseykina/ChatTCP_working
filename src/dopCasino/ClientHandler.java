package dopCasino;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static HashMap<String, Integer> clientsBets = new HashMap<>();
    public static String winBets ;

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

    public String winBets(){
        int max = 100;
        int rnd =  (int) (Math.random() * ++max);
        winBets = Integer.toString(rnd);
        return winBets;
    }

//    public void winner(String clientsBets){
//        if(clientsBets.contains(winBets())){
//
//        }
//    }

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

    public void broadcastMessage(String messageToSend) {
        if (messageToSend.contains("@betNumber")) {
            String bet = messageToSend.split("@betNumber")[1].split("\\s")[1];
                Pattern pattern1 = Pattern.compile("[0-9]");
                Matcher matcher1 = pattern1.matcher(bet);
                Pattern pattern2 = Pattern.compile("[^0-9]");
                Matcher matcher2 = pattern2.matcher(bet);
                if (matcher1.find()) {
                    if (!matcher2.find()) {
                        String needless = messageToSend.substring(clientUsername.length() + bet.length() + 12);
                        if (needless.isEmpty()) {
                            clientsBets.put(clientUsername ,Integer.parseInt(bet));
                            for (ClientHandler clientHandler : clientHandlers) {
                                try {
                                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                                        clientHandler.bufferedWriter.write(clientUsername + ": bet - " + bet);
                                        clientHandler.bufferedWriter.newLine();
                                        clientHandler.bufferedWriter.flush();
                                    }
                                } catch (IOException e) {
                                    closeEverything(socket, bufferedReader, bufferedWriter);
                                }
                            }
                        }
                    }
                }
        } else {
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