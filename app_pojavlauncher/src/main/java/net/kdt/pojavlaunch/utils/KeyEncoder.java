package net.kdt.pojavlaunch.utils;

import net.kdt.pojavlaunch.AWTInputBridge;

/*
    About Key Events. Because the Android Spec doesn't require
    soft keyboards to dispatch key events not all keyboard implementations
    across Android will trigger these actions.

    Currently we use the following function to translate keycodes for
    special character, capital letters, and digits.

    keycode 123 (F12) is used as a single digit capslock button which
    when sent to the miniclient before a char will act accordingly.
 */

public class KeyEncoder {

    static String specialChars = "/*!@#$%^&*()\"{}_[+:;=-_]'|\\?/<>,.";
    static char modifier = 123;
    static char backspaceAndroid = 67;
    public static char backspaceUnicode = 8;

    public static void sendEncodedChar(int keyCode, char iC){
        System.out.println(keyCode);
        if(keyCode == 75)
            AWTInputBridge.sendKey((char)222,222);
        else if(keyCode == backspaceAndroid){
            AWTInputBridge.sendKey(backspaceUnicode,backspaceUnicode);
        } else if(specialChars.contains(""+iC)){
            // Send special character to client
            char c = iC;
            switch(c){
                case '!':
                    c = '1';
                    break;
                case '@':
                    c = '2';
                    break;
                case '#':
                    c = '3';
                    break;
                case '$':
                    c = '4';
                    break;
                case '%':
                    c = '5';
                    break;
                case '^':
                    c = '6';
                    break;
                case '&':
                    c = '7';
                    break;
                case '*':
                    c = '8';
                    break;
                case '(':
                    c = '9';
                    break;
                case ')':
                    c = '0';
                    break;
                case '_':
                    c = '-';
                    break;
                case '+':
                    c = '=';
                    break;
                case '{':
                    c = '[';
                    break;
                case '}':
                    c = ']';
                    break;
                case ':':
                    c = ';';
                    break;
                case '"':
                    c = '\'';
                    break;
                case '<':
                    c = ',';
                    break;
                case '>':
                    c = '.';
                    break;
                case '?':
                    c = '/';
                    break;
                case '|':
                    c = '\\';
                    break;
            }
            if(c != iC){
                AWTInputBridge.sendKey(modifier,modifier);
            }
            AWTInputBridge.sendKey(c,c);
        } else if(Character.isDigit(iC)){
            AWTInputBridge.sendKey(iC,iC);
        } else if (iC == Character.toUpperCase(iC)){
            // We send F12 as a modifier to avoid needing to worry about shift.
            // Client takes this modifier and does a toUpperCase().
            AWTInputBridge.sendKey(modifier,modifier);
            AWTInputBridge.sendKey(Character.toUpperCase(iC),Character.toUpperCase(iC));
        } else if(iC == Character.toLowerCase(iC)){
            AWTInputBridge.sendKey(Character.toUpperCase(iC),Character.toUpperCase(iC));
        } else {
            AWTInputBridge.sendKey(iC,keyCode);
        }
    }
}