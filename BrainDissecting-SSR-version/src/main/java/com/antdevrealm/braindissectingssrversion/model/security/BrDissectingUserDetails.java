package com.antdevrealm.braindissectingssrversion.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class BrDissectingUserDetails extends User {

    private String firstName;

    private String lastName;

    public BrDissectingUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String firstName,
            String lastName) {

        super(username, password, authorities);

        this.firstName=firstName;
        this.lastName=lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public BrDissectingUserDetails setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public BrDissectingUserDetails setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();

        if(firstName != null) {
            fullName.append(firstName);
        }

        if(lastName != null) {

            if(!fullName.isEmpty()) {
                fullName.append(" ");
            }
            fullName.append(lastName);
        }

        if(fullName.isEmpty()) {
            fullName.append("User hasn't entered his full name");
        }

        return fullName.toString();
    }
}
