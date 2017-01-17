package org.eclipse.smarthome.core.auth;

public class User {
    private UsernamePasswordCredentials usernamePasswordCredentials;
    private String firstName;
    private String lastName;

    public User(UsernamePasswordCredentials usernamePasswordCredentials, String firstName, String lastName) {
        this.usernamePasswordCredentials = usernamePasswordCredentials;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UsernamePasswordCredentials getUsernamePasswordCredentials() {
        return usernamePasswordCredentials;
    }

    public void setUsernamePasswordCredentials(UsernamePasswordCredentials usernamePasswordCredentials) {
        this.usernamePasswordCredentials = usernamePasswordCredentials;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User " + this.usernamePasswordCredentials.getUsername() + " wurde mit dem Namen " + this.firstName + " "
                + this.lastName + " wurde angelegt.";
    }
}
