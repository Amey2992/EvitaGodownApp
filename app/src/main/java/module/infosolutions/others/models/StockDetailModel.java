package module.infosolutions.others.models;

/**
 * Created by R D Mishra on 15-07-2017.
 */

public class StockDetailModel {

    String title;
    String titleFull;
    String titleEmpty;

    public StockDetailModel(String title, String titleFull, String titleEmpty) {
        this.title = title;
        this.titleFull = titleFull;
        this.titleEmpty = titleEmpty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleFull() {
        return titleFull;
    }

    public void setTitleFull(String titleFull) {
        this.titleFull = titleFull;
    }

    public String getTitleEmpty() {
        return titleEmpty;
    }

    public void setTitleEmpty(String titleEmpty) {
        this.titleEmpty = titleEmpty;
    }

}
