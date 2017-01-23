package com.warmtel.expandtab;

public class KeyValueBean {
    private String key;
    private String value;
    private String id;
    private String num;


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public KeyValueBean(String key, String value,String id,String num) {
        super();
        this.key = key;
        this.value = value;
        this.id = id;
        this.num = num;
    }
    public KeyValueBean(String key, String value,String id) {
        super();
        this.key = key;
        this.value = value;
        this.id = id;
    }
    public KeyValueBean() {
        super();
    }


}
