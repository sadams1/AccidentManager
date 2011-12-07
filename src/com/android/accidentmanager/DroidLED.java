/*
 * Thanks to the DroidLED project for this library, which has some slick reflection
 * that works under Froyo without additional aidl files, access to private API java
 * files, etc.!
 * 
 * http://code.google.com/p/droidled/
 * http://android.git.kernel.org/?p=platform/frameworks/base.git;a=commit;h=eb9cbb8fdddf4c887004b20b504083035d57a15f
 * 
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright (c) 2009 ogodno
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.android.accidentmanager;

import java.lang.reflect.Method;

import android.os.IBinder;

public class DroidLED {

        private Object svc = null;
        private Method getFlashlightEnabled = null;
        private Method setFlashlightEnabled = null;

        @SuppressWarnings("unchecked")
        public DroidLED() throws Exception {
                try {
                        // call ServiceManager.getService("hardware") to get an IBinder for the service.
                        // this appears to be totally undocumented and not exposed in the SDK whatsoever.
                        Class sm = Class.forName("android.os.ServiceManager");
                        Object hwBinder = sm.getMethod("getService", String.class).invoke(null, "hardware");

                        // get the hardware service stub. this seems to just get us one step closer to the proxy
                        Class hwsstub = Class.forName("android.os.IHardwareService$Stub");
                        Method asInterface = hwsstub.getMethod("asInterface", android.os.IBinder.class);
                        svc = asInterface.invoke(null, (IBinder) hwBinder);

                        // grab the class (android.os.IHardwareService$Stub$Proxy) so we can reflect on its methods
                        Class proxy = svc.getClass();

                        // save methods
                        getFlashlightEnabled = proxy.getMethod("getFlashlightEnabled");
                        setFlashlightEnabled = proxy.getMethod("setFlashlightEnabled", boolean.class);
                }
                catch(Exception e) {
                        throw new Exception("LED could not be initialized");
                }
        }

        public boolean isEnabled() {
                try {
                        return getFlashlightEnabled.invoke(svc).equals(true);
                }
                catch(Exception e) {
                        return false;
                }
        }

        public void enable(boolean tf) {
                try {
                        setFlashlightEnabled.invoke(svc, tf);
                }
                catch(Exception e) {}
        }
}
