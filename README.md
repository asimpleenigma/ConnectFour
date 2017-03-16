# ConnectFour
A GUI and AI for the board game Connect Four.

### The Game
Connect Four is a popular board game played on a 6x7 grid where players take turns dropping checkers into the lowest unoccupied space in a column. The first player to get four checkers in a row wins.

### The GUI
The game can be played human vs human, human vs AI, or even AI vs AI. The user can also select the AI difficultly based on the number of moves it looks ahead. The user can click anywhere in the column they want to place their piece in. The last checker to be placed is highlighted. Press the Back button to go back one move, or the Reset button to clear the board. If either player wins or if there is a draw, the player is notified by a pop-up, and when it is accepted, the board is reset.

### The AI
The AI has consistently beat every human I have gotten to play it. This is accomplished using the Mini-Max algorithm to select the move that will get it to at least the best outcome it can guarantee in a certain number of moves. It judges the outcomes based off of my super secret sauce heuristic. Irrelevant branches of the search space are removed from consideration using Alpha-Beta pruning, which square-root's the exponential runtime, allowing it to reach a search depth of 7 in less than a second on my laptop.

The code is organized so that any class that implements the Player interface can easily be added as another agent that the user can select to play the game. My hope is that I can have a friendly connect four AI arms race with my friends, battling our AI's against each other.

![alt-text](/ScreenShots/BlackWin.png)
 
