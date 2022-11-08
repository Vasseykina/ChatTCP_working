package dopCasino;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static dopCasino.ClientHandler.*;

public class ServerCasino {

    private ServerSocket serverSocket;
    private ServerCasino (ServerSocket serverSocket){
        this.serverSocket =serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){
        }
    }

    public void closeServer(){
        try{
            if(serverSocket!=null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(7877);
        ServerCasino server = new ServerCasino(serverSocket);
        server.startServer();
        CasinoThread casinoThread = new CasinoThread();
    }
}
