package edu.wing.yytang.common;
import edu.wing.yytang.common.StateMachine.STATE;
/**
 * Created by yytang on 2/9/17.
 */

public interface StateObserver {
    // the observer receives a notification when the connection state changes
    public void onStateChange(STATE oldState, STATE newState, int resID);
}
