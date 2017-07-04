package ytx.twittersearch.search;

import java.util.Date;

public class TweetRecord {
	String name;
	String screenName;
	String creatT;
	String snippet;
	String text;
	long favorite;
	long retweet;
	String profileImageURL;
	String mediaURL;
	
	public TweetRecord(){
		name="";
		screenName="";
		creatT="";
		snippet="";
		text="";
		favorite=0;
		retweet=0;
		profileImageURL="";
		mediaURL="";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getCreatT() {
		return creatT;
	}

	public void setCreatT(String creatT) {
		this.creatT = creatT;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getFavorite() {
		return favorite;
	}

	public void setFavorite(long favorite) {
		this.favorite = favorite;
	}

	public long getRetweet() {
		return retweet;
	}

	public void setRetweet(long retweet) {
		this.retweet = retweet;
	}

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public void setProfileImageURL(String profileImageURL) {
		char delete='\\';
		profileImageURL=profileImageURL.replace(""+delete, "");
		this.profileImageURL = profileImageURL;
	}

	public String getMediaURL() {
		return mediaURL;
	}

	public void setMediaURL(String mediaURL) {
		char delete='\\';
		mediaURL=mediaURL.replace(""+delete, "");
		this.mediaURL = mediaURL;
	}
	
	
}
