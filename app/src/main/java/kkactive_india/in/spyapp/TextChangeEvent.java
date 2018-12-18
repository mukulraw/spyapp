package kkactive_india.in.spyapp;

public class TextChangeEvent {
    String text;
    boolean shouldToast = true;

    public TextChangeEvent(String text) {
        this.text = text;
    }

    public TextChangeEvent(String text,boolean shouldToast) {
        this.text = text;
        this.shouldToast = shouldToast;
    }

}
