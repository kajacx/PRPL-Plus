package com.prplplus.shipconstruct.parts;

public class PartSelection {
    public static final int STATE_IDLE = 0; //this is completely unselected and inactive
    public static final int STATE_READY = 1; //the "Select" button has been clicked, waiting for select
    public static final int STATE_SELECTING = 2; //The selection drag has begun
    public static final int STATE_SELECTED = 3; //The selection is complete

    public int state = STATE_IDLE;

    public int fromX, fromY;
    public int toX, toY;

    //sets both the data and state
    public void setFromSelection(int fromX, int fromY) {
        this.fromX = fromX;
        this.fromY = fromY;

        state = STATE_SELECTING;
    }

    //sets both the data and state, and ensures that from < to
    public void setToSelection(int toX, int toY) {
        this.toX = Math.max(this.fromX, toX);
        this.toY = Math.max(this.fromY, toY);

        this.fromX = Math.min(this.fromX, toX);
        this.fromY = Math.min(this.fromY, toY);

        state = STATE_SELECTED;
    }
}
