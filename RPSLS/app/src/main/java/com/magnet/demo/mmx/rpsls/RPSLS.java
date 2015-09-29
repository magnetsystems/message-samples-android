package com.magnet.demo.mmx.rpsls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Contains the main RPSLS game logic.
 */
public class RPSLS {
  /**
   * Enumeration of the possible outcomes
   */
  public enum Outcome {WIN, LOSS, DRAW}

  /**
   * Enumeration of how a win is accomplished
   */
  public enum How {CUTS, COVERS, CRUSHES, POISONS, SMASHES, DECAPITATES, EATS, DISPROVES, VAPORIZES}

  /**
   * Enumeration of the possible choices for each user
   */
  public enum Choice {
    ROCK(R.drawable.rock_300),
    PAPER(R.drawable.paper_300),
    SCISSORS(R.drawable.scissors_300),
    LIZARD(R.drawable.lizard_300),
    SPOCK(R.drawable.spock_300);
    private int mResourceId;

    Choice(int resourceId) {
      mResourceId = resourceId;
    }

    public int getResourceId() {
      return mResourceId;
    }
  }

  /**
   * Statically initialized table of what each choice beats and how
   */
  private static final Map<Choice, BeatsHow[]> sBeatsTable;
  private static final BeatsHow[] ROCK_BEATS =
          {new BeatsHow(Choice.SCISSORS, How.CRUSHES, R.drawable.rock_vs_scissiors_450), new BeatsHow(Choice.LIZARD, How.CRUSHES, R.drawable.rock_vs_lizard_450)};
  private static final BeatsHow[] PAPER_BEATS =
          {new BeatsHow(Choice.ROCK, How.COVERS, R.drawable.paper_vs_rock_450), new BeatsHow(Choice.SPOCK, How.DISPROVES, R.drawable.paper_vs_spock_450)};
  private static final BeatsHow[] SCISSORS_BEATS =
          {new BeatsHow(Choice.PAPER, How.CUTS, R.drawable.scissors_vs_paper_450), new BeatsHow(Choice.LIZARD, How.DECAPITATES, R.drawable.scissors_vs_lizard_450)};
  private static final BeatsHow[] LIZARD_BEATS =
          {new BeatsHow(Choice.PAPER, How.EATS, R.drawable.lizard_vs_paper_450), new BeatsHow(Choice.SPOCK, How.POISONS, R.drawable.lizard_vs_spock_450)};
  private static final BeatsHow[] SPOCK_BEATS =
          {new BeatsHow(Choice.ROCK, How.VAPORIZES, R.drawable.spock_vs_rock_450), new BeatsHow(Choice.SCISSORS, How.SMASHES, R.drawable.spock_vs_scissors_450)};

  static {
    HashMap<Choice, BeatsHow[]> table = new HashMap<Choice, BeatsHow[]>();
    table.put(Choice.ROCK, ROCK_BEATS);
    table.put(Choice.PAPER, PAPER_BEATS);
    table.put(Choice.SCISSORS, SCISSORS_BEATS);
    table.put(Choice.LIZARD, LIZARD_BEATS);
    table.put(Choice.SPOCK, SPOCK_BEATS);
    sBeatsTable = Collections.unmodifiableMap(table);
  }

  /**
   * The choice that is beaten and how it's accomplished
   */
  static final class BeatsHow {
    private final Choice mBeats;
    private final How mHow;
    private final int mResourceId;

    private BeatsHow(Choice beats, How how, int resourceId) {
      mBeats = beats;
      mHow = how;
      mResourceId = resourceId;
    }

    public final Choice getChoice() {
      return mBeats;
    }

    public final How getHow() {
      return mHow;
    }

    public final int getResourceId() { return mResourceId; }
  }

  /**
   * The main logic to determine how an attacker beats the defender.  Will
   * return null of the attacker does not win.

   * @param attacker the attacker's choice
   * @param defender the defender's choice
   * @return how the defender is beaten
   */
  public static BeatsHow getHowAttackerWins(Choice attacker, Choice defender) {
    BeatsHow[] beatsHow = sBeatsTable.get(attacker);
    for (BeatsHow current : beatsHow) {
      if (current.getChoice().equals(defender)) {
        return current;
      }
    }
    return null;
  }

  /**
   * Contains all of the constants for the messaging that is used for this application.
   */
  public static final class MessageConstants {
    /**
     * The topic name that availability messages are published against
     */
    public static final String AVAILABILITY_TOPIC_NAME = "availableplayers";

    /**
     * Message type used to announce availability (or unavailability)
     */
    public static final String TYPE_AVAILABILITY = "AVAILABILITY";
    /**
     * An invitation message
     */
    public static final String TYPE_INVITATION = "INVITATION";
    /**
     * The acceptance of an invitation to play
     */
    public static final String TYPE_ACCEPTANCE = "ACCEPTANCE";
    /**
     * The message type used for an actual choice by a player
     */
    public static final String TYPE_CHOICE = "CHOICE";

    public static final String KEY_TYPE = "type";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_IS_AVAILABLE = "isAvailable";
    public static final String KEY_IS_ACCEPT = "isAccept";
    public static final String KEY_WINS = "wins";
    public static final String KEY_LOSSES = "losses";
    public static final String KEY_DRAWS = "ties";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_GAMEID = "gameId";
    public static final String KEY_CHOICE = "choice";

    public static final String CHOICE_ROCK = "ROCK";
    public static final String CHOICE_PAPER = "PAPER";
    public static final String CHOICE_SCISSORS = "SCISSORS";
    public static final String CHOICE_LIZARD = "LIZARD";
    public static final String CHOICE_SPOCK = "SPOCK";

    public static final Map<String, Choice> CHOICE_MAP;
    static {
      HashMap<String, Choice> choiceMap = new HashMap<>();
      choiceMap.put(CHOICE_ROCK, Choice.ROCK);
      choiceMap.put(CHOICE_PAPER, Choice.PAPER);
      choiceMap.put(CHOICE_SCISSORS, Choice.SCISSORS);
      choiceMap.put(CHOICE_LIZARD, Choice.LIZARD);
      choiceMap.put(CHOICE_SPOCK, Choice.SPOCK);
      CHOICE_MAP = Collections.unmodifiableMap(choiceMap);
    }

    public static final Map<Choice, String> CHOICE_REVERSE_MAP;
    static {
      HashMap<Choice, String> choiceMap = new HashMap<>();
      choiceMap.put(Choice.ROCK, CHOICE_ROCK);
      choiceMap.put(Choice.PAPER, CHOICE_PAPER);
      choiceMap.put(Choice.SCISSORS, CHOICE_SCISSORS);
      choiceMap.put(Choice.LIZARD, CHOICE_LIZARD);
      choiceMap.put(Choice.SPOCK, CHOICE_SPOCK);
      CHOICE_REVERSE_MAP = Collections.unmodifiableMap(choiceMap);
    }

    /**
     * The amount of time an inviter waits for a response from an invitee
     * before the invitees are considered to have "rejected" the invitation
     */
    public static final long OPPONENT_ACCEPTANCE_WAIT_TIME = 1 * 60 * 1000l; // 1 minute
    /**
     * The amount of time to wait for an opponent's choice after the current player makes
     * a choice.
     */
    public static final long OPPONENT_CHOICE_WAIT_TIME = 10 * 1000l; //10 seconds
    /**
     * The amount of time to look back into the player availability messages to build the available player's list.
     */
    public static final long AVAILABLE_PLAYERS_SINCE_DURATION = 30 * 60 * 1000l; //30 minutes
    /**
     * How long an invitation is valid
     */
    public static final long INVITATION_VALIDITY_DURATION = OPPONENT_ACCEPTANCE_WAIT_TIME; // 1 minute
  }

  /**
   * This Util class contains the main Magnet Message logic and navigation logic for RPSLS.
   */
  public static class Util {
    private static final String TAG = Util.class.getSimpleName();
    public static final String EXTRA_GAME_ID = "gameId";
    private static final String[] ROBOT_NAMES = {"Rosie", "C-3PO", "R2-D2", "Data", "David", "IronGiant", "Johnny5", "OptimusPrime", "Wall-E"};
    private static final char DELIMITER = '-';
    private static final Random RANDOM = new Random();
    private static final LinkedList<UserProfile> sAvailablePlayers = new LinkedList<>();
    private static final DateFormat mDateFormatter = DateFormat.getDateTimeInstance();
    private static final HashMap<String, Game> sPendingGames = new HashMap<>(); //gameId:game

    static {
      //These AI players can be selected as opponents by the current player
      //sAvailablePlayers.add(new UserProfile("HAL 9000", new UserProfile.Stats(0,0,0,0,0,0,0,0), new Date(-51494400000l), true)); //May 15, 1968
      //sAvailablePlayers.add(new UserProfile("WOPR/Joshua", new UserProfile.Stats(0,0,0,0,0,0,0,0), new Date(423446400000l), true)); //June 3, 1983
      //sAvailablePlayers.add(new UserProfile("Skynet", new UserProfile.Stats(0,0,0,0,0,0,0,0), new Date(467596800000l), true)); //October 26, 1984
      //sAvailablePlayers.add(new UserProfile("V'Ger", new UserProfile.Stats(0,0,0,0,0,0,0,0), new Date(313372800000l), true)); //December 7, 1979
      //sAvailablePlayers.add(new UserProfile("Gort", new UserProfile.Stats(0,0,0,0,0,0,0,0), new Date(-576288000000l), true)); //September 28, 1951
      sAvailablePlayers.add(new UserProfile("player_bot", new UserProfile.Stats(0,0,0,0,0,0,0,0), new Date(System.currentTimeMillis()), false)); //September 28, 1951
    }

    /**
     * Generates a username
     * @return a generated username
     */
    public static String generateUsername() {
      int randomIndex = RANDOM.nextInt(ROBOT_NAMES.length);
      return ROBOT_NAMES[randomIndex] +
              DELIMITER + RANDOM.nextInt(Integer.MAX_VALUE) +
              DELIMITER + (System.currentTimeMillis() / 1000);
    }

    /**
     * Generates a password
     * @return a generated password
     */
    public static byte[] generatePassword() {
      byte[] password = String.valueOf(RANDOM.nextLong()).getBytes();
      try {
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        digester.update(password);
        password = digester.digest();
      } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, "generatePassword(): Unable to hash the password.", e);
      }
      return password;
    }

    /**
     * Generates a UUID for the game.
     * @return a generated UUID
     */
    public static String generateGameId() {
      return UUID.randomUUID().toString();
    }

    /**
     * The current list of available players
     * @return available players
     */
    public static List<UserProfile> getAvailablePlayers() {
      synchronized (sAvailablePlayers) {
        return new ArrayList<>(sAvailablePlayers);
      }
    }

    /**
     * Sets up the messaging for this application.  This is meant to be called
     * after a connection is already made.  It will create the availability channel (if necessary),
     * subscribe to the channel, and publish the current user as available.
     *
     * @param context the context
     */
    public static void setupGameMessaging(final Context context) {
      final MMXChannel availabilityChannel = getAvailabilityChannel();
      availabilityChannel.subscribe(new MMXChannel.OnFinishedListener<String>() {
        public void onSuccess(String subId) {
          Log.d(TAG, "setupGameMessaging(): subscribed successfully to topic: " +
                  availabilityChannel.getName() + ", subId=" + subId);
        }

        public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
          Log.e(TAG, "setupGameMessaging(): unable to subscribe to availability topic", throwable);
        }
      });

      fetchAvailablePlayers(context);
    }

    public static void fetchAvailablePlayers(final Context context) {
      getAvailabilityChannel().getMessages(new Date(System.currentTimeMillis() - (MessageConstants.AVAILABLE_PLAYERS_SINCE_DURATION)),
              null, 0, 100, false, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {
                public void onSuccess(ListResult<MMXMessage> mmxMessages) {
                  Log.d(TAG, "fetchAvailablePlayers(): found " + mmxMessages.totalCount + " availability items published in the last 30 minutes");
                  for (int i = mmxMessages.totalCount; --i >= 0; ) {
                    //start with the last (oldest message);
                    handleAvailabilityMessage(context, mmxMessages.items.get(i));
                  }

                }

                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                  Log.e(TAG, "fetchAvailablePlayers(): caught exception while finding the latest available players", throwable);
                }
              });
    }

    /**
     * Publishes the availability for the current user
     *
     * @param context the context
     * @param isAvailable whether or not the current user is available
     */
    public static void publishAvailability(Context context, boolean isAvailable) {
      HashMap<String,String> messageContent = new HashMap<>();
      MyProfile profile = MyProfile.getInstance(context);
      setProfileToMessage(profile, messageContent);
      messageContent.put(MessageConstants.KEY_TYPE, MessageConstants.TYPE_AVAILABILITY);
      messageContent.put(MessageConstants.KEY_IS_AVAILABLE, String.valueOf(isAvailable));
      messageContent.put(MessageConstants.KEY_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
      getAvailabilityChannel().publish(messageContent, new MMXMessage.OnFinishedListener<String>() {
        public void onSuccess(String messageId) {
          Log.d(TAG, "publishAvailability(): successfully published availability: " + messageId);
        }

        public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
          Log.e(TAG, "publishAvailability(): unable to publish availability: " + failureCode, throwable);
        }
      });
    }

    /**
     * Handles an availability message.  This is a published availability message
     *
     * @param context the context
     * @param message the availability message
     */
    private static boolean handleAvailabilityMessage(Context context, MMXMessage message) {
      Map<String, String> messageContent = message.getContent();
      UserProfile profileFromPayload = parseUserProfileFromMessage(messageContent);
      String username = profileFromPayload.getUsername();
      Log.d(TAG, "handleAvailabilityMessage(): handling availability message sent by " + username + " at "
              + mDateFormatter.format(message.getTimestamp()));

      if (username == null || username.equals(MyProfile.getInstance(context).getUsername())) {
        Log.d(TAG, "handleAvailabilityMessage(): ignoring my own availability");
        return false;
      }

      boolean isAvailable = Boolean.parseBoolean(messageContent.get(MessageConstants.KEY_IS_AVAILABLE));

      synchronized (sAvailablePlayers) {
        for (int i = sAvailablePlayers.size(); --i>=0;) {
          final UserProfile profile = sAvailablePlayers.get(i);
          if (profile.getUsername().equals(username)) {
            //found an existing profile:
            sAvailablePlayers.remove(i);
            break;
          }
        }
        if (isAvailable) {
          //add profile here
          sAvailablePlayers.add(0, profileFromPayload);
        }
      }
      return true;
    }

    /**
     * Marshaller method to pull a UserProfile object from a payload
     *
     * @param messageContent the messageContent
     * @return the UserProfile object
     */
    private static UserProfile parseUserProfileFromMessage(Map<String,String> messageContent) {
      String username = messageContent.get(MessageConstants.KEY_USERNAME);
      String winStr = messageContent.get(MessageConstants.KEY_WINS);
      String lossStr = messageContent.get(MessageConstants.KEY_LOSSES);
      String tieStr = messageContent.get(MessageConstants.KEY_DRAWS);
      int wins = winStr == null ? 0 : Integer.parseInt(winStr);
      int losses = lossStr == null ? 0 : Integer.parseInt(lossStr);
      int ties = tieStr == null ? 0 : Integer.parseInt(tieStr);
      return new UserProfile(username,
              new UserProfile.Stats(wins, losses, ties, 0, 0, 0, 0, 0), null, false);
    }

    /**
     * Marshaller method to put a UserProfile into a payload
     *
     * @param profile the UserProfile
     * @param messageContent the message content
     */
    private static void setProfileToMessage(UserProfile profile, Map<String,String> messageContent) {
      Map<Outcome,Integer> outcomeCounts = profile.getStats().getOutcomeCounts();
      messageContent.put(MessageConstants.KEY_USERNAME, profile.getUsername());
      messageContent.put(MessageConstants.KEY_WINS, String.valueOf(outcomeCounts.get(Outcome.WIN)));
      messageContent.put(MessageConstants.KEY_LOSSES, String.valueOf(outcomeCounts.get(Outcome.LOSS)));
      messageContent.put(MessageConstants.KEY_DRAWS, String.valueOf(outcomeCounts.get(Outcome.DRAW)));
    }

    /**
     * Handles an incoming message, including messages from onMessageReceived and onPubsubItemReceived()
     *
     * @param context the context
     * @param message the incoming message
     * @return true if the message was processed, false otherwise
     */
    public static boolean handleIncomingMessage(final Context context, final MMXMessage message) {
      Map<String,String> messageContent = message.getContent();
      String type = messageContent.get(MessageConstants.KEY_TYPE);
      boolean returnVal = false;
      if (MessageConstants.TYPE_INVITATION.equals(type)) {
        returnVal = handleInvitation(context, message);
      } else if (MessageConstants.TYPE_ACCEPTANCE.equals(type)) {
        returnVal = handleInvitationAcceptance(context, message);
      } else if (MessageConstants.TYPE_CHOICE.equals(type)) {
        returnVal = handleOpponentChoice(message);
      } else if (MessageConstants.TYPE_AVAILABILITY.equals(type)) {
        returnVal = handleAvailabilityMessage(context, message);
      } else {
        Log.d(TAG, "handleIncomingMessage(): unsupported message type: " + type);
      }
      return returnVal;
    }

    private static Map<String,String> buildAcceptPayload(Context context, String gameId,
                                                 boolean isAccept) {
      HashMap<String,String> messageContent = new HashMap<>();
      messageContent.put(MessageConstants.KEY_TYPE, MessageConstants.TYPE_ACCEPTANCE);
      MyProfile myProfile = MyProfile.getInstance(context);
      messageContent.put(MessageConstants.KEY_GAMEID, gameId);
      messageContent.put(MessageConstants.KEY_IS_ACCEPT, String.valueOf(isAccept));
      messageContent.put(MessageConstants.KEY_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
      setProfileToMessage(myProfile, messageContent);
      return messageContent;
    }

    private static boolean handleInvitation(final Context context, MMXMessage message) {
      Log.d(TAG, "handleInvitation(): Message is an invitation");
      Map<String,String> messageContent = message.getContent();
      final String gameId = messageContent.get(MessageConstants.KEY_GAMEID);
      String timestamp = messageContent.get(MessageConstants.KEY_TIMESTAMP);
      final UserProfile profile = parseUserProfileFromMessage(messageContent);
      if (gameId == null || profile.getUsername() == null) {
        //can't continue
        Log.w(TAG, "handleInvitation(): Invitation is invalid");
        return false;
      } else if (timestamp != null && (System.currentTimeMillis() - Long.parseLong(timestamp)) > (MessageConstants.INVITATION_VALIDITY_DURATION)){
        //if the invitation is too old, drop it
        Log.d(TAG, "handleInvitation(): Invitation is outdated");
        return false;
      } else {
        //show a UI whether or not to accept the invitation
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.invitation_from, profile.getUsername()))
                .setMessage(R.string.accept_invitation_prompt)
                .setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    synchronized (sPendingGames) {
                      sPendingGames.put(gameId, new Game(gameId, profile));
                      HashSet<MMXUser> recipients = new HashSet<>();
                      recipients.add(new MMXUser.Builder()
                              .username(profile.getUsername())
                              .build());
                      MMXMessage message = new MMXMessage.Builder()
                              .content(buildAcceptPayload(context, gameId, true))
                              .recipients(recipients)
                              .build();
                      message.send(new MMXMessage.OnFinishedListener<String>() {
                        public void onSuccess(String messageId) {
                          Log.d(TAG, "handleInvitation(): sent acceptance message: " + messageId);
                          launchGameActivity(context, gameId);
                        }

                        public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
                          Log.e(TAG, "handleInvitation(): unable to send acceptance message", throwable);
                          Toast.makeText(context, "Unable to accept: " + failureCode + ", " + throwable, Toast.LENGTH_LONG).show();
                        }
                      });
                      dialog.dismiss();
                    }
                  }
                })
                .setNegativeButton(R.string.btn_reject, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    HashSet<MMXUser> recipients = new HashSet<>();
                    recipients.add(new MMXUser.Builder()
                            .username(profile.getUsername())
                            .build());
                    MMXMessage message = new MMXMessage.Builder()
                            .content(buildAcceptPayload(context, gameId, false))
                            .recipients(recipients)
                            .build();
                    message.send(new MMXMessage.OnFinishedListener<String>() {
                      public void onSuccess(String messageId) {
                        Log.d(TAG, "handleInvitation(): sent rejection message: " + messageId);
                      }

                      public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
                        Log.e(TAG, "handleInvitation(): unable to send rejection message", throwable);
                        Toast.makeText(context, "Unable to reject: " + failureCode + ", " + throwable, Toast.LENGTH_LONG).show();
                      }
                    });
                    dialog.dismiss();
                  }
                });
        builder.show();
        return true;
      }
    }

    private static boolean handleInvitationAcceptance(final Context context, MMXMessage message) {
      //start the game
      Map<String,String> messageContent = message.getContent();
      UserProfile opponent = parseUserProfileFromMessage(messageContent);
      final String gameId = messageContent.get(MessageConstants.KEY_GAMEID);
      String timestamp = messageContent.get(MessageConstants.KEY_TIMESTAMP);
      boolean isAccept = Boolean.parseBoolean(messageContent.get(MessageConstants.KEY_IS_ACCEPT));
      if (gameId == null) {
        Log.d(TAG, "handleInvitationAcceptance(): gameId was null");
        return false;
      } else if (timestamp != null && (System.currentTimeMillis() - Long.parseLong(timestamp)) > (MessageConstants.INVITATION_VALIDITY_DURATION)){
        //if the invitation is too old, drop it
        Log.d(TAG, "handleInvitationAcceptance(): Invitation is outdated");
        return false;
      }

      Game game = getGame(gameId);
      if (game != null) {
        if (game.getSelectedOpponent() == null) {
          if (isAccept) {
            game.setSelectedOpponent(opponent.getUsername());
            checkStartGame(context, game);
          } else {
            //game was rejected
            game.removeInvitee(opponent.getUsername());
            if (!game.hasRealInvitee()) {
              if (game.setRandomAIOpponent()) {
                checkStartGame(context, game);
              } else {
                //no more opponents.  remove this game
                removeGame(game.getGameId());
              }
            }
          }
        } else {
          Log.d(TAG, "handleInvitationAcceptance(): Game has already been accepted.  ignoring acceptance.");
        }
      }
      return true;
    }

    private static boolean handleOpponentChoice(final MMXMessage message) {
      Map<String,String> messageContent = message.getContent();
      String choiceStr = messageContent.get(MessageConstants.KEY_CHOICE);
      String gameId = messageContent.get(MessageConstants.KEY_GAMEID);
      Choice choice = MessageConstants.CHOICE_MAP.get(choiceStr);
      Log.d(TAG, "handleOpponentChoice(): gameId=" + gameId + ", choice=" + choiceStr);
      if (gameId == null || choiceStr == null) {
        Log.e(TAG, "handleOpponentChoice(): gameId or choiceStr was null.  Can't continue");
        return false;
      } else if (choice == null) {
        Log.e(TAG, "handleOpponentChoice(): unrecognized choice string: " + choiceStr);
        return false;
      }
      Game game = getGame(gameId);
      game.setOpponentChoice(MessageConstants.CHOICE_MAP.get(choiceStr));
      return true;
    }

    private static void checkStartGame(final Context context, final Game game) {
      UserProfile opponent = game.getSelectedOpponent();
      if (opponent != null) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.invitation_accepted)
                .setMessage(context.getString(R.string.starting_game, opponent.getUsername()))
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    launchGameActivity(context, game.mGameId);
                    dialog.dismiss();
                  }
                });
        builder.show();
      }
    }

    public static void launchGameActivity(Context context, String gameId) {
      Intent intent = new Intent(context, GameActivity.class);
      intent.putExtra(EXTRA_GAME_ID, gameId);
      context.startActivity(intent);
    }

    public static boolean sendInvitations(Context context, List<UserProfile> invitees) {
      boolean result = false;
      if (invitees != null && invitees.size() > 0) {
        String gameId = generateGameId();
        HashMap<String,String> messageContent = new HashMap<>();
        messageContent.put(MessageConstants.KEY_TYPE, MessageConstants.TYPE_INVITATION);
        MyProfile myProfile = MyProfile.getInstance(context);
        setProfileToMessage(myProfile, messageContent);
        messageContent.put(MessageConstants.KEY_GAMEID, gameId);
        messageContent.put(MessageConstants.KEY_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        final HashSet<MMXUser> recipients = new HashSet<>();
        ArrayList<UserProfile> aiPlayers = new ArrayList<>();
        for (UserProfile invitee : invitees) {
          if (!invitee.isArtificialIntelligence()) {
            recipients.add(new MMXUser.Builder().username(invitee.getUsername()).build());
          } else {
            aiPlayers.add(invitee);
          }
        }
        Game game = new Game(gameId, invitees);
        synchronized (sPendingGames) {
          sPendingGames.put(gameId, game);
        }

        if (recipients.size() > 0) {
          MMXMessage message = new MMXMessage.Builder()
                  .recipients(recipients)
                  .content(messageContent)
                  .build();
          message.send(new MMXMessage.OnFinishedListener<String>() {
            public void onSuccess(String messageId) {
              Log.d(TAG, "sendInvitations(): sent invitation to " + recipients.size() + " users.  messageId=" + messageId);
            }

            public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
              Log.e(TAG, "sendInvitations(): unable to send invitation to recipient: " + failureCode + ", " + throwable, throwable);
            }
          });
          game.waitForOpponent(context, MessageConstants.OPPONENT_ACCEPTANCE_WAIT_TIME);
          result = true;
        } else if (aiPlayers.size() > 0) {
          game.setRandomAIOpponent();
          checkStartGame(context, game);
          result = true;
        }
      }
      return result;
    }

    private static MMXChannel sAvailabilityChannel = null;

    private synchronized static MMXChannel getAvailabilityChannel() {
      if (sAvailabilityChannel == null) {
        MMXChannel.OnFinishedListener<MMXChannel> listener =
                new MMXChannel.OnFinishedListener<MMXChannel>() {
          public void onSuccess(MMXChannel mmxChannel) {
            sAvailabilityChannel = mmxChannel;
            synchronized (this) {
              this.notify();
            }
          }

          public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
            synchronized (this) {
              this.notify();
            }
          }
        };
        MMXChannel.getPublicChannel(MessageConstants.AVAILABILITY_TOPIC_NAME, listener);
        synchronized (listener) {
          try {
            listener.wait(5000);
          } catch (InterruptedException e) {
          }
        }
      }
      return sAvailabilityChannel;
    }

    public static Game getGame(String gameId) {
      synchronized (sPendingGames) {
        return sPendingGames.get(gameId);
      }
    }

    public static Game removeGame(String gameId) {
      synchronized (sPendingGames) {
        return sPendingGames.remove(gameId);
      }
    }
  }

  /**
   * Representation of an actual game.  Each time the current user invites players to play,
   * a new Game object is created in memory and the invitees responses are in references to the
   * specific game instance.
   */
  public static class Game {
    private static final String TAG = Game.class.getSimpleName();
    private String mGameId = null;
    private List<UserProfile> mInvitees = null;
    private UserProfile mSelectedOpponent = null;
    private Choice mOpponentChoice = null;
    private Runnable mWaitRunnable = null;

    /**
     * Called by the inviter
     *
     * @param gameId the game id
     * @param invitees a list of invitees
     */
    private Game(String gameId, List<UserProfile> invitees) {
      mGameId = gameId;
      mInvitees = invitees;
    }

    /**
     * Called by the person accepting the invitation
     *
     * @param gameId the game id
     * @param selectedOpponent the selected opponent
     */
    private Game(String gameId, UserProfile selectedOpponent) {
      mGameId = gameId;
      mSelectedOpponent = selectedOpponent;
    }

    public final String getGameId() {
      return mGameId;
    }

    private synchronized void removeInvitee(String username) {
      for (int i=mInvitees.size(); --i>=0;) {
        if (mInvitees.get(i).getUsername().equals(username)) {
          mInvitees.remove(i);
          break;
        }
      }
    }

    private synchronized boolean hasRealInvitee() {
      for (UserProfile invitee : mInvitees) {
        if (!invitee.isArtificialIntelligence()) {
          return true;
        }
      }
      return false;
    }

    private synchronized boolean setSelectedOpponent(String username) {
      if (mInvitees == null) {
        throw new IllegalStateException("This method should be called by the original inviter");
      }
      if (username == null) {
        throw new IllegalArgumentException("Username cannot be null");
      }
      if (mSelectedOpponent != null) {
        //opponent already selected
        return false;
      }
      for (UserProfile curProfile : mInvitees) {
        if (curProfile.getUsername().equals(username)) {
          mSelectedOpponent = curProfile;
          return true;
        }
      }
      throw new IllegalArgumentException("Specified username was not an invitee: " + username);
    }

    private synchronized boolean setRandomAIOpponent() {
      if (mInvitees == null) {
        throw new IllegalStateException("This method should be called by the original inviter");
      }
      ArrayList<UserProfile> ai = new ArrayList<UserProfile>();
      for (UserProfile curProfile : mInvitees) {
        if (curProfile.isArtificialIntelligence()) {
          ai.add(curProfile);
        }
      }
      int aiCount = ai.size();
      if (aiCount == 0) {
        Log.d(TAG, "setRandomAIOpponent(): no ai opponents were invited");
        return false;
      } else {
        mSelectedOpponent = ai.get(Util.RANDOM.nextInt(aiCount));
        return true;
      }
    }

    public synchronized UserProfile getSelectedOpponent() {
      return mSelectedOpponent;
    }

    private void setOpponentChoice(Choice choice) {
      synchronized (this) {
        mOpponentChoice = choice;
        this.notifyAll();
      }
    }

    private void waitForOpponent(final Context context, long waitTime) {
      mWaitRunnable = new Runnable() {
        public void run() {
          //if this runs, it means that it wasn't cancelled, which means that there was no response from a real player
          synchronized (Game.this) {
            if (mSelectedOpponent == null) {
              //select an opponent
              setRandomAIOpponent();
              Util.checkStartGame(context, Game.this);
            }
          }
        }
      };
      Handler handler = new Handler();
      handler.postDelayed(mWaitRunnable, waitTime);
    }

    /**
     * Called when the current player makes a choice.  This will block until the opponent makes their
     * choice selection or until timeout specified in the MessageConstants value.
     *
     * @param context the context
     * @param choice the current user's choice
     * @see com.magnet.demo.mmx.rpsls.RPSLS.MessageConstants#OPPONENT_CHOICE_WAIT_TIME
     * @return the result of this game
     */
    public Result getResult(Context context, Choice choice) {
      if (!mSelectedOpponent.isArtificialIntelligence()) {
        HashMap<String, String> messageContent = new HashMap<>();
        messageContent.put(MessageConstants.KEY_TYPE, MessageConstants.TYPE_CHOICE);
        MyProfile profile = MyProfile.getInstance(context);
        RPSLS.Util.setProfileToMessage(profile, messageContent);
        messageContent.put(MessageConstants.KEY_GAMEID, mGameId);
        messageContent.put(MessageConstants.KEY_CHOICE, MessageConstants.CHOICE_REVERSE_MAP.get(choice));
        messageContent.put(MessageConstants.KEY_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        HashSet<MMXUser> recipients = new HashSet<>();
        recipients.add(new MMXUser.Builder()
                .username(mSelectedOpponent.getUsername()).build());
        MMXMessage message = new MMXMessage.Builder()
                .recipients(recipients)
                .content(messageContent)
                .build();
        message.send(new MMXMessage.OnFinishedListener<String>() {
          public void onSuccess(String messageId) {
            Log.d(TAG, "getResult(): sent choice to opponent.  messageId=" + messageId);
          }

          public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
            Log.e(TAG, "getresult(): unable to send choice to opponent.", throwable);
          }
        });

        synchronized (this) {
          if (mOpponentChoice == null) {
            try {
              this.wait(MessageConstants.OPPONENT_CHOICE_WAIT_TIME);
            } catch (InterruptedException e) {
              Log.e(TAG, "getResult(): caught exception", e);
            }
          }
        }
      } else {
        //AI.  randomly make a choice and return the result
        mOpponentChoice = Choice.values()[Util.RANDOM.nextInt(Choice.values().length)];
      }
      if (mOpponentChoice != null) {
        BeatsHow iWinHow = RPSLS.getHowAttackerWins(choice, mOpponentChoice);
        BeatsHow theyWinHow = RPSLS.getHowAttackerWins(mOpponentChoice, choice);
        BeatsHow how = null;
        Outcome outcome;
        if (iWinHow != null) {
          how = iWinHow;
          outcome = Outcome.WIN;
        } else if (theyWinHow != null) {
          how = theyWinHow;
          outcome = Outcome.LOSS;
        } else {
          outcome = Outcome.DRAW;
        }
        MyProfile.getInstance(context).incrementCount(choice, outcome);
        return new Result(outcome, how != null ? how.getHow() : null, choice, mOpponentChoice, how != null ? how.getResourceId() : R.drawable.draw);
      } else {
        return null;
      }
    }

    /**
     * Represents the results of the current game.
     *
     * @see com.magnet.demo.mmx.rpsls.RPSLS.Game#getResult(Context, Choice)
     */
    public static class Result {
      public final Outcome outcome;
      public final How how;
      public final Choice myChoice;
      public final Choice opponentChoice;
      public final int resourceId;

      private Result(Outcome outcome, How how, Choice myChoice, Choice opponentChoice, int resourceId) {
        this.outcome = outcome;
        this.how = how;
        this.myChoice = myChoice;
        this.opponentChoice = opponentChoice;
        this.resourceId = resourceId;
      }
    }
  }
}
