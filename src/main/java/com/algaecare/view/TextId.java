package com.algaecare.view;

public enum TextId {
    TITLE,
    SUBTITLE,
    NOT_AXOLOTL,
    AXOLOTL_INTRODUCTION;

    @Override
    public String toString() {
        return this.name();
    }
}