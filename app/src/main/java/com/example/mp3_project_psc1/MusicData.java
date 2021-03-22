package com.example.mp3_project_psc1;

import java.util.Objects;

public class MusicData {

    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private String duration;
    private int PlayCount;
    private int liked;

    public MusicData(){

    }

    public MusicData(String id, String artist, String title, String albumArt, String duration, int playCount, int liked) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        PlayCount = playCount;
        this.liked = liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPlayCount() {
        return PlayCount;
    }

    public void setPlayCount(int playCount) {
        PlayCount = playCount;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicData musicData = (MusicData) o;
        return PlayCount == musicData.PlayCount &&
                liked == musicData.liked &&
                Objects.equals(id, musicData.id) &&
                Objects.equals(artist, musicData.artist) &&
                Objects.equals(title, musicData.title) &&
                Objects.equals(albumArt, musicData.albumArt) &&
                Objects.equals(duration, musicData.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artist, title, albumArt, duration, PlayCount, liked);
    }
}
