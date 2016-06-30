package de.hsb.ants;

/**
 * An enum of actions that may be sent as the type parameter of the json
 * message to the antworld service.
 * 
 * @author Daniel
 *
 */
public enum Action {
	ANT_ACTION_UP, ANT_ACTION_DOWN, ANT_ACTION_LEFT, ANT_ACTION_RIGHT, ANT_ACTION_COLLECT, ANT_ACTION_DROP, ANT_ACTION_LOGIN;
}
