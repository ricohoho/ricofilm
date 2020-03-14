package ricohoho.themoviedb;

import java.util.Date;

public class Film {
	/*
	"vote_count": 8125,
	"id": 603,
	"video": false,
	"vote_average": 7.9,
	"title": "The Matrix",
	"popularity": 9.216916,
	"poster_path": "/hEpWvX6Bp79eLxY1kX5ZZJcme5U.jpg",
	"original_language": "en",
	"original_title": "The Matrix",
	"genre_ids": [
	28,
	878
	],
	"backdrop_path": "/7u3pxc0K1wx32IleAkLv78MKgrw.jpg",
	"adult": false,
	"overview": "Set in the 22nd century, The Matrix tells the story of a computer hacker who joins a group of underground insurgents fighting the vast and powerful computers who now rule the earth.",
	"release_date": "1999-03-30"
	*/	
	
	int vote_count;
	long id;
	boolean video;
	double vote_average;
	String title;
	double popularity;
	String poster_path;
	String original_language;
	String original_title;
	//genre_ids
	String backdrop_path;
	boolean adult;
	String release_date;	
	
	
	public int getVote_count() {
		return vote_count;
	}
	public void setVote_count(int vote_count) {
		this.vote_count = vote_count;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isVideo() {
		return video;
	}
	public void setVideo(boolean video) {
		this.video = video;
	}
	public double getVote_average() {
		return vote_average;
	}
	public void setVote_average(double vote_average) {
		this.vote_average = vote_average;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPopularity() {
		return popularity;
	}
	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}
	public String getPoster_path() {
		return poster_path;
	}
	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}
	public String getOriginal_language() {
		return original_language;
	}
	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}
	public String getOriginal_title() {
		return original_title;
	}
	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}
	public String getBackdrop_path() {
		return backdrop_path;
	}
	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}
	public boolean isAdult() {
		return adult;
	}
	public void setAdult(boolean adult) {
		this.adult = adult;
	}
	public String getRelease_date() {
		return release_date;
	}
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}

	
}
