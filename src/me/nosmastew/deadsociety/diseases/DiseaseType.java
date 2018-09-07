package me.nosmastew.deadsociety.diseases;

public enum DiseaseType {
    NECK,
    LEG,
    WATER,
    BLEEDING;

    DiseaseType() {

    }

    public String get() {
        return name().toUpperCase();
    }
}