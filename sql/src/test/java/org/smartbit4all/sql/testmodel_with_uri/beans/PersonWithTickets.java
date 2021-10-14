package org.smartbit4all.sql.testmodel_with_uri.beans;

import java.net.URI;
import java.util.List;

public class PersonWithTickets {

  public static final String FIELD_UID = "uid";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_TICKETS = "tickets";

  private String uid;

  private URI uri;

  private String name;

  private List<TicketOfPerson> tickets;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TicketOfPerson> getTickets() {
    return tickets;
  }

  public void setTickets(List<TicketOfPerson> tickets) {
    this.tickets = tickets;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }


  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }


  public static class TicketOfPerson {

    public static final String FIELD_UID = "uid";
    public static final String FIELD_URI = "uri";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_PARENT_TITLE = "parentTicketTitle";
    public static final String FIELD_SECONDARY_PERSON_NAME = "secondaryPersonName";

    private String uid;

    private URI uri;

    private String title;

    private String parentTicketTitle;

    private String secondaryPersonName;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getParentTicketTitle() {
      return parentTicketTitle;
    }

    public void setParentTicketTitle(String parentTicketTitle) {
      this.parentTicketTitle = parentTicketTitle;
    }

    public String getSecondaryPersonName() {
      return secondaryPersonName;
    }

    public void setSecondaryPersonName(String secondaryPersonName) {
      this.secondaryPersonName = secondaryPersonName;
    }

    public URI getUri() {
      return uri;
    }

    public void setUri(URI uri) {
      this.uri = uri;
    }

    public String getUid() {
      return uid;
    }

    public void setUid(String uid) {
      this.uid = uid;
    }

  }

}
