package net.artux.visualdz;

public class Tick {

    private String name;
    private float[] values;

    Tick(String name, float[] values){
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public float[] getValues() {
        return values;
    }
}
