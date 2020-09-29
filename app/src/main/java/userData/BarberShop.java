package userData;

import java.util.List;

public class BarberShop {
    private String name;
    private String area;
    private String city;
    private String address;
    private String phoneNumber;
    private List<String> images;

    public BarberShop() {} //for firebase database

    public BarberShop(String name, String area, String city, String address, String phoneNumber, List<String> images) {
        this.name = name;
        this.area = area;
        this.city = city;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
