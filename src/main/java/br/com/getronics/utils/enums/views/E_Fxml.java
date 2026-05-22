package br.com.getronics.utils.enums.views;

public enum E_Fxml {
    HOME,
    ABOUT,
    WRAPPER;

    public String getFilename() {
        return "/br/com/getronics/views/" + this.name().toLowerCase() + "View.fxml";
    }
}
