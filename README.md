![Parley banner](./assets/images/banner.png)

# Parley-v2

This is a networked communication application built in Java, designed to support real-time text messaging, peer-to-peer (P2P) file transfers and video streaming between clients, and online turn based games such as Blackjack. It uses a central server for message relaying, user coordination, and peer discovery enabling direct communication between clients for media exchange.

The chat server supports multiple clients by using Java’s multithreading, with each client handled in its own thread. Messages are added to a shared, thread-safe queue and processed separately, decoupling communication and improving scalability and responsiveness.

The base of the project was a group project for CS313, however I decided to take it a step further by expanding the scope of the project, improving code quality, enhancing client/server messenging lifecycle and adding game lobbies like Blackjack.

## Features

- Username & username changing
- Chatroom
- Direct messaging
- P2P file transfers, video streams & video calls
- Game lobbies, only
- Multithreaded server
- Expandable/Adaptable codebase
  - Can expand server to support clients over different protocol (currently only tcp)
  - Integration of more game engines

## Screenshots

### Login

![Login](./assets/screenshots/login.png)

### Chatroom

![Chatroom](./assets/screenshots/chatroom.png)

### Update username

![Update username](./assets/screenshots/update_username.png)

### P2P handshake

![P2P handshake](./assets/screenshots/p2p_handshake.png)

### Select file

![Select file](./assets/screenshots/select_file.png)

### Receive/view file

![Receive file](./assets/screenshots/receive_view_file.png)

### Video file stream

![Viedo file stream](./assets/screenshots/video_file_stream.png)

### Video call

![Viedo call](./assets/screenshots/videocall.png)

### Game picker

![Game picker](./assets/screenshots/game_picker.png)

### Invite to game

![Invite to game](./assets/screenshots/invite_to_game.png)

### Blackjack

![Blackjack](./assets/screenshots/blackjack.png)

### Blackjack win

![Blackjack win](./assets/screenshots/blackjack_win.png)

## Parley-v1 Original Contributors

- [Calum Cardownie](https://github.com/calumvc)
- [Mux Diven](https://github.com/muxdiven)
- [Fraser Patrick](https://github.com/fraserpatrick)
- Kieran Ballard
- Moray Blackwood
