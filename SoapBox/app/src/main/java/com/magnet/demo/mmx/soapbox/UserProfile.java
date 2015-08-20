package com.magnet.demo.mmx.soapbox;

import java.util.Date;

public class UserProfile {
  private String mUsername = null;
  private Date mCreationDate = null;

  protected UserProfile() {}

  public UserProfile(String username, Date creationDate) {
    mUsername = username;
    mCreationDate = creationDate;
  }

  public final String getUsername() {
    return mUsername;
  }

  protected void setUsername(String username) {
    mUsername = username;
  }

  public final Date getCreationDate() {
    return mCreationDate;
  }

  protected final void setCreationDate(Date creationDate) {
    mCreationDate = creationDate;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof UserProfile) || ((UserProfile) o).getUsername() == null) {
      return false;
    }
    return ((UserProfile)o).getUsername().equals(mUsername);
  }
}
