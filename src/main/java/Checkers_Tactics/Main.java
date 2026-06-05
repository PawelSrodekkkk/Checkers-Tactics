package Checkers_Tactics;


import Checkers_Tactics.network.*;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Utworz grę");
        System.out.println("2. Dolacz do gry");
        System.out.print("Wybierz opcję: ");

        String choice = scanner.nextLine();

        GuestConnection guestConnection = null;
        HostConnection hostConnection = null;

        try {
            if ("1".equals(choice)) {
                hostConnection = startHost();
                testHostCommunication(hostConnection);
            } else if ("2".equals(choice)) {
                guestConnection = startGuest(scanner);
                testGuestCommunication(guestConnection);
            } else {
                System.out.println("Niepoprawny wybor");
            }
        } catch (IOException exception) {
            System.out.println("Bład połaczenia: " + exception.getMessage());
            return;
        }
        catch (ClassNotFoundException exception){
            System.out.println("Nie rozpoznano odebranego obiektu: " + exception.getMessage());
        }


    }

    private static HostConnection startHost() throws IOException {
        HostConnection hostConnection = new HostConnection(PORT);

        System.out.println("Oczekiwanie na drugiego gracza...");
        hostConnection.waitForGuest();

        System.out.println("Gracz dolaczyl");

        return hostConnection;
    }

    private static GuestConnection startGuest(Scanner scanner) throws IOException {
        System.out.print("Podaj adres IP gospodarza: ");
        String hostAddress = scanner.nextLine(); //narazie ip i tak nie ma znaczenia bo uzywamy local hosta wiec moze byc dowolna wartosc

        GuestConnection guestConnection = new GuestConnection("localhost", PORT);

        guestConnection.connect();

        System.out.println("Polaczono z gospodarzem");

        return guestConnection;
    }

    private static void testHostCommunication(HostConnection hostConnection) throws IOException, ClassNotFoundException{
        System.out.println("Host czeka na ruch...");

        NetworkMessage receivedMessage = hostConnection.receive();

        System.out.println("Host odebral wiadomosc: " + receivedMessage.getType());

        System.out.println("Host odebral tekst: " + receivedMessage.getText());

        System.out.println("Host odebral ruch: " + receivedMessage.getMove());

        NetworkMessage response = new NetworkMessage(MessageType.MOVE_APPLIED, receivedMessage.getMove(),"Host zaakceptowal testowy ruch");

        hostConnection.send(response);
    }

    private static void testGuestCommunication(GuestConnection guestConnection) throws IOException, ClassNotFoundException {

        Move move = new Move(5,0,4,1);

        NetworkMessage message = new NetworkMessage(MessageType.MOVE_REQUEST, move, "Testowa prosba o wykonanie ruchu");

        guestConnection.send(message);

        System.out.println("Guest wyslal ruch: " + move);

        NetworkMessage response = guestConnection.receive();

        System.out.println("Guest odebral odpowiedz: " + response.getType());

        System.out.println("Guest odebral tekst: " + response.getText());

        System.out.println("Guest odebral ruch: " + response.getMove());
    }
}