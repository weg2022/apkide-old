/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.util;

import android.os.Build;

import brut.common.BrutException;

public class OSDetection {

    public static String getSupportedAbi(){
        for (String abi : Build.SUPPORTED_ABIS) {
             if (abi.endsWith("arm64-v8a")||abi.equals("armeabi-v7a")||abi.equals("x86")){
                return abi;
             }
        }
        return null;
    }
}
