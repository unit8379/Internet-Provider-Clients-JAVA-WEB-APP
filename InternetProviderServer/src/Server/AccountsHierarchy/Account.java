package Server.AccountsHierarchy;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "account")
public class Account {
    private String accountNumber;
    private String firstName;
    private String lastName;
    private Tariff tariff;

    public Account() {}

    public Account(String accountNumber, String firstName, String lastName, Tariff tariff) {
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tariff = tariff;
    }

    @XmlAttribute(name = "accountNumber")
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @XmlAttribute(name = "firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    @XmlAttribute(name = "lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    @XmlElement(name = "tariff")
    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public Tariff getTariff() {
        return tariff;
    }
}