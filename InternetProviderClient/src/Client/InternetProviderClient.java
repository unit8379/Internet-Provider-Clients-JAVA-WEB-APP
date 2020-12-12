package Client;

import Server.InternetProvider;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class InternetProviderClient {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "InternetProviderClient\\src\\Client\\client.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        AppManager();
    }

    private static void AppManager() {
        System.out.println("Система для работы с базой данных клиентов интернет-провайдера.\n\nВведите номер требуемого действия над базой данных." +
                "\n1. Добавить информацию о клиенте в базу данных." + "\n2. Удалить клиента из базы данных" +
                "\n3. Создать в папке клиентской программы XML файл с информацией обо всех клиентах интернет-провайдера");

        String userInput = scanner.next();
        while (!tryParseInt(userInput)) {
            System.out.println("Введите корректное значение.");
            userInput = scanner.next();
        }

        switch (Integer.parseInt(userInput)) {
            case 1:
                insertNewAccountToDatabase();
                break;

            case 2:
                deleteAccountFromDatabase();
                break;

            case 3:
                getXmlFileOfAccounts();
                break;

            default:
                System.out.println("Выполнение программы окончено.");
        }
    }

    static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void insertNewAccountToDatabase() {
        System.out.println("Введите имя клиента:");
        String firstName = scanner.next();
        System.out.println("Введите фамилию клиента:");
        String secondName = scanner.next();
        System.out.println("Введите номер подключенного для клиента тарифа (1-9):");
        int tariffId = scanner.nextInt();

        String url = "127.0.0.1"; // localhost
        try {
            Registry registry = LocateRegistry.getRegistry(url);
            InternetProvider internetProvider = (InternetProvider) registry.lookup("actions");
            internetProvider.insertNewAccountToDatabase(firstName, secondName, tariffId);
        }
        catch (Exception e) {
            System.out.println("Client exception: ");
            e.printStackTrace();
        }
    }

    private static void deleteAccountFromDatabase() {
        System.out.println("Введите id клиента, которого нужно удалить из базы данных:");
        int accountId = scanner.nextInt();

        String url = "127.0.0.1"; // localhost
        try {
            Registry registry = LocateRegistry.getRegistry(url);
            InternetProvider internetProvider = (InternetProvider) registry.lookup("actions");
            internetProvider.deleteAccountFromDatabase(accountId);
        }
        catch (Exception e) {
            System.out.println("Client exception: ");
            e.printStackTrace();
        }
    }

    private static void getXmlFileOfAccounts() {
        String url = "127.0.0.1"; // localhost

        try {
            Registry registry = LocateRegistry.getRegistry(url);
            InternetProvider internetProvider = (InternetProvider) registry.lookup("actions");

            // Открытие потока на вывод и заполнение XML файла на стороне клиента пришедшими с сервера данными
            try(BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("InternetProviderClient\\src\\Client\\accounts.xml"))) {
                bufferedOutputStream.write(internetProvider.getXmlFileOfAccounts());
            }
        }
        catch (Exception e) {
            System.out.println("Client exception: ");
            e.printStackTrace();
        }
    }
}
