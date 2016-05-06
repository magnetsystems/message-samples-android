package com.magnet.magnetchat.test;

import com.magnet.magnetchat.model.MMXMessageWrapper;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by aorehov on 06.05.16.
 */
public class BItTest {

    @Test
    public void testMask() {
        Assert.assertTrue((MMXMessageWrapper.TYPE_MAP_MY & MMXMessageWrapper.MY_MESSAGE_MASK) == MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_PHOTO_MY & MMXMessageWrapper.MY_MESSAGE_MASK) == MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_POLL_MY & MMXMessageWrapper.MY_MESSAGE_MASK) == MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_TEXT_MY & MMXMessageWrapper.MY_MESSAGE_MASK) == MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_VIDEO_MY & MMXMessageWrapper.MY_MESSAGE_MASK) == MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_MAP_ANOTHER & MMXMessageWrapper.MY_MESSAGE_MASK) != MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_PHOTO_ANOTHER & MMXMessageWrapper.MY_MESSAGE_MASK) != MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_POLL_ANOTHER & MMXMessageWrapper.MY_MESSAGE_MASK) != MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_TEXT_ANOTHER & MMXMessageWrapper.MY_MESSAGE_MASK) != MMXMessageWrapper.MY_MESSAGE_MASK);
        Assert.assertTrue((MMXMessageWrapper.TYPE_VIDEO_ANOTHER & MMXMessageWrapper.MY_MESSAGE_MASK) != MMXMessageWrapper.MY_MESSAGE_MASK);
    }

}
