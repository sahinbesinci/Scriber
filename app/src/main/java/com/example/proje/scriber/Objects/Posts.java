package com.example.proje.scriber.Objects;

/**
 * Created by sahin on 9.05.2016.
 */
public class Posts {
    private String PostID;
    private String UyeID;
    private String Post;
    private String AdSoyad;
    public Posts(String postid,String uyeid,String post,String adsoyad)
    {
        this.PostID = postid;
        this.UyeID=uyeid;
        this.Post=post;
        this.AdSoyad = adsoyad;
    }

    public void setPostID(String postID){this.PostID = postID;}
    public String getPostID(){return PostID;}
    public void setUyeID(String uyeID){this.UyeID = uyeID;}
    public String getUyeID(){return UyeID;}
    public void setPost(String post){this.Post=post;}
    public String getPost(){return Post;}
    public void setAdSoyad(String adSoyad){this.AdSoyad = adSoyad;}
    public String getAdSoyad(){return AdSoyad;}

}
