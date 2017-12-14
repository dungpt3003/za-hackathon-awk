package com.zalo.hackathon.application;

import com.zalo.hackathon.controller.HackathonController;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class HackathonApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(HackathonController.class);
        return classes;
    }
}
