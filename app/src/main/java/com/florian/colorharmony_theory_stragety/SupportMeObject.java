package com.florian.colorharmony_theory_stragety;

public class SupportMeObject {

    private String name;
    private int icon_resource;
    private double price;

    public SupportMeObject(String n, int i_r, double p){
        this.name = n;
        this.icon_resource = i_r;
        this.price = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon_resource() {
        return icon_resource;
    }

    public void setIcon_resource(int icon_resource) {
        this.icon_resource = icon_resource;
    }

    public double getPrice() {
        return price;
    }

    public String getStringPrice(){
        return ((int)(price)) + "€";
     }

    public void setPrice(int price) {
        this.price = price;
    }
}
