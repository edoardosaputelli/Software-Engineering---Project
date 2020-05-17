package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.utils.AsciiArt;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMain {

    private static SocketChannel normalChannel;
    private static SocketChannel errorChannel;
    public static final Scanner scannerIn = new Scanner(System.in);
    public static final ExecutorService clientExecutor = Executors.newCachedThreadPool();


    public static void main(String[] args) {

        //printa un messaggio di benvenuto
        printWelcome();

        try {

            //apre il canale di connessione standard
            openConnectionChannels();

            //gestisce la scelta tra gui e cli da parte dell'utente
            setTypeOfUserInterface();

            //fa partire la connessione standard e la macchina a stati che gestisce il gioco e la comunicazione standard
            initiateStandardCommunication();


            clientExecutor.shutdown();
            if (!clientExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) clientExecutor.shutdownNow();


            ClientViewAdapter.printMessage("Connessione chiusa con sucesso");


        }catch(ConnectException ex){

            ClientViewAdapter.printMessage("Non sono riuscito a connettermi al server per la connessione standard");
            System.exit(-1);

        } catch (IOException | InterruptedException e) {
            ClientViewAdapter.printMessage("Upsi, mi son disconnesso");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }



    }



    //metodo che printa il welcome al gioco
    private static void printWelcome() {

        System.out.println(ColorAnsi.YELLOW +"\n\n\n\n\n\n\n\nBenvenuto in:" +ColorAnsi.RESET);

        System.out.println("\n\n\n" + ColorAnsi.RED + AsciiArt.SANTORINI_4 +ColorAnsi.RESET);
        System.out.println(ColorAnsi.YELLOW +"A TRS production (excuse moi el dulce principe cranio creations)\n\n" +ColorAnsi.RESET);


    }
    //metodo che gestisce la scelta della user interface: gui o cli
    private static void setTypeOfUserInterface() {

        String guiOrCli = null;

        do {

            System.out.printf("%s%s%s", ColorAnsi.RED, "Preferisci gui o cli? g/c\n", ColorAnsi.RESET);
            guiOrCli = scannerIn.nextLine();
            guiOrCli = guiOrCli.toUpperCase();

        } while (!(guiOrCli.equals("G") || guiOrCli.equals("C")));

        MenuUserInterface menuUi;
        InGameUserInterface inGameUi;
        if (guiOrCli.equals("G")) {

            menuUi = new MenuCli();
            inGameUi = new InGameGui();
        }

        //l'utente ha scelto di usare la cli
        else {

            menuUi = new MenuCli();
            inGameUi = new InGameCli();

        }


        ClientViewAdapter.setTypeInterface(menuUi, inGameUi);

    }

    //metodo che apre i canali di connessione standard e per errori e ping
    private static void openConnectionChannels() throws IOException {

        normalChannel = SocketChannel.open();
        errorChannel = SocketChannel.open();
        normalChannel.configureBlocking(true);
        errorChannel.configureBlocking(true);


    }

    public static void closeConnectionChannels() throws IOException {

        normalChannel.close();
        errorChannel.close();

    }

    //fa partire la comunicazione standard del gioco
    private static void initiateStandardCommunication() throws IOException {

        if (normalChannel.connect(new InetSocketAddress("localhost", 6700))) {

            ClientFsm clientFsm = new ClientFsm(normalChannel.socket());
            clientFsm.run();

        }

    }

    public static SocketChannel getErrorChannel() {
        return errorChannel;

    }



}