package ru.alxstn.tastycoffeebulkpurchase.model.api.product;
import java.util.List;

public class User {
        private int id;
        private String name;
        private String phone;
        private String email;
        private String city;
        private String cityData;
        private int unreadArticles;
        private String exportDocs;
        private boolean coffeeMap;
        private int companiesCount;
        private String discountBlock;
        private List<String> hiddenNotifications;
        private boolean isAmbassador;
        private List<String> timezoneList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityData() {
        return cityData;
    }

    public void setCityData(String cityData) {
        this.cityData = cityData;
    }

    public int getUnreadArticles() {
        return unreadArticles;
    }

    public void setUnreadArticles(int unreadArticles) {
        this.unreadArticles = unreadArticles;
    }

    public String getExportDocs() {
        return exportDocs;
    }

    public void setExportDocs(String exportDocs) {
        this.exportDocs = exportDocs;
    }

    public boolean isCoffeeMap() {
        return coffeeMap;
    }

    public void setCoffeeMap(boolean coffeeMap) {
        this.coffeeMap = coffeeMap;
    }

    public int getCompaniesCount() {
        return companiesCount;
    }

    public void setCompaniesCount(int companiesCount) {
        this.companiesCount = companiesCount;
    }

    public String getDiscountBlock() {
        return discountBlock;
    }

    public void setDiscountBlock(String discountBlock) {
        this.discountBlock = discountBlock;
    }

    public List<String> getHiddenNotifications() {
        return hiddenNotifications;
    }

    public void setHiddenNotifications(List<String> hiddenNotifications) {
        this.hiddenNotifications = hiddenNotifications;
    }

    public boolean isAmbassador() {
        return isAmbassador;
    }

    public void setAmbassador(boolean ambassador) {
        isAmbassador = ambassador;
    }

    public List<String> getTimezoneList() {
        return timezoneList;
    }

    public void setTimezoneList(List<String> timezoneList) {
        this.timezoneList = timezoneList;
    }
}
