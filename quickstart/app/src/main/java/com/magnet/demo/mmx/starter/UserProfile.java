package com.magnet.demo.mmx.starter;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfile {
  private String mUsername = null;
  private String mDisplayName = null;
  private Date mCreationDate = null;

  protected UserProfile() {}

  public UserProfile(String username, String displayName, Date creationDate) {
    mUsername = username;
    mDisplayName = displayName;
    mCreationDate = creationDate;
  }

  public final String getUsername() {
    return mUsername;
  }

  protected void setUsername(String username) {
    mUsername = username;
  }

  public final String getDisplayName() { return mDisplayName; }

  protected void setDisplayName(String displayName) { mDisplayName = displayName; }

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
