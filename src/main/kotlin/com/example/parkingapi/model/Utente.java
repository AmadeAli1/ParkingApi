package com.example.parkingapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("Utente")
@ToString
public class Utente {

    @Id
    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @Column
    @NotBlank
    private String nome;

    @Email
    @Column
    @NotBlank
    private String email;

    @Column
    @NotBlank
    private String password;

    @Column
    @NotNull
    private UtenteType type;

    @NotNull
    @Column("paymentType")
    private PaymentPlan paymentType;

    @Column("divida")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double divida = 0;

    @Column("lastSubscription")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "EEEE dd MMM yyyy hh:mm:ss")
    private LocalDateTime lastSubscription;

    public enum UtenteType {
        ESTUDANTE(0.5f),
        OUTROS(0.25f);

        private final float discount;

        UtenteType(float discount) {
            this.discount = discount;
        }
    }

    public enum PaymentPlan {
        Monthly {
            @Override
            public float discount(UtenteType type) {
                return type.discount;
            }
        },
        Daily {
            @Override
            public float discount(UtenteType type) {
                return type == UtenteType.ESTUDANTE ? type.discount : 0f;
            }
        };

        public abstract float discount(UtenteType type);
    }


    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UtenteType getType() {
        return type;
    }

    public void setType(UtenteType type) {
        this.type = type;
    }

    public PaymentPlan getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentPlan paymentType) {
        this.paymentType = paymentType;
    }

    public double getDivida() {
        return divida;
    }

    public LocalDateTime getLastSubscription() {
        return lastSubscription;
    }

    public void setLastSubscription(LocalDateTime lastSubscription) {
        this.lastSubscription = lastSubscription;
    }

    public void setDivida(double divida) {
        this.divida = divida;
    }


}
