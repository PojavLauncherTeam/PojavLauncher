/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

/**
 * <p>
 * Internal library methods
 * </p>
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision: 3608 $ $Id: LWJGLUtil.java 3608 2011-08-10 16:05:46Z
 *          spasi $
 */
public class LWJGLUtil {
	public static final int PLATFORM_LINUX = 1;
	public static final int PLATFORM_MACOSX = 2;
	public static final int PLATFORM_WINDOWS = 3;
	public static final String PLATFORM_LINUX_NAME = "linux";
	public static final String PLATFORM_MACOSX_NAME = "macosx";
	public static final String PLATFORM_WINDOWS_NAME = "windows";

	private static final String LWJGL_ICON_DATA_16x16 = "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\376\377\377\377\302\327\350\377"
			+ "\164\244\313\377\120\213\275\377\124\216\277\377\206\257\322\377"
			+ "\347\357\366\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\365\365\365\377\215\217\221\377\166\202\215\377"
			+ "\175\215\233\377\204\231\252\377\224\267\325\377\072\175\265\377"
			+ "\110\206\272\377\332\347\361\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\364\370\373\377\234\236\240\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\344\344\344\377\204\255\320\377"
			+ "\072\175\265\377\133\222\301\377\374\375\376\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\221\266\325\377\137\137\137\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\042\042\042\377\377\377\377\377\350\360\366\377"
			+ "\071\174\265\377\072\175\265\377\304\330\351\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\306\331\351\377"
			+ "\201\253\316\377\035\035\035\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\146\146\146\377\377\377\377\377\320\340\355\377"
			+ "\072\175\265\377\072\175\265\377\215\264\324\377\377\377\377\377"
			+ "\362\362\362\377\245\245\245\377\337\337\337\377\242\301\334\377"
			+ "\260\305\326\377\012\012\012\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\250\250\250\377\377\377\377\377\227\272\330\377"
			+ "\072\175\265\377\072\175\265\377\161\241\312\377\377\377\377\377"
			+ "\241\241\241\377\000\000\000\377\001\001\001\377\043\043\043\377"
			+ "\314\314\314\377\320\320\320\377\245\245\245\377\204\204\204\377"
			+ "\134\134\134\377\357\357\357\377\377\377\377\377\140\226\303\377"
			+ "\072\175\265\377\072\175\265\377\155\236\310\377\377\377\377\377"
			+ "\136\136\136\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\317\317\317\377\037\037\037\377\003\003\003\377\053\053\053\377"
			+ "\154\154\154\377\306\306\306\377\372\374\375\377\236\277\332\377"
			+ "\167\245\314\377\114\211\274\377\174\250\316\377\377\377\377\377"
			+ "\033\033\033\377\000\000\000\377\000\000\000\377\027\027\027\377"
			+ "\326\326\326\377\001\001\001\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\122\122\122\377\345\345\345\377\075\075\075\377"
			+ "\150\150\150\377\246\246\247\377\332\336\341\377\377\377\377\377"
			+ "\164\164\164\377\016\016\016\377\000\000\000\377\131\131\131\377"
			+ "\225\225\225\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\221\221\221\377\233\233\233\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\002\002\002\377\103\103\103\377"
			+ "\377\377\377\377\356\356\356\377\214\214\214\377\277\277\277\377"
			+ "\126\126\126\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\323\323\323\377\130\130\130\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\063\063\063\377"
			+ "\377\377\377\377\377\377\377\377\374\375\376\377\377\377\377\377"
			+ "\300\300\300\377\100\100\100\377\002\002\002\377\000\000\000\377"
			+ "\033\033\033\377\373\373\373\377\027\027\027\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\170\170\170\377"
			+ "\377\377\377\377\377\377\377\377\322\341\356\377\176\251\316\377"
			+ "\340\352\363\377\377\377\377\377\324\324\324\377\155\155\155\377"
			+ "\204\204\204\377\323\323\323\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\275\275\275\377"
			+ "\377\377\377\377\377\377\377\377\376\376\376\377\146\232\305\377"
			+ "\075\177\266\377\202\254\320\377\344\355\365\377\377\377\377\377"
			+ "\377\377\377\377\345\345\345\377\055\055\055\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\014\014\014\377\366\366\366\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\342\354\364\377"
			+ "\115\211\274\377\072\175\265\377\076\200\266\377\207\260\322\377"
			+ "\347\357\366\377\377\377\377\377\376\376\376\377\274\274\274\377"
			+ "\117\117\117\377\003\003\003\377\112\112\112\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\353\362\370\377\214\263\324\377\126\220\300\377\120\214\275\377"
			+ "\167\245\314\377\355\363\370\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\337\337\337\377\346\346\346\377\377\377\377\377";

	private static final String LWJGL_ICON_DATA_32x32 = "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\372\374\375\377"
			+ "\313\335\354\377\223\267\326\377\157\240\311\377\134\223\302\377\140\226\303\377\172\247\315\377\254\310\340\377\355\363\370\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\374\375\376\377\265\316\343\377\132\222\301\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\105\205\271\377"
			+ "\241\301\334\377\374\375\376\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\374\374\374\377\342\352\361\377\270\317\343\377\256\311\340\377"
			+ "\243\302\334\377\230\272\330\377\214\263\323\377\201\254\317\377\156\237\310\377\075\177\266\377\072\175\265\377\072\175\265\377"
			+ "\072\175\265\377\162\242\312\377\365\370\373\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\330\330\330\377\061\061\061\377\044\044\044\377\061\061\061\377\100\100\100\377"
			+ "\122\122\122\377\145\145\145\377\164\164\164\377\217\217\217\377\367\370\370\377\254\310\337\377\073\175\265\377\072\175\265\377"
			+ "\072\175\265\377\072\175\265\377\171\247\315\377\374\375\376\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\376\376\376\377\150\150\150\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\266\266\266\377\376\376\376\377\206\256\321\377\072\175\265\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\256\312\341\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\323\342\356\377\341\352\362\377\050\050\050\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\002\002\002\377\336\336\336\377\377\377\377\377\365\370\373\377\133\222\301\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\110\206\272\377\364\370\373\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\354\363\370\377\144\231\305\377\327\331\333\377\005\005\005\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\044\044\044\377\376\376\376\377\377\377\377\377\377\377\377\377\300\325\347\377"
			+ "\071\174\265\377\072\175\265\377\072\175\265\377\072\175\265\377\253\310\340\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\376\377\377\377"
			+ "\170\246\314\377\173\247\315\377\236\236\236\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\145\145\145\377\377\377\377\377\377\377\377\377\377\377\377\377\342\354\364\377"
			+ "\067\173\264\377\072\175\265\377\072\175\265\377\072\175\265\377\146\232\305\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\303\327\350\377"
			+ "\071\175\265\377\262\314\341\377\130\130\130\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\251\251\251\377\377\377\377\377\377\377\377\377\377\377\377\377\274\322\345\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\100\201\267\377\356\364\371\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\372\374\375\377\132\222\301\377"
			+ "\075\177\266\377\335\345\355\377\034\034\034\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\007\007\007\377\347\347\347\377\377\377\377\377\377\377\377\377\377\377\377\377\205\256\321\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\071\175\265\377\314\336\354\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\376\376\376\377\377\377\377\377\377\377\377\377\377\377\377\377\272\322\345\377\072\175\265\377"
			+ "\127\220\277\377\320\321\321\377\003\003\003\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\063\063\063\377\375\375\375\377\377\377\377\377\377\377\377\377\373\374\375\377\120\213\275\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\071\175\265\377\261\314\342\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\312\312\312\377\067\067\067\377\141\141\141\377\242\242\242\377\335\335\335\377\344\354\363\377\261\313\341\377"
			+ "\264\315\342\377\346\346\346\377\043\043\043\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\162\162\162\377\377\377\377\377\377\377\377\377\377\377\377\377\330\345\360\377\072\175\265\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\240\300\333\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\146\146\146\377\000\000\000\377\000\000\000\377\000\000\000\377\006\006\006\377\047\047\047\377\146\146\146\377"
			+ "\324\324\324\377\377\377\377\377\366\366\366\377\320\320\320\377\227\227\227\377\136\136\136\377\047\047\047\377\004\004\004\377"
			+ "\000\000\000\377\003\003\003\377\300\300\300\377\377\377\377\377\377\377\377\377\377\377\377\377\242\301\333\377\072\175\265\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\236\277\332\377\377\377\377\377\377\377\377\377"
			+ "\373\373\373\377\045\045\045\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\134\134\134\377\377\377\377\377\352\352\352\377\217\217\217\377\265\265\265\377\351\351\351\377\375\375\375\377\347\347\347\377"
			+ "\262\262\262\377\275\275\275\377\376\376\376\377\377\377\377\377\377\377\377\377\377\377\377\377\153\235\307\377\072\175\265\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\241\301\334\377\377\377\377\377\377\377\377\377"
			+ "\333\333\333\377\003\003\003\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\203\203\203\377\377\377\377\377\137\137\137\377\000\000\000\377\000\000\000\377\013\013\013\377\067\067\067\377\166\166\166\377"
			+ "\267\267\267\377\360\360\360\377\377\377\377\377\377\377\377\377\377\377\377\377\360\365\371\377\113\210\273\377\075\177\266\377"
			+ "\071\174\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\262\314\342\377\377\377\377\377\377\377\377\377"
			+ "\232\232\232\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\305\305\305\377\367\367\367\377\035\035\035\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\007\007\007\377\074\074\074\377\337\337\337\377\377\377\377\377\373\374\375\377\374\375\376\377\363\367\372\377"
			+ "\314\335\353\377\236\276\332\377\162\241\311\377\114\211\273\377\072\175\265\377\311\334\353\377\377\377\377\377\377\377\377\377"
			+ "\126\126\126\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\017\017\017\377"
			+ "\371\371\371\377\321\321\321\377\003\003\003\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\216\216\216\377\377\377\377\377\371\371\371\377\204\204\204\377\160\160\160\377"
			+ "\260\260\260\377\352\352\352\377\377\377\377\377\371\373\374\377\334\350\362\377\366\371\374\377\377\377\377\377\377\377\377\377"
			+ "\025\025\025\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\116\116\116\377"
			+ "\377\377\377\377\221\221\221\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\273\273\273\377\377\377\377\377\236\236\236\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\004\004\004\377\057\057\057\377\160\160\160\377\260\260\260\377\346\346\346\377\376\376\376\377\377\377\377\377"
			+ "\071\071\071\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\220\220\220\377"
			+ "\377\377\377\377\115\115\115\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\020\020\020\377\360\360\360\377\377\377\377\377\132\132\132\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\011\011\011\377\062\062\062\377\261\261\261\377"
			+ "\366\366\366\377\241\241\241\377\065\065\065\377\002\002\002\377\000\000\000\377\000\000\000\377\002\002\002\377\321\321\321\377"
			+ "\365\365\365\377\023\023\023\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\105\105\105\377\376\376\376\377\370\370\370\377\035\035\035\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\053\053\053\377"
			+ "\377\377\377\377\377\377\377\377\374\374\374\377\276\276\276\377\120\120\120\377\005\005\005\377\045\045\045\377\371\371\371\377"
			+ "\302\302\302\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\206\206\206\377\377\377\377\377\322\322\322\377\001\001\001\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\103\103\103\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\376\376\376\377\334\334\334\377\340\340\340\377\377\377\377\377"
			+ "\225\225\225\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\001\001\001\377\310\310\310\377\377\377\377\377\216\216\216\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\210\210\210\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\337\337\337\377\051\051\051\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\030\030\030\377\365\365\365\377\377\377\377\377\112\112\112\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\317\317\317\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\361\366\372\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\371\371\371\377\265\265\265\377\113\113\113\377\006\006\006\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\122\122\122\377\377\377\377\377\370\370\370\377\020\020\020\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\034\034\034\377\370\370\370\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\206\257\321\377\220\265\325\377\352\361\367\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\333\333\333\377\170\170\170\377\033\033\033\377\000\000\000\377"
			+ "\000\000\000\377\226\226\226\377\377\377\377\377\306\306\306\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\132\132\132\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\303\330\351\377\072\175\265\377\103\203\270\377"
			+ "\224\270\326\377\355\363\370\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\364\364\364\377\247\247\247\377"
			+ "\205\205\205\377\364\364\364\377\377\377\377\377\206\206\206\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\235\235\235\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\372\373\375\377\135\224\302\377\072\175\265\377"
			+ "\072\175\265\377\106\205\271\377\230\273\330\377\357\364\371\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\233\233\233\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\005\005\005\377\335\335\335\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\305\331\351\377\073\176\266\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\110\206\272\377\236\276\332\377\362\366\372\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\373\373\373\377\216\216\216\377\045\045\045\377\001\001\001\377\000\000\000\377"
			+ "\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\054\054\054\377\374\374\374\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\217\265\325\377"
			+ "\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\112\207\273\377\243\302\334\377\363\367\372\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\372\372\372\377\260\260\260\377\105\105\105\377"
			+ "\004\004\004\377\000\000\000\377\000\000\000\377\000\000\000\377\000\000\000\377\156\156\156\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\374\375\376\377"
			+ "\205\257\321\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\115\211\274\377"
			+ "\250\305\336\377\366\371\374\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\376\376\376\377"
			+ "\322\322\322\377\150\150\150\377\016\016\016\377\000\000\000\377\001\001\001\377\270\270\270\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\376\376\377\377\261\313\342\377\114\211\274\377\071\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377\072\175\265\377"
			+ "\072\175\265\377\115\211\274\377\277\324\347\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\354\354\354\377\223\223\223\377\233\233\233\377\375\375\375\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\363\367\372\377\265\316\343\377\201\254\320\377\145\231\305\377\141\227\304\377\154\236\310\377"
			+ "\217\265\325\377\305\331\351\377\367\372\374\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377"
			+ "\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377\377";

	/** LWJGL Logo - 16 by 16 pixels */
	public static final ByteBuffer LWJGLIcon16x16 = loadIcon(LWJGL_ICON_DATA_16x16);

	/** LWJGL Logo - 32 by 32 pixels */
	public static final ByteBuffer LWJGLIcon32x32 = loadIcon(LWJGL_ICON_DATA_32x32);

	/** Debug flag. */
	public static final boolean DEBUG = getPrivilegedBoolean("org.lwjgl.util.Debug");

	public static final boolean CHECKS = !getPrivilegedBoolean("org.lwjgl.util.NoChecks");

	private static final int PLATFORM;

	static {
		final String osName = getPrivilegedProperty("os.name");
		if (osName.startsWith("Windows"))
			PLATFORM = PLATFORM_WINDOWS;
		else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD") || osName.startsWith("SunOS")
				|| osName.startsWith("Unix") || osName.startsWith("Android"))
			PLATFORM = PLATFORM_LINUX;
		else if (osName.startsWith("Mac OS X") || osName.startsWith("Darwin"))
			PLATFORM = PLATFORM_MACOSX;
		else
			throw new LinkageError("Unknown platform: " + osName);
	}

	private static ByteBuffer loadIcon(String data) {
		int len = data.length();
		ByteBuffer bb = BufferUtils.createByteBuffer(len);
		for (int i = 0; i < len; i++) {
			bb.put(i, (byte) data.charAt(i));
		}
		return bb.asReadOnlyBuffer();
	}

	/**
	 * @see #PLATFORM_WINDOWS
	 * @see #PLATFORM_LINUX
	 * @see #PLATFORM_MACOSX
	 * @return the current platform type
	 */
	public static int getPlatform() {
		return PLATFORM;
	}

	/**
	 * @see #PLATFORM_WINDOWS_NAME
	 * @see #PLATFORM_LINUX_NAME
	 * @see #PLATFORM_MACOSX_NAME
	 * @return current platform name
	 */
	public static String getPlatformName() {
		switch (LWJGLUtil.getPlatform()) {
		case LWJGLUtil.PLATFORM_LINUX:
			return PLATFORM_LINUX_NAME;
		case LWJGLUtil.PLATFORM_MACOSX:
			return PLATFORM_MACOSX_NAME;
		case LWJGLUtil.PLATFORM_WINDOWS:
			return PLATFORM_WINDOWS_NAME;
		default:
			return "unknown";
		}
	}

	/**
	 * Locates the paths required by a library.
	 *
	 * @param libname
	 *            Local Library Name to search the classloader with ("openal").
	 * @param platform_lib_name
	 *            The native library name ("libopenal.so")
	 * @param classloader
	 *            The classloader to ask for library paths
	 * @return Paths to located libraries, if any
	 */
	public static String[] getLibraryPaths(String libname, String platform_lib_name, ClassLoader classloader) {
		return getLibraryPaths(libname, new String[] { platform_lib_name }, classloader);
	}

	/**
	 * Locates the paths required by a library.
	 *
	 * @param libname
	 *            Local Library Name to search the classloader with ("openal").
	 * @param platform_lib_names
	 *            The list of possible library names ("libopenal.so")
	 * @param classloader
	 *            The classloader to ask for library paths
	 * @return Paths to located libraries, if any
	 */
	public static String[] getLibraryPaths(String libname, String[] platform_lib_names, ClassLoader classloader) {
		// need to pass path of possible locations of library to native side
		List<String> possible_paths = new ArrayList<String>();

		String classloader_path = getPathFromClassLoader(libname, classloader);
		if (classloader_path != null) {
			log("getPathFromClassLoader: Path found: " + classloader_path);
			possible_paths.add(classloader_path);
		}

		for (String platform_lib_name : platform_lib_names) {
			String lwjgl_classloader_path = getPathFromClassLoader("lwjgl", classloader);
			if (lwjgl_classloader_path != null) {
				log("getPathFromClassLoader: Path found: " + lwjgl_classloader_path);
				possible_paths
						.add(lwjgl_classloader_path.substring(0, lwjgl_classloader_path.lastIndexOf(File.separator))
								+ File.separator + platform_lib_name);
			}

			// add Installer path
			String alternative_path = getPrivilegedProperty("org.lwjgl.librarypath");
			if (alternative_path != null) {
				possible_paths.add(alternative_path + File.separator + platform_lib_name);
			}

			// Add all possible paths from java.library.path
			String java_library_path = getPrivilegedProperty("java.library.path");

			StringTokenizer st = new StringTokenizer(java_library_path, File.pathSeparator);
			while (st.hasMoreTokens()) {
				String path = st.nextToken();
				possible_paths.add(path + File.separator + platform_lib_name);
			}

			// add current path
			String current_dir = getPrivilegedProperty("user.dir");
			possible_paths.add(current_dir + File.separator + platform_lib_name);

			// add pure library (no path, let OS search)
			possible_paths.add(platform_lib_name);
		}

		// create needed string array
		return possible_paths.toArray(new String[possible_paths.size()]);
	}

	static void execPrivileged(final String[] cmd_array) throws Exception {
		try {
			Process process = AccessController.doPrivileged(new PrivilegedExceptionAction<Process>() {
				public Process run() throws Exception {
					return Runtime.getRuntime().exec(cmd_array);
				}
			});
			// Close unused streams to make sure the child process won't hang
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
		} catch (PrivilegedActionException e) {
			throw (Exception) e.getCause();
		}
	}

	private static String getPrivilegedProperty(final String property_name) {
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				return System.getProperty(property_name);
			}
		});
	}

	/**
	 * Tries to locate named library from the current ClassLoader This method
	 * exists because native libraries are loaded from native code, and as such
	 * is exempt from ClassLoader library loading rutines. It therefore always
	 * fails. We therefore invoke the protected method of the ClassLoader to see
	 * if it can locate it.
	 *
	 * @param libname
	 *            Name of library to search for
	 * @param classloader
	 *            Classloader to use
	 * @return Absolute path to library if found, otherwise null
	 */
	private static String getPathFromClassLoader(final String libname, final ClassLoader classloader) {
		try {
			log("getPathFromClassLoader: searching for: " + libname);
			Class<?> c = classloader.getClass();
			while (c != null) {
				final Class<?> clazz = c;
				try {
					return AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
						public String run() throws Exception {
							Method findLibrary = clazz.getDeclaredMethod("findLibrary", String.class);
							findLibrary.setAccessible(true);
							String path = (String) findLibrary.invoke(classloader, libname);
							return path;
						}
					});
				} catch (PrivilegedActionException e) {
					log("Failed to locate findLibrary method: " + e.getCause());
					c = c.getSuperclass();
				}
			}
		} catch (Exception e) {
			log("Failure locating " + e + " using classloader:" + e);
		}
		return null;
	}

	/**
	 * Gets a boolean property as a privileged action.
	 */
	public static boolean getPrivilegedBoolean(final String property_name) {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				return Boolean.getBoolean(property_name);
			}
		});
	}

	/**
	 * Gets an integer property as a privileged action.
	 *
	 * @param property_name
	 *            the integer property name
	 *
	 * @return the property value
	 */
	public static Integer getPrivilegedInteger(final String property_name) {
		return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
			public Integer run() {
				return Integer.getInteger(property_name);
			}
		});
	}

	/**
	 * Gets an integer property as a privileged action.
	 *
	 * @param property_name
	 *            the integer property name
	 * @param default_val
	 *            the default value to use if the property is not defined
	 *
	 * @return the property value
	 */
	public static Integer getPrivilegedInteger(final String property_name, final int default_val) {
		return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
			public Integer run() {
				return Integer.getInteger(property_name, default_val);
			}
		});
	}

	/**
	 * Prints the given message to System.err if DEBUG is true.
	 *
	 * @param msg
	 *            Message to print
	 */
	public static void log(CharSequence msg) {
		if (DEBUG) {
			System.err.println("[LWJGL] " + msg);
		}
	}

	/**
	 * Method to determine if the current system is running a version of Mac OS
	 * X better than the given version. This is only useful for Mac OS X
	 * specific code and will not work for any other platform.
	 */
	public static boolean isMacOSXEqualsOrBetterThan(int major_required, int minor_required) {
		String os_version = getPrivilegedProperty("os.version");
		StringTokenizer version_tokenizer = new StringTokenizer(os_version, ".");
		int major;
		int minor;
		try {
			String major_str = version_tokenizer.nextToken();
			String minor_str = version_tokenizer.nextToken();
			major = Integer.parseInt(major_str);
			minor = Integer.parseInt(minor_str);
		} catch (Exception e) {
			LWJGLUtil.log("Exception occurred while trying to determine OS version: " + e);
			// Best guess, no
			return false;
		}
		return major > major_required || (major == major_required && minor >= minor_required);
	}

	/**
	 * Returns a map of public static final integer fields in the specified
	 * classes, to their String representations. An optional filter can be
	 * specified to only include specific fields. The target map may be null, in
	 * which case a new map is allocated and returned.
	 * <p>
	 * This method is useful when debugging to quickly identify values returned
	 * from the AL/GL/CL APIs.
	 *
	 * @param filter
	 *            the filter to use (optional)
	 * @param target
	 *            the target map (optional)
	 * @param tokenClasses
	 *            an array of classes to get tokens from
	 *
	 * @return the token map
	 */

	public static Map<Integer, String> getClassTokens(final TokenFilter filter, final Map<Integer, String> target,
			final Class... tokenClasses) {
		return getClassTokens(filter, target, Arrays.asList(tokenClasses));
	}

	/**
	 * Returns a map of public static final integer fields in the specified
	 * classes, to their String representations. An optional filter can be
	 * specified to only include specific fields. The target map may be null, in
	 * which case a new map is allocated and returned.
	 * <p>
	 * This method is useful when debugging to quickly identify values returned
	 * from the AL/GL/CL APIs.
	 *
	 * @param filter
	 *            the filter to use (optional)
	 * @param target
	 *            the target map (optional)
	 * @param tokenClasses
	 *            the classes to get tokens from
	 *
	 * @return the token map
	 */
	public static Map<Integer, String> getClassTokens(final TokenFilter filter, Map<Integer, String> target,
			final Iterable<Class> tokenClasses) {
		if (target == null)
			target = new HashMap<Integer, String>();

		final int TOKEN_MODIFIERS = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

		for (final Class tokenClass : tokenClasses) {
			for (final Field field : tokenClass.getDeclaredFields()) {
				// Get only <public static final int> fields.
				if ((field.getModifiers() & TOKEN_MODIFIERS) == TOKEN_MODIFIERS && field.getType() == int.class) {
					try {
						final int value = field.getInt(null);
						if (filter != null && !filter.accept(field, value))
							continue;

						if (target.containsKey(value)) // Print colliding tokens
														// in their hex
														// representation.
							target.put(value, toHexString(value));
						else
							target.put(value, field.getName());
					} catch (IllegalAccessException e) {
						// Ignore
					}
				}
			}
		}

		return target;
	}

	/**
	 * Returns a string representation of the integer argument as an unsigned
	 * integer in base&nbsp;16. The string will be uppercase and will have a
	 * leading '0x'.
	 *
	 * @param value
	 *            the integer value
	 *
	 * @return the hex string representation
	 */
	public static String toHexString(final int value) {
		return "0x" + Integer.toHexString(value).toUpperCase();
	}

	/** Simple interface for Field filtering. */
	public interface TokenFilter {

		/**
		 * Should return true if the specified Field passes the filter.
		 *
		 * @param field
		 *            the Field to test
		 * @param value
		 *            the integer value of the field
		 *
		 * @result true if the Field is accepted
		 */
		boolean accept(Field field, int value);

	}

}
