package com.example.passwordmanager;

public class AccountsModel {

    String email, password, socialMediaName, accountPageUrl;
            int imageUrl;

    public AccountsModel() {
    }

    public AccountsModel(String email, String password, String socialMediaName, String accountPageUrl, int imageUrl) {
        this.email = email;
        this.password = password;
        this.socialMediaName = socialMediaName;
        this.accountPageUrl = accountPageUrl;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSocialMediaName() {
        return socialMediaName;
    }

    public void setSocialMediaName(String socialMediaName) {
        this.socialMediaName = socialMediaName;
    }

    public String getAccountPageUrl() {
        return accountPageUrl;
    }

    public void setAccountPageUrl(String accountPageUrl) {
        this.accountPageUrl = accountPageUrl;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
