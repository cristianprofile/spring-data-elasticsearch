package com.example.elastic;

public enum ColorEnum {


    RED("red"), GREEN("green"), BLUE("blue");

    private String description;

    ColorEnum(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }

}
