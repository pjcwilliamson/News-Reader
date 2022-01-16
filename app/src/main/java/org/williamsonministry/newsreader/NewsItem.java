package org.williamsonministry.newsreader;

public class NewsItem {
    private String title;
    private String description;
    private String link;
    private String date;
    private String coverImages;

    public NewsItem(String title, String description, String link, String date, String coverImages) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
        this.coverImages = coverImages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoverImages() {
        return coverImages;
    }

    public void setCoverImages(String coverImages) {
        this.coverImages = coverImages;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", date='" + date + '\'' +
                ", coverImages='" + coverImages + '\'' +
                '}';
    }
}
