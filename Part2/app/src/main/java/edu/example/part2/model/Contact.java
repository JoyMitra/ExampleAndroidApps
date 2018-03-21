package edu.example.part2.model;

import java.util.List;

/**
 * Created by Joy on 3/20/18.
 * A data model for Phone Contacts
 */

public class Contact {

    private String id;
    private String name;
    private List<String> phoneNumbers;
    private List<String> emailid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmailid() {
        return emailid;
    }

    public void setEmailid(List<String> emailid) {
        this.emailid = emailid;
    }
}
