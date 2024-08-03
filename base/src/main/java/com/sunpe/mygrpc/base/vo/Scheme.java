package com.sunpe.mygrpc.base.vo;

public enum Scheme {
    ZK("zk", "zookeeper"),
    CONSUL("consul", "consul"),
    ;
    private final String code;
    private final String desc;

    private Scheme(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
