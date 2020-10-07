package userData;

public class User {
    String fullName;
    String password;
    String email;
    String profilePicture;
    String gender;
    String birthday;
    String address;

    public User(String fullName, String password, String email, String profilePicture, String gender, String birthday, String address) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.gender = gender;
        this.birthday = birthday;
        this.address = address;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
