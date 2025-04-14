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

  // Reserved for games
  GAME_MOVE,
  GAME_INSTANTIATE,
  GAME_JOIN

}
