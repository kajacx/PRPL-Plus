package com.prplplus.shipconstruct;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;

import com.prplplus.gui.OffsetIterable;

public class Hull {

    //@formatter:off
    
    public static final int HULL_SPACE           = 0x0000;
    
    public static final int HULL_BLOCK           = 0x0001; 
    public static final int HULL_CORNER_LB       = 0x0002;// Left bot corner ◺
    public static final int HULL_CORNER_RB       = 0x0003;
    public static final int HULL_CORNER_RT       = 0x0004;
    public static final int HULL_CORNER_LT       = 0x0005;
    public static final int HULL_SPIKE_B         = 0x0006;
    public static final int HULL_SPIKE_R         = 0x0007;
    public static final int HULL_SPIKE_T         = 0x0008;
    public static final int HULL_SPIKE_L         = 0x0009;
    
    public static final int HULL_ARMOR_MASK      = 0x000A; //don't use this, it's for rendering only
    
    public static final int HULL_ARMOR_BLOCK     = 0x000B;
    public static final int HULL_ARMOR_CORNER_LB = 0x000C;
    public static final int HULL_ARMOR_CORNER_RB = 0x000D;
    public static final int HULL_ARMOR_CORNER_RT = 0x000E;
    public static final int HULL_ARMOR_CORNER_LT = 0x000F;
    public static final int HULL_ARMOR_SPIKE_B   = 0x0010;
    public static final int HULL_ARMOR_SPIKE_R   = 0x0011;
    public static final int HULL_ARMOR_SPIKE_T   = 0x0012;
    public static final int HULL_ARMOR_SPIKE_L   = 0x0013;
    
    //@formatter:on

    public static Image[] hullImages = new Image[HULL_ARMOR_SPIKE_L + 1];

    static {
        for (Field f : Hull.class.getDeclaredFields()) {
            if (f.getName().startsWith("HULL")) {
                try {
                    String name = f.getName().toLowerCase();
                    int value = f.getInt(null);

                    Image i = ImageIO.read(new File("img/hull/" + name + ".png"));
                    hullImages[value] = i;
                } catch (Exception e) {
                    System.out.println("Exception when reading: " + f.getName());
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    public static int withArmor(int hull, boolean armor) {
        if (armor && hull >= HULL_BLOCK && hull <= HULL_SPIKE_L) {
            return hull + HULL_ARMOR_BLOCK - HULL_BLOCK;
        }
        if (!armor && hull >= HULL_ARMOR_BLOCK && hull <= HULL_ARMOR_SPIKE_L) {
            return hull - HULL_ARMOR_BLOCK + HULL_BLOCK;
        }
        return hull;
    }

    public static int rotateCCW(int hull) {
        //fuck it, this is way easier anyway
        //@formatter:off
        switch(hull) {
        case HULL_CORNER_LB: return HULL_CORNER_RB;
        case HULL_CORNER_RB: return HULL_CORNER_RT;
        case HULL_CORNER_RT: return HULL_CORNER_LT;
        case HULL_CORNER_LT: return HULL_CORNER_LB;
        case HULL_SPIKE_B: return HULL_SPIKE_R;
        case HULL_SPIKE_R: return HULL_SPIKE_T;
        case HULL_SPIKE_T: return HULL_SPIKE_L;
        case HULL_SPIKE_L: return HULL_SPIKE_B;
        case HULL_ARMOR_CORNER_LB: return HULL_ARMOR_CORNER_RB;
        case HULL_ARMOR_CORNER_RB: return HULL_ARMOR_CORNER_RT;
        case HULL_ARMOR_CORNER_RT: return HULL_ARMOR_CORNER_LT;
        case HULL_ARMOR_CORNER_LT: return HULL_ARMOR_CORNER_LB;
        case HULL_ARMOR_SPIKE_B: return HULL_ARMOR_SPIKE_R;
        case HULL_ARMOR_SPIKE_R: return HULL_ARMOR_SPIKE_T;
        case HULL_ARMOR_SPIKE_T: return HULL_ARMOR_SPIKE_L;
        case HULL_ARMOR_SPIKE_L: return HULL_ARMOR_SPIKE_B;
        default: return hull;
        }
        //@formatter:on
    }

    public static int rotateCW(int hull) {
        //ain't nobody got time for this
        return rotateCCW(rotateCCW(rotateCCW(hull)));
    }

    public static int getOffsetDirection(int hull) {
        //@formatter:off
        switch(withArmor(hull, false)) {
        case HULL_CORNER_LB: return OffsetIterable.DIAG_LB;
        case HULL_CORNER_RB: return OffsetIterable.DIAG_LT;
        case HULL_CORNER_RT: return OffsetIterable.DIAG_LB;
        case HULL_CORNER_LT: return OffsetIterable.DIAG_LT;
        case HULL_SPIKE_B: return OffsetIterable.HORIZONTAL;
        case HULL_SPIKE_R: return OffsetIterable.VERTICAL;
        case HULL_SPIKE_T: return OffsetIterable.HORIZONTAL;
        case HULL_SPIKE_L: return OffsetIterable.VERTICAL;
        default: return OffsetIterable.FULL;
        //@formatter:on
        }
    }
}
