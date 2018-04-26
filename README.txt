ECEN CS 4283 Computer Networks
Grant Coles, Kris Hale

First, start up the server on the host computer using "java server"
and input a server password when prompted.

Next, start up the client using "java client <host IP address>"
The client will ask for the server password until the correct
password is entered. Then, select a username that is not taken
on the current server and does not contain any spaces.

Once logged in, instructions on sending messages and files
on the server will be sent to the new user.

To send a server wide message, just type out your message and hit enter
To send a direct message, type '/msg <username> <message>
To send a server wide file, type '/file <filepath>
To send a direct message file, type '/file /username <username> <filepath>
To send a blacklisted message, type '/blk <username> <message>

When finished, simply exit the client and close the server.
