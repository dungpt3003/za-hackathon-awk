package com.zalo.hackathon.conversation;

public class Entity {
    private String value;

    private EntityType type;

    public Entity(String value, EntityType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public EntityType getType() {
        return type;
    }

    @Override
    public String toString() {
        return " [" + value + " - " + type + "]";
    }
}
