package com.example.parkingapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("Parking")
public class Parking {

    @Id
    @Column("id")
    private int id;

    @JsonFormat(pattern = "EEEE dd MMM yyyy hh:mm:ss")
    @Column("entranceTime")
    private LocalDateTime entranceTime = LocalDateTime.now();

    @Column("utenteId")
    private int utenteId;

    @Transient
    private Spot spot;

    @Transient
    private Utente utente;

    public Parking(int utenteId) {
        this.utenteId = utenteId;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getEntranceTime() {
        return entranceTime;
    }

    public Spot getSpot() {
        return spot;
    }


    public int getUtenteId() {
        return utenteId;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }


    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}