package com.example.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;

@Document(indexName = "person-index", type = "person")
public class Person {


    @Id
    private Long id;

    private String name;

    private String surname;

    private String address;

    private LocalDate birthDate;

    private Integer favoriteNumber;


    private ColorEnum favoriteColor;

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getFavoriteNumber() {
        return favoriteNumber;
    }

    public void setFavoriteNumber(Integer favoriteNumber) {
        this.favoriteNumber = favoriteNumber;
    }

    public ColorEnum getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(ColorEnum favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    public Long getId() {

        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("address='").append(address).append('\'');
        sb.append(", birthDate=").append(birthDate);
        sb.append(", favoriteColor=").append(favoriteColor);
        sb.append(", favoriteNumber=").append(favoriteNumber);
        sb.append(", id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", surname='").append(surname).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
