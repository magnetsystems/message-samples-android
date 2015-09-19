package com.magnet.smartshopper.services;


import android.content.Context;
import android.widget.Toast;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.client.common.Log;
import com.magnet.smartshopper.model.User;
import com.magnet.smartshopper.walmart.model.Product;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MagnetMessageService {

    private static final String TAG = "MagnetMessageService";
    private static final String DEFAULT_USER = "smartshopperandroid";
    private static final String DEFAULT_PASSWORD = "smartshopperandroid";

    public static final String ITEM_ID = "itemId";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_PRICE = "salePrice";
    public static final String ITEM_IMAGE = "productUrl";
    public static final String MY_WISH_LIST = "myWishList";

    public static void registerAndLoginUser(final Context context) {

        Log.e(TAG, "Creating user -" + DEFAULT_USER);
        MMXUser user = new MMXUser.Builder().username(DEFAULT_USER).displayName(DEFAULT_USER).build();


        user.register(DEFAULT_PASSWORD.getBytes(), new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                //Successful registration.  login?
                Toast.makeText(context, "User has been created", Toast.LENGTH_LONG).show();
                loginUser(context);
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                if (MMXUser.FailureCode.REGISTRATION_USER_ALREADY_EXISTS.equals(failureCode)) {
                    loginUser(context);
                    return;
                }
                Toast.makeText(context, "User Registration Error.  Please try again." + failureCode.getDescription() + " " + failureCode.getValue(), Toast.LENGTH_LONG).show();

            }
        });
    }



    private static void loginUser(final Context context) {
        //Login with user
        MMX.login(DEFAULT_USER, DEFAULT_PASSWORD.getBytes(), new MMX.OnFinishedListener<Void>() {


            public void onSuccess(Void aVoid) {
                //success!
                //if an EventListener has already been registered, start receiving messages
                MMX.enableIncomingMessages(true);

                Toast.makeText(context,
                        "Logged in", Toast.LENGTH_LONG).show();
                createWishListChannel();
            }

            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(context,
                        "Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void createWishListChannel() {
        
        MMXChannel.create(MY_WISH_LIST,"Chanel to store my wish ist",false,new MMXChannel.OnFinishedListener<MMXChannel>() {

            public void onSuccess(final MMXChannel result) {
                Log.i(TAG, "Wish list channel got created");
            }

            public void onFailure(final MMXChannel.FailureCode code, final Throwable ex) {
                Log.e(TAG, "Exception caught: " + code, ex);

            }
        });
    }

   public static void addToWishList(final Context context,final Product product) {

       MMXChannel.getPrivateChannel(MY_WISH_LIST, new MMXChannel.OnFinishedListener<MMXChannel>() {
           @Override
           public void onSuccess(MMXChannel mmxChannel) {
               Map<String, String> messageMap = new HashMap<String, String>(4);

               messageMap.put(ITEM_ID, product.getId());
               messageMap.put(ITEM_NAME, product.getName());
               if(product.getSalePrice() != null && product.getSalePrice().length() > 1) {
                   messageMap.put(ITEM_PRICE, product.getSalePrice().substring(1));
               }

               messageMap.put(ITEM_IMAGE, product.getThumbnailImage());

               mmxChannel.publish(messageMap, new MMXChannel.OnFinishedListener<String>() {
                   @Override
                   public void onSuccess(String s) {
                       Toast.makeText(context,
                               "Product '" + product.getName() + "' has been added to wish list", Toast.LENGTH_LONG).show();
                   }

                   @Override
                   public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                       Toast.makeText(context,
                               "Error during publish: " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                   }
               });
           }

           @Override
           public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
               Toast.makeText(context,
                       "Not able to find the channel : " + throwable.getMessage(), Toast.LENGTH_LONG).show();
           }
       });


    }

    public static void shareTheProduct(final Context context, final User targetUser, final Product product) {

            HashSet<MMXUser> recipients = new HashSet<MMXUser>();
            String username =targetUser.getUsername();
            MMXUser mmxUser = new MMXUser.Builder().username(username).displayName(username).build();
            recipients.add(mmxUser);

            Map<String, String> content = new HashMap<String, String>(4);
            content.put(ITEM_ID, product.getId());
            content.put(ITEM_NAME, product.getName());
            if(product.getSalePrice() != null && product.getSalePrice().length() > 1) {
                content.put(ITEM_PRICE, product.getSalePrice().substring(1));
            }
            content.put(ITEM_IMAGE, product.getThumbnailImage());
            // Build the message
            MMXMessage message = new MMXMessage.Builder()
                    .recipients(recipients)
                    .content(content)
                    .build();

            String messageId = message.send(new MMXMessage.OnFinishedListener<String>() {

                @Override
                public void onSuccess(String s) {
                    Toast.makeText(context,
                            "Product " + product.getName() + " has been shared", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
                    Toast.makeText(context,
                            "Error while sending the message", Toast.LENGTH_LONG).show();
                }
            });

    }
}
