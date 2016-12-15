# ConnectFour
A GUI and AI for the board game Connect Four.

### The Game
Connect Four is a popular board game played on a 6x7 grid where players take turns dropping checkers into the lowest unoccupied space in a column. The first player to get four checkers in a row wins.

### The GUI
The user can click anywhere in the column they want to place their piece in. The AI will respond. The last checker to be placed is highlighted. If either player wins or if there is a draw, a the player is notified by a pop-up, and when it is accepted, the board is reset.

### The AI
The AI is smart enough to consistently beat probably over 95% of people. This is accomplished using the Mini-Max algorithm to select the move that will get it to at least the best outcome it can guarantee in a certain number of moves. It judges the outcomes based off of a super secret sauce heuristic. Irrelevant branches of the search space are removed from consideration using Alpha-Beta pruning, which square-root's the exponential runtime, allowing it to reach a search depth of 7 nearly instantly on my computer.

![alt-text](/ScreenShots/Cross.png)

![alt-text](/ScreenShots/BlackWins.png)
