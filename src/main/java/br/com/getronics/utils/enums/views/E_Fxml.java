package br.com.getronics.utils.enums.views;

public enum E_Fxml {
    SETTINGS,
    HOME,
    ABOUT,
    WRAPPER;

    public String getFilename() {
        return "/views/" + this.name().toLowerCase() + "View.fxml";
    }
}
