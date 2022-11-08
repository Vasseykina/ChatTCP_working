package dopCasino;

import java.io.IOException;
import java.util.*;

public class CasinoThread extends Thread {

    public CasinoThread() {
        super();
        super.start();

    }


    public String winBets() {
        int max = 100;
        int rnd = (int) (Math.random() * ++max);
        String winBets = Integer.toString(rnd);
        return winBets;
    }

    @Override
    public void run() {
        Timer myTimer;
        myTimer = new Timer();

        myTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                completeTask();
            }
        }, 0, 10000);
    }

    private void completeTask() {
        String wb = winBets();
        ClientHandler clientHandlr = ClientHandler.clientHandlers.get(0);
        clientHandlr.broadcastMessage( " win!!!\n");
        for (HashMap.Entry<String, Integer> entry : ClientHandler.clientsBets.entrySet()) {
            if (entry.getValue() == Integer.parseInt(wb)) {
                ClientHandler clientHandler = ClientHandler.clientHandlers.get(0);
                clientHandler.broadcastMessage(entry.getKey() + " win!!!\n");
            }
            else{
                ClientHandler clientHandler = ClientHandler.clientHandlers.get(0);
                clientHandler.broadcastMessage("Nobody win!!!\n");
                clientHandler.broadcastMessage("Bets start\n");
            }
        }
        ClientHandler.clientsBets.clear();
    }

}
