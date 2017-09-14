package com.prplplus.test;

public class KeyDown {
    public static void main(String[] args) {
        System.out.println("-A-B-C-D-E-F-G-H-I-J-K-L-M-N-O-P-Q-R-S-T-U-V-W-X-Y-Z-".length());
        for (char k = 'A'; k <= 'Z'; k++) {
            //System.out.format("\"%1$s\" GetKeyDown if%n" +
            //        "    \"%1$s\" @onKeyDown%n" +
            //        "endif%n%n", k);
            System.out.print(k + "-");
        }

        /*String[] additional = { "Space", "Backspace", "At", "Comma", "Minus", "DoubleQuote", "Less", "Greater",
                "JoystickButton0",
                "JoystickButton1",
                "JoystickButton2",
                "JoystickButton3",
                "JoystickButton4",
                "JoystickButton5",
                "JoystickButton6",
                "JoystickButton7",
                "JoystickButton8",
                "JoystickButton9",
                "JoystickButton10",
                "JoystickButton11",
                "JoystickButton12",
                "JoystickButton13",1!2@#$%^&*()_+
                "JoystickButton14",
                "JoystickButton15",
                "JoystickButton16",
                "JoystickButton17",
                "JoystickButton18",
                "JoystickButton19"
        };
        for (String k : additional) {
            System.out.format("\"%1$s\" GetKeyDown if%n" +
                    "    \"%1$s\" @onKeyDown%n" +
                    "endif%n%n", k);
        }//*/

        /*String allKeys = "None,\r\n" +
                "        Backspace = 8,\r\n" +
                "        Delete = 127,\r\n" +
                "        Tab = 9,\r\n" +
                "        Clear = 12,\r\n" +
                "        Return,\r\n" +
                "        Pause = 19,\r\n" +
                "        Escape = 27,\r\n" +
                "        Space = 32,\r\n" +
                "        Keypad0 = 256,\r\n" +
                "        Keypad1,\r\n" +
                "        Keypad2,\r\n" +
                "        Keypad3,\r\n" +
                "        Keypad4,\r\n" +
                "        Keypad5,\r\n" +
                "        Keypad6,\r\n" +
                "        Keypad7,\r\n" +
                "        Keypad8,\r\n" +
                "        Keypad9,\r\n" +
                "        KeypadPeriod,\r\n" +
                "        KeypadDivide,\r\n" +
                "        KeypadMultiply,\r\n" +
                "        KeypadMinus,\r\n" +
                "        KeypadPlus,\r\n" +
                "        KeypadEnter,\r\n" +
                "        KeypadEquals,\r\n" +
                "        UpArrow,\r\n" +
                "        DownArrow,\r\n" +
                "        RightArrow,\r\n" +
                "        LeftArrow,\r\n" +
                "        Insert,\r\n" +
                "        Home,\r\n" +
                "        End,\r\n" +
                "        PageUp,\r\n" +
                "        PageDown,\r\n" +
                "        F1,\r\n" +
                "        F2,\r\n" +
                "        F3,\r\n" +
                "        F4,\r\n" +
                "        F5,\r\n" +
                "        F6,\r\n" +
                "        F7,\r\n" +
                "        F8,\r\n" +
                "        F9,\r\n" +
                "        F10,\r\n" +
                "        F11,\r\n" +
                "        F12,\r\n" +
                "        F13,\r\n" +
                "        F14,\r\n" +
                "        F15,\r\n" +
                "        Alpha0 = 48,\r\n" +
                "        Alpha1,\r\n" +
                "        Alpha2,\r\n" +
                "        Alpha3,\r\n" +
                "        Alpha4,\r\n" +
                "        Alpha5,\r\n" +
                "        Alpha6,\r\n" +
                "        Alpha7,\r\n" +
                "        Alpha8,\r\n" +
                "        Alpha9,\r\n" +
                "        Exclaim = 33,\r\n" +
                "        DoubleQuote,\r\n" +
                "        Hash,\r\n" +
                "        Dollar,\r\n" +
                "        Ampersand = 38,\r\n" +
                "        Quote,\r\n" +
                "        LeftParen,\r\n" +
                "        RightParen,\r\n" +
                "        Asterisk,\r\n" +
                "        Plus,\r\n" +
                "        Comma,\r\n" +
                "        Minus,\r\n" +
                "        Period,\r\n" +
                "        Slash,\r\n" +
                "        Colon = 58,\r\n" +
                "        Semicolon,\r\n" +
                "        Less,\r\n" +
                "        Equals,\r\n" +
                "        Greater,\r\n" +
                "        Question,\r\n" +
                "        At,\r\n" +
                "        LeftBracket = 91,\r\n" +
                "        Backslash,\r\n" +
                "        RightBracket,\r\n" +
                "        Caret,\r\n" +
                "        Underscore,\r\n" +
                "        BackQuote,\r\n" +
                "        A,\r\n" +
                "        B,\r\n" +
                "        C,\r\n" +
                "        D,\r\n" +
                "        E,\r\n" +
                "        F,\r\n" +
                "        G,\r\n" +
                "        H,\r\n" +
                "        I,\r\n" +
                "        J,\r\n" +
                "        K,\r\n" +
                "        L,\r\n" +
                "        M,\r\n" +
                "        N,\r\n" +
                "        O,\r\n" +
                "        P,\r\n" +
                "        Q,\r\n" +
                "        R,\r\n" +
                "        S,\r\n" +
                "        T,\r\n" +
                "        U,\r\n" +
                "        V,\r\n" +
                "        W,\r\n" +
                "        X,\r\n" +
                "        Y,\r\n" +
                "        Z,\r\n" +
                "        Numlock = 300,\r\n" +
                "        CapsLock,\r\n" +
                "        ScrollLock,\r\n" +
                "        RightShift,\r\n" +
                "        LeftShift,\r\n" +
                "        RightControl,\r\n" +
                "        LeftControl,\r\n" +
                "        RightAlt,\r\n" +
                "        LeftAlt,\r\n" +
                "        LeftCommand = 310,\r\n" +
                "        LeftApple = 310,\r\n" +
                "        LeftWindows,\r\n" +
                "        RightCommand = 309,\r\n" +
                "        RightApple = 309,\r\n" +
                "        RightWindows = 312,\r\n" +
                "        AltGr,\r\n" +
                "        Help = 315,\r\n" +
                "        Print,\r\n" +
                "        SysReq,\r\n" +
                "        Break,\r\n" +
                "        Menu,\r\n" +
                "        Mouse0 = 323,\r\n" +
                "        Mouse1,\r\n" +
                "        Mouse2,\r\n" +
                "        Mouse3,\r\n" +
                "        Mouse4,\r\n" +
                "        Mouse5,\r\n" +
                "        Mouse6,\r\n" +
                "        JoystickButton0,\r\n" +
                "        JoystickButton1,\r\n" +
                "        JoystickButton2,\r\n" +
                "        JoystickButton3,\r\n" +
                "        JoystickButton4,\r\n" +
                "        JoystickButton5,\r\n" +
                "        JoystickButton6,\r\n" +
                "        JoystickButton7,\r\n" +
                "        JoystickButton8,\r\n" +
                "        JoystickButton9,\r\n" +
                "        JoystickButton10,\r\n" +
                "        JoystickButton11,\r\n" +
                "        JoystickButton12,\r\n" +
                "        JoystickButton13,\r\n" +
                "        JoystickButton14,\r\n" +
                "        JoystickButton15,\r\n" +
                "        JoystickButton16,\r\n" +
                "        JoystickButton17,\r\n" +
                "        JoystickButton18,\r\n" +
                "        JoystickButton19,\r\n" +
                "        Joystick1Button0,\r\n" +
                "        Joystick1Button1,\r\n" +
                "        Joystick1Button2,\r\n" +
                "        Joystick1Button3,\r\n" +
                "        Joystick1Button4,\r\n" +
                "        Joystick1Button5,\r\n" +
                "        Joystick1Button6,\r\n" +
                "        Joystick1Button7,\r\n" +
                "        Joystick1Button8,\r\n" +
                "        Joystick1Button9,\r\n" +
                "        Joystick1Button10,\r\n" +
                "        Joystick1Button11,\r\n" +
                "        Joystick1Button12,\r\n" +
                "        Joystick1Button13,\r\n" +
                "        Joystick1Button14,\r\n" +
                "        Joystick1Button15,\r\n" +
                "        Joystick1Button16,\r\n" +
                "        Joystick1Button17,\r\n" +
                "        Joystick1Button18,\r\n" +
                "        Joystick1Button19,\r\n" +
                "        Joystick2Button0,\r\n" +
                "        Joystick2Button1,\r\n" +
                "        Joystick2Button2,\r\n" +
                "        Joystick2Button3,\r\n" +
                "        Joystick2Button4,\r\n" +
                "        Joystick2Button5,\r\n" +
                "        Joystick2Button6,\r\n" +
                "        Joystick2Button7,\r\n" +
                "        Joystick2Button8,\r\n" +
                "        Joystick2Button9,\r\n" +
                "        Joystick2Button10,\r\n" +
                "        Joystick2Button11,\r\n" +
                "        Joystick2Button12,\r\n" +
                "        Joystick2Button13,\r\n" +
                "        Joystick2Button14,\r\n" +
                "        Joystick2Button15,\r\n" +
                "        Joystick2Button16,\r\n" +
                "        Joystick2Button17,\r\n" +
                "        Joystick2Button18,\r\n" +
                "        Joystick2Button19,\r\n" +
                "        Joystick3Button0,\r\n" +
                "        Joystick3Button1,\r\n" +
                "        Joystick3Button2,\r\n" +
                "        Joystick3Button3,\r\n" +
                "        Joystick3Button4,\r\n" +
                "        Joystick3Button5,\r\n" +
                "        Joystick3Button6,\r\n" +
                "        Joystick3Button7,\r\n" +
                "        Joystick3Button8,\r\n" +
                "        Joystick3Button9,\r\n" +
                "        Joystick3Button10,\r\n" +
                "        Joystick3Button11,\r\n" +
                "        Joystick3Button12,\r\n" +
                "        Joystick3Button13,\r\n" +
                "        Joystick3Button14,\r\n" +
                "        Joystick3Button15,\r\n" +
                "        Joystick3Button16,\r\n" +
                "        Joystick3Button17,\r\n" +
                "        Joystick3Button18,\r\n" +
                "        Joystick3Button19,\r\n" +
                "        Joystick4Button0,\r\n" +
                "        Joystick4Button1,\r\n" +
                "        Joystick4Button2,\r\n" +
                "        Joystick4Button3,\r\n" +
                "        Joystick4Button4,\r\n" +
                "        Joystick4Button5,\r\n" +
                "        Joystick4Button6,\r\n" +
                "        Joystick4Button7,\r\n" +
                "        Joystick4Button8,\r\n" +
                "        Joystick4Button9,\r\n" +
                "        Joystick4Button10,\r\n" +
                "        Joystick4Button11,\r\n" +
                "        Joystick4Button12,\r\n" +
                "        Joystick4Button13,\r\n" +
                "        Joystick4Button14,\r\n" +
                "        Joystick4Button15,\r\n" +
                "        Joystick4Button16,\r\n" +
                "        Joystick4Button17,\r\n" +
                "        Joystick4Button18,\r\n" +
                "        Joystick4Button19,\r\n" +
                "        Joystick5Button0,\r\n" +
                "        Joystick5Button1,\r\n" +
                "        Joystick5Button2,\r\n" +
                "        Joystick5Button3,\r\n" +
                "        Joystick5Button4,\r\n" +
                "        Joystick5Button5,\r\n" +
                "        Joystick5Button6,\r\n" +
                "        Joystick5Button7,\r\n" +
                "        Joystick5Button8,\r\n" +
                "        Joystick5Button9,\r\n" +
                "        Joystick5Button10,\r\n" +
                "        Joystick5Button11,\r\n" +
                "        Joystick5Button12,\r\n" +
                "        Joystick5Button13,\r\n" +
                "        Joystick5Button14,\r\n" +
                "        Joystick5Button15,\r\n" +
                "        Joystick5Button16,\r\n" +
                "        Joystick5Button17,\r\n" +
                "        Joystick5Button18,\r\n" +
                "        Joystick5Button19,\r\n" +
                "        Joystick6Button0,\r\n" +
                "        Joystick6Button1,\r\n" +
                "        Joystick6Button2,\r\n" +
                "        Joystick6Button3,\r\n" +
                "        Joystick6Button4,\r\n" +
                "        Joystick6Button5,\r\n" +
                "        Joystick6Button6,\r\n" +
                "        Joystick6Button7,\r\n" +
                "        Joystick6Button8,\r\n" +
                "        Joystick6Button9,\r\n" +
                "        Joystick6Button10,\r\n" +
                "        Joystick6Button11,\r\n" +
                "        Joystick6Button12,\r\n" +
                "        Joystick6Button13,\r\n" +
                "        Joystick6Button14,\r\n" +
                "        Joystick6Button15,\r\n" +
                "        Joystick6Button16,\r\n" +
                "        Joystick6Button17,\r\n" +
                "        Joystick6Button18,\r\n" +
                "        Joystick6Button19,\r\n" +
                "        Joystick7Button0,\r\n" +
                "        Joystick7Button1,\r\n" +
                "        Joystick7Button2,\r\n" +
                "        Joystick7Button3,\r\n" +
                "        Joystick7Button4,\r\n" +
                "        Joystick7Button5,\r\n" +
                "        Joystick7Button6,\r\n" +
                "        Joystick7Button7,\r\n" +
                "        Joystick7Button8,\r\n" +
                "        Joystick7Button9,\r\n" +
                "        Joystick7Button10,\r\n" +
                "        Joystick7Button11,\r\n" +
                "        Joystick7Button12,\r\n" +
                "        Joystick7Button13,\r\n" +
                "        Joystick7Button14,\r\n" +
                "        Joystick7Button15,\r\n" +
                "        Joystick7Button16,\r\n" +
                "        Joystick7Button17,\r\n" +
                "        Joystick7Button18,\r\n" +
                "        Joystick7Button19,\r\n" +
                "        Joystick8Button0,\r\n" +
                "        Joystick8Button1,\r\n" +
                "        Joystick8Button2,\r\n" +
                "        Joystick8Button3,\r\n" +
                "        Joystick8Button4,\r\n" +
                "        Joystick8Button5,\r\n" +
                "        Joystick8Button6,\r\n" +
                "        Joystick8Button7,\r\n" +
                "        Joystick8Button8,\r\n" +
                "        Joystick8Button9,\r\n" +
                "        Joystick8Button10,\r\n" +
                "        Joystick8Button11,\r\n" +
                "        Joystick8Button12,\r\n" +
                "        Joystick8Button13,\r\n" +
                "        Joystick8Button14,\r\n" +
                "        Joystick8Button15,\r\n" +
                "        Joystick8Button16,\r\n" +
                "        Joystick8Button17,\r\n" +
                "        Joystick8Button18,\r\n" +
                "        Joystick8Button19";
        
        for (String key : allKeys.split("\r\n")) {
            key = key.split("[,=]")[0].trim();
            System.out.format("\"%1$s\" GetKeyDown if%n" +
                    "    \"%1$s\" @onKeyDown%n" +
                    "endif%n%n", key);
        } //*/

    }
}
