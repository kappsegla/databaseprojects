package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class Event {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)   //https://manbunder.medium.com/streamline-uuid-v7-generation-in-spring-boot-entities-with-custom-annotations-hibernate-6-5-4ddc018895cf
    private java.util.UUID id;

    private String info;

    public String getInfo() {
        return info;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
