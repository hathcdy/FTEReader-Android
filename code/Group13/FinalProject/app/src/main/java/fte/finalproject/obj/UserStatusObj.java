package fte.finalproject.obj;

public class UserStatusObj {
    int user_id;
    int hor_or_ver_screen;
    int day_or_night_status;
    int textSize;

    public UserStatusObj(int user_id, int hor_or_ver_screen, int day_or_night_status, int textSize) {
        this.user_id = user_id;
        this.hor_or_ver_screen = hor_or_ver_screen;
        this.day_or_night_status = day_or_night_status;
        this.textSize = textSize;
    }

    public int getDay_or_night_status() {
        return day_or_night_status;
    }

    public int getHor_or_ver_screen() {
        return hor_or_ver_screen;
    }

    public int getTextSize() {
        return textSize;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setDay_or_night_status(int dat_or_night_status) {
        this.day_or_night_status = dat_or_night_status;
    }

    public void setHor_or_ver_screen(int hor_or_ver_screen) {
        this.hor_or_ver_screen = hor_or_ver_screen;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

