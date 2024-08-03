package com.sunpe.mygrpc.zookeeper;

public class Payload {
    private int weight;

    public Payload() {
    }

    public Payload(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
