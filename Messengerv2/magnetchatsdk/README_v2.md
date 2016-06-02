#1. Installation

1. clone chat sdk repository
```
git clone https://github.com/magnetsystems/message-chatkit-android
```
2. In Android Studio in your project, add mmx chat sdk as a module.
3. Add to your application's dependecy list

#2. Integration with your App

## Initialization
In your Application subclass, init the Max and ChatSDK

```
public class YourApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Max.init(this, new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
        ChatSDK.init(this);
    }
}
```

####OR
Use ChatSDK.Builder.
You can add your own factories to chatkit, and named factories too.

```
public class YourApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Max.init(this, new MaxAndroidPropertiesConfig(this, R.raw.magnetmax));
        new ChatSDK.Builder()
                        .setDefaultMMXListItemFactory(new MyMMXListItemFactory())
                        .setDefaultMMXViewFactory(new MyMMXViewFactory())
                        .registerNamedFactory("custom", new MyMMXListItemFactory())
                        .init(this);
    }
}
```


#3. Layers

Chat sdk has mvp mutilayer architecture. You can create your own presenters, views, factories, converters

Chat sdk consists of the next layers.

## VIEWS
UI components which you can create using factories or xml
`com.magnet.magnetchat.ui`

## PRESENTERS
Presenters contains mmx messenger logic and interact with VIEW flow.
`com.magnet.magnetchat.presenters`

## CONVERTERS
Converters convert one models into another models.
`com.magnet.magnetchat.model.converters`

## FACTORIES
The layer creates VIEWS, CONVERTERS, PRESENTERS and beans
```
// bean factory
com.magnet.magnetchat.beans.MMXBeanFactory
//converter factory
com.magnet.magnetchat.model.converters.factoriesMMXObjectConverterFactory 
// presenter factory
com.magnet.magnetchat.presenters.core.MMXPresenterFactory
// list item factory
com.magnet.magnetchat.ui.factories.MMXListItemFactory
// views factory
com.magnet.magnetchat.ui.factories.MMXViewFactory
```

#4. Components

## Splash
Available only splash presenter

## Login 
Login available in next components:
You can attach to your xml file DefaultLoginView
`com.magnet.magnetchat.ui.views.login.DefaultLoginView`
Available attributes
```
    <com.magnet.magnetchat.ui.views.login.DefaultLoginView
    android:id="@+id/viewLogin"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginLeft="@dimen/dimen_15"
    android:layout_marginRight="@dimen/dimen_15"
    android:layout_marginTop="@dimen/dimen_10"
    app:backgroundsEdit="@drawable/background_edit_login"
    app:colorButtonRemember="@color/colorBlueDark"
    app:colorTextButtons="@color/colorBlueDark"
    app:colorTextRemember="@android:color/black"
    app:dimenButtonsText="@dimen/text_14"
    app:dimenEditsText="@dimen/text_14"
    app:dimenRememberText="@dimen/text_14"
    app:hintEmail="Enter email here"
    app:hintPassword="Enter password here"
    app:textButtonLogin="Sign in"
    app:textButtonRegister="Create account"
    app:textColorEdits="@android:color/black"
    app:textHintColorEdits="@android:color/darker_gray"
    app:textRemember="Remember me" />
```


Or implement your own login view
`com.magnet.magnetchat.ui.views.login.AbstractLoginView`

## Registration 
Registration available in next components:
xml attributes customization
`com.magnet.magnetchat.ui.views.register.DefaultRegisterView`
```
    <com.magnet.magnetchat.ui.views.register.DefaultRegisterView
    android:id="@+id/viewRegister"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginLeft="@dimen/dimen_16"
    android:layout_marginRight="@dimen/dimen_16"
    app:backgroundRegEdits="@drawable/background_edit_login"
    app:colorRegEdits="@android:color/black"
    app:colorRegHintEdits="@android:color/darker_gray"
    app:colorRegLabels="@android:color/black"
    app:colorRegLoadingBackground="@color/colorBlue"
    app:colorRegLoadingMessage="@android:color/white"
    app:colorRegTextButton="@color/colorBlueDark"
    app:dimenRegButton="@dimen/text_14"
    app:dimenRegEdits="@dimen/text_14"
    app:dimenRegLabels="@dimen/text_14"
    app:minimumRegPassLength="6" />
```
Or implement your own register view
`com.magnet.magnetchat.ui.views.register.AbstractRegisterView`

## Channel list
Channel list view available in next components:
Xml attributes customization:
`com.magnet.magnetchat.ui.views.channels.DefaultChannelsView`
```
    <com.magnet.magnetchat.ui.views.channels.DefaultChannelsView
     android:id="@+id/viewChannels"
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     app:colorChatsBackground="@android:color/black"
     app:colorChatsDivider="#64dd17"
     app:colorChatsHeaderText="@android:color/white"
     app:colorChatsMessageText="#ff6e40"
     app:colorChatsTimeText="#00b8d4"
     app:colorChatsUnreadTint="#ffea00"
     app:dimenTextChatsHeader="@dimen/text_16"
     app:dimenTextChatsMessage="@dimen/text_14"
     app:dimenTextChatsTime="@dimen/dimen_10"
     app:isNeedChatsImage="true"
     app:textChatsDateFormat="MMM -> hh:mm"
     app:textChatsLocationMessage="Map message"
     app:textChatsNoMessage="No more message"
     app:textChatsPhotoMessage="Instagram message" />
```
    
Or implement your own channel list view:
`com.magnet.magnetchat.ui.views.channels.AbstractChannelsView`

## Edit profile
Edit profile screen available in next components:
Xml attribute customization:
`package com.magnet.magnetchat.ui.views.edit.DefaultEditProfileView`
```
    <com.magnet.magnetchat.ui.views.edit.DefaultEditProfileView
         android:id="@+id/viewEditProfile"
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:layout_marginLeft="@dimen/dimen_14"
         android:layout_marginRight="@dimen/dimen_14"
         app:backgroundEditUserEdits="@drawable/background_edit_login"
         app:colorEditUserEdits="@android:color/black"
         app:colorEditUserHintEdits="@android:color/darker_gray"
         app:colorEditUserLabels="@color/colorBlueDark"
         app:colorEditUserLoadingBackground="@color/colorBlue"
         app:colorEditUserLoadingMessage="@android:color/white"
         app:colorEditUserTextButton="@color/colorBlueDark"
         app:dimenEditUserButton="@dimen/text_14"
         app:dimenEditUserEdits="@dimen/text_14"
         app:dimenEditUserLabels="@dimen/text_16" />
```

Or implement your own edit profile view:
`com.magnet.magnetchat.ui.views.edit.AbstractEditProfileView`

##Chat view:
Activity:
use static methods for creation:
`com.magnet.magnetchat.ui.activities.MMXChatActivity`
Fragment:
`com.magnet.magnetchat.ui.fragments.MMXChatFragment`
Views:

1. **Chat view**
`com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatView`
available xml attributes:
```
  app:chatlist_background="@drawable/splash"
  app:post_background="@color/colorAccentCover"
  app:post_height="?attr/actionBarSize"
  app:postdivider_color="@color/common_google_signin_btn_text_dark_focused"
  app:postdivider_height="@dimen/dimen_3"
```
or create your own
`com.magnet.magnetchat.ui.views.chatlist.MMXChatView`

2. **Chat list view:**
You can add only chat list without posting field:
Xml customization view:
`com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatListView`
xml attributes customization
```
<com.magnet.magnetchat.ui.views.chatlist.DefaultMMXChatView
    app:chatlist_background="@drawable/splash"
    app:post_background="@color/colorAccentCover"
    app:post_height="?attr/actionBarSize"
    app:postdivider_color="@color/common_google_signin_btn_text_dark_focused"
    app:postdivider_height="@dimen/dimen_3" />
```

Or implement your own view:
`com.magnet.magnetchat.ui.views.chatlist.MMXChatListView`

3. **Post view**
`com.magnet.magnetchat.ui.views.chatlist.DefaultMMXPostMessageView`
xml attributes customization
```
  app:attach_background="@android:color/holo_red_light"
  app:attach_marginBottom="0dp"
  app:attach_marginLeft="0dp"
  app:attach_marginRight="0dp"
  app:attach_marginTop="0dp"
  app:attach_padding="7dp"
  app:attach_src="@drawable/user_group"
  app:send_background="@android:color/holo_green_light"
  app:send_padding="7dp"
  app:send_text="Post"
  app:send_textColor="@android:color/holo_red_dark"
  app:send_textSize="20sp"
  app:text_background="@android:color/transparent"
  app:text_hint="Hint text"
  app:text_marginLeft="@dimen/dimen_7"
  app:text_marginTop="@dimen/dimen_2"
  app:text_maxLines="2"
  app:text_textColor="@android:color/holo_red_dark"
  app:text_textSize="@dimen/text_14" />
```
or you can implement your own
`com.magnet.magnetchat.ui.views.chatlist.MMXPostMessageView`

#### If you want use exists activity or fragment but want change chat list view or post view:
You can inherit from exist factory and override some methods
**EXAMPLE:**
```
public class MyMMXViewFactory extends DefaultMMXViewFactory {

    @Override
    public MMXPostMessageView createMMXPostMessageView(Context context) {
        return (MMXPostMessageView) LayoutInflater.from(context).inflate(R.layout.test_view_post, null, false);
    }


    @Override
    public MMXChatView createMMXChatView(Context context) {
        return (MMXChatView) LayoutInflater.from(context).inflate(R.layout.test_view_chatlist, null, false);
    }
}
```

#### If you need customize message item:
You can customize exists message or add your own
path to items: `com.magnet.magnetchat.ui.views.chatlist.list`

**You have to override item factory**
 `com.magnet.magnetchat.ui.factories.MMXListItemFactory`
 
Example how to add xml customized message:
```
 public class MyMMXListItemFactory extends DefaultMMXListItemFactory {
     @Override
     protected BaseMMXTypedView createMyCustomView(Context context, int type) {
         switch (type) {
             case MMXMessageWrapper.TYPE_TEXT_ANOTHER:
                 return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_cutom_msg_another, null, false);
             case MMXMessageWrapper.TYPE_TEXT_MY:
                 return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_cutom_msg_my, null, false);
             case MMXMessageWrapper.TYPE_PHOTO_MY:
             case MMXMessageWrapper.TYPE_PHOTO_ANOTHER:
                 return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_custom_msg_pic_another, null, false);
             case MMXMessageWrapper.TYPE_POLL_MY:
                 return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_custom_msg_poll_my, null, false);
             case MMXMessageWrapper.TYPE_VOTE_ANSWER:
                 return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_custom_answers, null, false);
             case MMXUserWrapper.TYPE_USER:
                 return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_user_item, null, false);
         }
         return super.createMyCustomView(context, type);
     }
 }
```
**_Name convention for message type:_**
Set the first bit `1` of last 2 bytes of message type
My message mask `0xFFFF`
Another message mask `0x7FFF`

Example text message customization:
```
<com.magnet.magnetchat.ui.views.chatlist.list.DefaultMMXMessageMyView 
    app:common_background="@android:color/white"
    app:date_textColor="@android:color/holo_green_dark"
    app:date_textSize="@dimen/text_10"
    app:letters_textColor="@android:color/holo_red_dark"
    app:letters_textSize="@dimen/text_12"
    app:msg_bubble="@drawable/msg_sent"
    app:msg_paddingBottom="@dimen/dimen_7"
    app:msg_paddingLeft="@dimen/dimen_25"
    app:msg_paddingRight="@dimen/dimen_15"
    app:msg_paddingTop="@dimen/dimen_7"
    app:msg_textColor="@android:color/white"
    app:uname_textColor="@android:color/holo_red_dark"
    app:uname_textSize="@dimen/text_12"
    app:upic_borderColor="@color/colorMagnetRedCover"
    app:upic_borderSize="@dimen/dimen_3"
    app:upic_src="@android:color/holo_green_dark" />
```

Or you can implement your own. 

Available next message items types:
```
    public static final int TYPE_MAP_MY = 0xFF00;
    public static final int TYPE_VIDEO_MY = 0xFF01;
    public static final int TYPE_PHOTO_MY = 0xFF02;
    public static final int TYPE_TEXT_MY = 0xFF03;
    public static final int TYPE_POLL_MY = 0xFF04;
    public static final int TYPE_MAP_ANOTHER = 0x7F05;
    public static final int TYPE_VIDEO_ANOTHER = 0x7F06;
    public static final int TYPE_PHOTO_ANOTHER = 0x7F07;
    public static final int TYPE_TEXT_ANOTHER = 0x7F08;
    public static final int TYPE_POLL_ANOTHER = 0x7F09;
    public static final int MY_MESSAGE_MASK = 0x8000;
    public static final int TYPE_VOTE_ANSWER = 0x7888;
```


## How to implement your own message type:
### Coming soon...

