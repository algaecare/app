package com.algaecare.view;

public enum TextId {
    TITLE,
    SUBTITLE,
    NOT_AXOLOTL,
    AXOLOTL_INTRODUCTION,
    NOT_OBJECT,
    OBJECT_GARBAGE_BAG,
    OBJECT_RECYCLING_BIN,
    OBJECT_CAR,
    OBJECT_AIRPLANE,
    OBJECT_TRAIN,
    OBJECT_BICYCLE,
    OBJECT_SHOPPING_BASKET_INTERNATIONAL,
    OBJECT_SHOPPING_BASKET_LOCAL,
    ENDSCREEN_NEGATIVE,
    ENDSCREEN_POSITIVE,
    GOODBYE;



    @Override
    public String toString() {
        return this.name();
    }
}