package Server.AccountsHierarchy;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "tariff")
//@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL) // аннотация для изменения порядка атрибутов элемента в XML. имеет заранее заданные значения
public class Tariff {
    private String name;
    private String cost;
    private String id;
    private InternetService internetService;
    private TelephoneService telephoneService;
    private TvService tvService;

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "cost")
    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCost() {
        return cost;
    }

    @XmlAttribute(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @XmlElement(name = "internetService")
    public void setInternetService(InternetService internetService) {
        this.internetService = internetService;
    }

    public InternetService getInternetService() {
        return internetService;
    }

    @XmlElement(name = "telephoneService")
    public void setTelephoneService(TelephoneService telephoneService) {
        this.telephoneService = telephoneService;
    }

    public TelephoneService getTelephoneService() {
        return telephoneService;
    }

    @XmlElement(name = "tvService")
    public void setTvService(TvService tvService) {
        this.tvService = tvService;
    }

    public TvService getTvService() {
        return tvService;
    }
}
