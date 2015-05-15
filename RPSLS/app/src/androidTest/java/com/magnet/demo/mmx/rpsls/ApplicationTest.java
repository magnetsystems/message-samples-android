package com.magnet.demo.mmx.rpsls;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
  public ApplicationTest() {
    super(Application.class);
  }

  public void testRpsls() {
    RPSLS.Choice myChoice = RPSLS.Choice.ROCK;
    RPSLS.Choice theirChoice = RPSLS.Choice.LIZARD;

    RPSLS.How howIWin = RPSLS.getHowAttackerWins(myChoice, theirChoice);
    RPSLS.How howTheyWin = RPSLS.getHowAttackerWins(theirChoice, myChoice);

    assertNotNull(howIWin);
    assertEquals(howIWin, RPSLS.How.CRUSHES);
    assertNull(howTheyWin);
  }
}