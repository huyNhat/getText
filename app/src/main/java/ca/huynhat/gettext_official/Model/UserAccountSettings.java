package ca.huynhat.gettext_official.Model;

/**
 * Created by huynhat on 2018-03-12.
 */

public class UserAccountSettings {
    private String display_name;
    private String school;
    private String major;
    private long posts;
    private String username;
    private String profile_photo;

    public UserAccountSettings(){

    }

    public UserAccountSettings(String display_name, String school, String major,
                               long posts, String username, String profile_photo) {
        this.display_name = display_name;
        this.school = school;
        this.major = major;
        this.posts = posts;
        this.username = username;
        this.profile_photo = profile_photo;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "display_name='" + display_name + '\'' +
                ", school='" + school + '\'' +
                ", major='" + major + '\'' +
                ", posts=" + posts +
                ", username='" + username + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                '}';
    }
}
