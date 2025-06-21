package Message;

public enum Type {

  // Reserved for username
  USERNAME_PROPAGATE,
  USERNAME_UPDATE,

  TEXT,
  CHATROOM,

  // Reserved for signalling for setting up p2p
  SIGNAL,
  SIGNAL_ACK,

  // Reserved for server use only
  SERVER,
  ONLINE_USERS,
  ERROR,
  SUCCESS,

  // Reserved for games
  // A user sends a move to the game client
  GAME_MOVE,
  // Request for server to create a game client
  GAME_INSTANTIATE,
  // User sends to server to join a server
  GAME_JOIN,
  // Will be the response of a successful join or instantion telling the client to create the window
  GAME_JOIN_SUCCESS,
  // Just a text message
  GAME_NOTIFICATION,
  // User sends to the server when exiting a game
  GAME_LEAVE,
  GAME_INVITE,
  // The game will return its state
  GAME_STATE,
  GAME_START

}
