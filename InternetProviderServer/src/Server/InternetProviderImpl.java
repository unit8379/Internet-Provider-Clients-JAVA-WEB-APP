package Server;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Server.AccountsHierarchy.*;

public class InternetProviderImpl implements InternetProvider {
    private String fromName;

    public InternetProviderImpl(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return "This is the Message from " + fromName;
    }

    public void insertNewAccountToDatabase(String firstName, String secondName, int tariffId) {
        try{
            String url = "jdbc:mysql://127.0.0.1:3306/internet_provider_clients?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT";
            String username = "root";
            String password = "";

            // Зачем явно создавать инстанс драйвера? Без него и так всё работает
            // Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            try (Connection connection = DriverManager.getConnection(url, username, password)){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(account_id) FROM accounts");
                resultSet.next();
                int nextAccountId = resultSet.getInt(1) + 1;

                statement.executeUpdate("INSERT INTO accounts VALUES ("+nextAccountId+", '"+firstName+"', '"+secondName+"', "+tariffId+")");
                System.out.println("Запись добавлена!");
            }
        }
        catch(Exception e){
            System.out.println("Database connection failed...");
            e.printStackTrace();
        }
    }

    public void deleteAccountFromDatabase(int accountId) {
        try{
            String url = "jdbc:mysql://127.0.0.1:3306/internet_provider_clients?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT";
            String username = "root";
            String password = "";

            try (Connection connection = DriverManager.getConnection(url, username, password)){
                Statement statement = connection.createStatement();
                statement.executeUpdate("DELETE FROM accounts WHERE account_id = " + accountId);
                System.out.println("Запись удалена!");
            }
        }
        catch(Exception e){
            System.out.println("Database connection failed...");
            e.printStackTrace();
        }
    }

    public byte[] getXmlFileOfAccounts() {
        System.setProperty("javax.xml.accessExternalDTD", "all");  // свойство для разрешения доступа к сторонним DTD
        byte[] xmlFileByteArray = new byte[8]; // тут проинициализировал, чтобы компилятор не ругался. потом эта переменная примет массив с нужным кол-вом байт

        // ЗАПОЛНЕНИЕ Accounts ИЗ БАЗЫ ДАННЫХ
        Accounts accounts = FillAccountsHierarchyData();

        //System.out.println("Введите имя входного XML файла:");
        String inputFileName = makeCorrectXmlName("InternetProviderServer\\src\\Server\\accounts-new.xml");

        //System.out.println("Введите имя выходного XML файла:");
        String outputFileName = makeCorrectXmlName("InternetProviderServer\\src\\Server\\xml-to-transfer.xml");

        try {
//            // связывание класса с xml и чтение из него.
            JAXBContext jaxbContext = JAXBContext.newInstance(Accounts.class);
//            InputStream inputStream = new FileInputStream(inputFileName);
//            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//            Accounts accounts = (Accounts)unmarshaller.unmarshal(inputStream);
//            inputStream.close();

            // запись в новый xml
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  // формат под xml стиль
            File outFile = new File(outputFileName);
            outFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(outFile);
            marshaller.marshal(accounts, outputStream);
            outputStream.close();

            // запись XML файла в массив байт для передачи
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(outFile))) {
                xmlFileByteArray = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(xmlFileByteArray);
            }
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return xmlFileByteArray;
    }

    /**
     * Полное заполнение иерархии классов клиентов интернет-провайдера информацией из базы данных.
     * @return Экземпляр класса Accounts, который содержит список всех клиентов с заполненной информацией о них.
     */
    private Accounts FillAccountsHierarchyData() {
        ArrayList<Account> accounts;

        try{
            String url = "jdbc:mysql://127.0.0.1:3306/internet_provider_clients?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT";
            String username = "root";
            String password = "";

            try (Connection connection = DriverManager.getConnection(url, username, password)){
                // Для Резалт Сетов этого Statement'а задал свойство для скролла указателя вниз-вверх. По умолчанию курсор двигается только вниз.
                // Каждый стейтмент может держать открытым только один ResultSet, поэтому пришлось создать все пять для чтения БД.
                Statement statementForAccountsSet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                Statement statementForTariffSet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                Statement statementForInternetSet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                Statement statementForTelephoneSet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                Statement statementForTvSet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                ResultSet internet_serviceResultSet = statementForInternetSet.executeQuery("SELECT * FROM internet_service");
                ResultSet telephone_serviceResultSet = statementForTelephoneSet.executeQuery("SELECT * FROM telephone_service");
                ResultSet tv_serviceResultSet = statementForTvSet.executeQuery("SELECT * FROM tv_service");
                ResultSet tariff_serviceResultSet = statementForTariffSet.executeQuery("SELECT * FROM tariff");
                ResultSet accountsResultSet = statementForAccountsSet.executeQuery("SELECT * FROM accounts");

                // создаётся ArrayList размером с количество записей клиентов. Чтобы получить число записей используются манипуляции с курсором резалт сета
                accountsResultSet.last();
                int accountsNumber = accountsResultSet.getRow();
                accounts = new ArrayList<Account>(accountsNumber);
                accountsResultSet.beforeFirst(); // курсор обратно перед первой строкой

                // Полное заполнение иерархии классов с использованием сразу всех ResultSet'ов базы данных. В цикле заполняются все клиенты поочерёдно.
                int tariffId;
                int internetServiceId;
                int telephoneServiceId;
                int tvServiceId;
                while (accountsResultSet.next()) {
                    // Тут устанавливаются курсоры на нужную строку в таблицах тарифа и разлчных услуг, в соответствии с записью account
                    tariffId = accountsResultSet.getInt("tariff_id");
                    tariff_serviceResultSet.absolute(tariffId);
                    internetServiceId = tariff_serviceResultSet.getInt("internet_service_id");
                    internet_serviceResultSet.absolute(internetServiceId);
                    telephoneServiceId = tariff_serviceResultSet.getInt("telephone_service_id");
                    telephone_serviceResultSet.absolute(telephoneServiceId);
                    tvServiceId = tariff_serviceResultSet.getInt("tv_service_id");
                    tv_serviceResultSet.absolute(tvServiceId);

                    // добавление заполненного аккаунта в ArrayList
                    accounts.add(
                            new Account(accountsResultSet.getString("account_id"), accountsResultSet.getString("first_name"), accountsResultSet.getString("last_name"),
                                    new Tariff(tariff_serviceResultSet.getString("name"), tariff_serviceResultSet.getString("cost"), tariff_serviceResultSet.getString("tariff_id"),
                                            new InternetService(internet_serviceResultSet.getString("internet_service_id"), internet_serviceResultSet.getString("name"), internet_serviceResultSet.getString("speed")),
                                            new TelephoneService(telephone_serviceResultSet.getString("telephone_service_id"), telephone_serviceResultSet.getString("name"), telephone_serviceResultSet.getString("type")),
                                            new TvService(tv_serviceResultSet.getString("tv_service_id"), tv_serviceResultSet.getString("name"), tv_serviceResultSet.getString("type")))));
                }
                Accounts accountsInstanceToReturn = new Accounts();
                accountsInstanceToReturn.setAccount(accounts);
                return accountsInstanceToReturn;
            }
        }
        catch(Exception e){
            System.out.println("Database connection failed...");
            e.printStackTrace();
        }

        // пустой экземпляр акканутов, если было исключение
        return new Accounts();
    }

    private String makeCorrectXmlName(String inputString) {
        StringBuilder stringBuilder = new StringBuilder(inputString);
        if (!stringBuilder.toString().endsWith(".xml")) {
            stringBuilder.append(".xml");
        }
        return stringBuilder.toString();
    }
}
