/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.quickstart.helpers;

import android.content.Context;
import com.magnet.max.android.Attachment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AttachmentHelper {

    private static final String[] FILES = {"GoldenGateBridge1.jpg", "GoldenGateBridge2.jpg"};

    public static Attachment getRandomAttachment(Context context) throws IOException {
        int fileIdx = new Random().nextInt(FILES.length);
        InputStream stream = context.getAssets().open(FILES[fileIdx]);
        return new Attachment(stream, "image/*", FILES[fileIdx], "Random image");
    }

}
