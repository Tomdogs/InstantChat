package com.rance.chatui.enity;

/**
 * Created by Tomdog on 2017/5/30.
 */

public class News {

    /**
     * url_3w
     * ltitle
     * digest
     * imgsrc
     * ptime
     * source
     */

    private String url3w;
    private String ltitle;
    private String digest;
    private String imgsrc;
    private String ptime;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl3w() {
        return url3w;
    }

    public void setUrl3w(String url3w) {
        this.url3w = url3w;
    }

    public String getLtitle() {
        return ltitle;
    }

    public void setLtitle(String ltitle) {
        this.ltitle = ltitle;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    @Override
    public String toString() {
        return "News{" +
                "url3w='" + url3w + '\'' +
                ", ltitle='" + ltitle + '\'' +
                ", digest='" + digest + '\'' +
                ", imgsrc='" + imgsrc + '\'' +
                ", ptime='" + ptime + '\'' +
                '}';
    }
}
