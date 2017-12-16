package com.zalo.hackathon.detector;

public class Entity {

    private String value;

    private EntityType type;

    public Entity(String value, EntityType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }
}
