package com.example.hide.lasd5.lasd5.dict;

public enum LevelEnum {

    TOP("Entry"),
    HEAD("Head"),
    SENSE("Sense"),
    TAIL("Tail"),
    HWD("HWD"),
    DERIV("DERIV"),
    NONE("");

    private final String element;

    LevelEnum(String str) {
        element = str;
    }

    @Override
    public String toString() {
        return element;
    }

    public static LevelEnum get(String str) {
        for (LevelEnum e : LevelEnum.values()) {
            if (str.equals(e.element)) {
                return e;
            }
        }
        System.out.println("???? LevelEnum.get:" + str);
        return NONE;
    }

    public boolean equals(String str) {
        return str.equals(element);
    }
}
