# Chatroom
For Computer Networks

At the moment, we have the ability to send messages as a shout to everyone connected to the server chatroom. The ability to send
  directed messages and blocked messages is close to completion, they just need the string manipulation done in order to direct
  the messages to the correct thread. Then, all we will have is the file sending.

In order to run this program, first compile all three files using 'javac \*.java'. This is confirmed to work on Java JDK 7, 8, and 9.

Next, from ther server terminal, run 'java server'. This will be your server instance.

From the client terminals, run 'java client <IP address of server>'. In the video, I show the client running from three other
  instances. Two of them are from my machine, one using 127.0.0.1 (localhost), and the other using my computer's IP on my home
  network. The third client is from a laptop in another room, also using the IP of my home computer to connect.
  
Once you have connected, you will be prompted for a login name. This currently can be any string, as long as another user does not
  currently use that string as THEIR login name.

After this, you are in the chatroom and ready to chat with anyone else! There are a handfull of debugging statements currently in 
  the server, and eventually these will be pushed to a log. Also, currently, everything is via Command Prompt. We intend on adding
  a full GUI, which should dramatically help the look and feel of the program.
  
Thank you!
Grant Coles
Kris Hale
