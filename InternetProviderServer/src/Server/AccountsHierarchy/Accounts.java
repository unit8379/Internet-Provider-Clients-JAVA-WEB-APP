package Server.AccountsHierarchy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "accounts") // если не укзать аргумент name, то имя элемента будет определено, исходя из имени класса
// @XmlType( propOrder = { "element1", "element3", "element2" } ) так можно задать порядок появления элементов в XML
public class Accounts {
    private List<Account> account;

    @XmlElement(name = "account")
    public void setAccount(List<Account> account) {
        this.account = account;
    }

    public List<Account> getAccount() {
        return account;
    }
}
