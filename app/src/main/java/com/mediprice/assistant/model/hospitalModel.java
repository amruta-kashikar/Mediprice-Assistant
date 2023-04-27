package com.mediprice.assistant.model;

public class hospitalModel
{
    String name,area,phone,email,total,id,facility,price;
//    int vacant;

    public hospitalModel() {
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

//    public int getVacant() {
//        return vacant;
//    }
//
//    public void setVacant(int vacant) {
//        this.vacant = vacant;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacility() { return facility; }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
