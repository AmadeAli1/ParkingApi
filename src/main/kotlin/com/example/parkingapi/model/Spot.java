package com.example.parkingapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Spot")
public class Spot {

    @Id
    @Column("number")
    private int number;

    @Column("isAvailable")
    private boolean isAvailable = true;

    @Column("utenteId")
    private Integer utenteId = null;

    @Transient
    private Status status;

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setUtenteId(Integer utenteId) {
        this.utenteId = utenteId;
    }

    public Integer getUtenteId() {
        return utenteId;
    }

    public int getNumber() {
        return number;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        ENTRY, EXIT
    }
}