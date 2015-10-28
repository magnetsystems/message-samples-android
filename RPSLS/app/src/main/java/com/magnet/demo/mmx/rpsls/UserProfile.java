package com.magnet.demo.mmx.rpsls;

import com.magnet.android.User;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfile {
  public static class Stats {
    public final Map<RPSLS.Outcome, Integer> mOutcomeCounts;
    public final Map<RPSLS.Choice, Integer> mChoiceCounts;

    Stats(int wins, int losses, int draws, int rock, int paper, int scissors, int lizard, int spock) {
      mChoiceCounts = new HashMap<RPSLS.Choice, Integer>();
      mChoiceCounts.put(RPSLS.Choice.ROCK, rock);
      mChoiceCounts.put(RPSLS.Choice.PAPER, paper);
      mChoiceCounts.put(RPSLS.Choice.SCISSORS, scissors);
      mChoiceCounts.put(RPSLS.Choice.LIZARD, lizard);
      mChoiceCounts.put(RPSLS.Choice.SPOCK, spock);

      mOutcomeCounts = new HashMap<RPSLS.Outcome, Integer>();
      mOutcomeCounts.put(RPSLS.Outcome.WIN, wins);
      mOutcomeCounts.put(RPSLS.Outcome.LOSS, losses);
      mOutcomeCounts.put(RPSLS.Outcome.DRAW, draws);
    }

    public Map<RPSLS.Outcome, Integer> getOutcomeCounts() {
      return Collections.unmodifiableMap(mOutcomeCounts);
    }

    public Map<RPSLS.Choice, Integer> getChoiceCounts() {
      return Collections.unmodifiableMap(mChoiceCounts);
    }
  }

  private User mUser = null;
  private String mUsername = null;
  private Stats mStats = null;
  private Date mCreationDate = null;
  private boolean mArtificialIntelligence = false;

  protected UserProfile() {}

  public UserProfile(String username, User user, Stats stats, Date creationDate, boolean isArtificialIntelligence) {
    mUsername = username;
    mUser = user;
    mStats = stats;
    mCreationDate = creationDate;
    mArtificialIntelligence = isArtificialIntelligence;
  }

  public final String getUsername() {
    return mUsername;
  }

  public final User getUser() { return mUser; }

  protected void setUsername(String username) {
    mUsername = username;
  }

  public final Stats getStats() {
    return mStats;
  }

  protected final void setStats(Stats stats) {
    mStats = stats;
  }

  public final Date getCreationDate() {
    return mCreationDate;
  }

  protected final void setCreationDate(Date creationDate) {
    mCreationDate = creationDate;
  }

  public final boolean isArtificialIntelligence() {
    return mArtificialIntelligence;
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
