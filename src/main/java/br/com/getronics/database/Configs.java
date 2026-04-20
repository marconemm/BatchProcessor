package br.com.getronics.database;

public class Configs {
    private String lastWorkBooksDir, lastOutPutDir, lastOutPutFile;

    public Configs() {
    }

    public String getLastWorkBooksDir() {
        if (lastWorkBooksDir == null)
            return System.getProperty("user.home");

        return lastWorkBooksDir;
    }

    public void setLastWorkBooksDir(String lastWorkBooksDir) {
        this.lastWorkBooksDir = lastWorkBooksDir;
    }

    public String getLastOutPutDir() {
        if (lastOutPutDir == null)
            return "<Selecionar Pasta>";

        return lastOutPutDir;
    }

    public void setLastOutPutDir(String lastOutPutDir) {
        this.lastOutPutDir = lastOutPutDir;
    }

    public String getLastOutPutFile() {
        if (lastOutPutFile == null){}
        return lastOutPutFile;
    }

    public void setLastOutPutFile(String lastOutPutFile) {
        this.lastOutPutFile = lastOutPutFile;
    }

    public Configs update(Configs configs) {
        setLastWorkBooksDir(configs.getLastWorkBooksDir());
        setLastOutPutDir(configs.getLastOutPutDir());

        return this;
    }
}
