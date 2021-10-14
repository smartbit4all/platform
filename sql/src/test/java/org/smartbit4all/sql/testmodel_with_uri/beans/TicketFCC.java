package org.smartbit4all.sql.testmodel_with_uri.beans;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TicketFCC {

  public static final String FIELD_iD = "id";
  public static final String FIELD_URI = "uri";
  public static final String FIELD_TITLE = "title";
  public static final String FIELD_PARENT_TITLE = "parentTicketTitle";
  public static final String FIELD_SECONDARY_PERSON_NAME = "secondaryPersonName";

  private String id;

  private URI uri;

  private String title;

  private Person primaryPerson;

  private Person secondaryPerson;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Person getPrimaryPerson() {
    return primaryPerson;
  }

  public void setPrimaryPerson(Person primaryPerson) {
    this.primaryPerson = primaryPerson;
  }

  public Person getSecondaryPerson() {
    return secondaryPerson;
  }

  public void setSecondaryPerson(Person secondaryPerson) {
    this.secondaryPerson = secondaryPerson;
  }

  public static class Person {

    public static final String FIELD_iD = "id";
    public static final String FIELD_NAME = "name";

    // private String id;

    private String name;

    private List<Address> addresses;

    // public String getId() {
    // return id;
    // }
    //
    // public void setId(String id) {
    // this.id = id;
    // }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Address> getAddresses() {
      if (addresses == null) {
        addresses = new ArrayList<>();
      }
      return addresses;
    }

    public void setAddresses(List<Address> addresses) {
      this.addresses = addresses;
    }

  }

  public static class Address {

    // public static final String FIELD_ID = "id";
    public static final String FIELD_ZIP = "zip";
    public static final String FIELD_CITY = "city";

    // private String id;

    private String zip;

    private String city;

    // public String getId() {
    // return id;
    // }
    //
    // public void setId(String id) {
    // this.id = id;
    // }

    public String getZip() {
      return zip;
    }

    public void setZip(String zip) {
      this.zip = zip;
    }

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }

  }

}
