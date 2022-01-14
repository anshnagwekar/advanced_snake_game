# Ansh's Advanced Snake Game (CIS 120 Game Project) #

This project is my advanced update on the snake game with moving apples, time-sensitive events, advanced scoring, etc. Read the project writeup (transcribed below) for a full description!

### Core Concepts ###

List the four core concepts, the features they implement, and why each feature is an appropriate use of the concept.

  - 2D Arrays: I used 2D arrays in order to govern Apple placement in the game grid layout. Essentially, every time an Apple is added or removed, the 2D Array (which stores String values) will either have the color of the Apple or an empty String respectively. Additionally, we check to ensure that a new Apple that is generated will not be drawn overan existing Apple. Moreover, the snake head's coordinates correspond to the indices of the 2D array, so it is easy to check for collisions and update the 2D array as necessary.

  - File I/O: I keep a CSV file within the program that displays a leaderboard of people's best scores (for all time). This CSV file holds username, score, number of apples eaten, and snake color during play. This is just a raw leaderboard (thus it is possible for one user to have multiple scores on the leaderboard). This leaderboard is similar to one you would find on an arcade game console. Moreover, at the end of each game, the user has the option to record the score to the leaderboard if they wish, and upon recording the score, the updated leaderboard will be displayed. I also have implemented a "pause" function that enables the user to pause the game and revisit it anytime to pick up where they left off (as long as they press "Quit" instead of exiting the window after pausing. The pause methods will save the entire game state to a text file and load it once resumed, even if the game was quited (do by pressing the "Load" button on the home screen).

  - Inheritance/Subtyping: I use an "Apples" abstract class which will be extended by every type of apple, and each of the subtypes of this class will have different properties based on their special power. Each apple will have multiple shared methods such as getting their coordinates or their color, but there are a few Apples that have different implementations of certain methods. For example, the "onCollision" method will adjust the score and coordinates as required by every Apple, but certain Apples will require more actions. For example, the red apple and green apple randomly add or remove brown apples from the game on collision. The blue and brown apples change the frames per second of the game. The reason that Apple is an abstract class is that it should never be instantiated since the game should never just contain a regular Apple; every Apple must be one of the valid types of Apple.

  - Collections: I use an ArrayList to store the Apples that are in play during the game. The reason for using the ArrayList data structure is that it is faster for looking up elements (which will be important when removing an apple from the list if a certain condition is triggered) and it enables us to dynamically change the size of the list (since we need to be able to add or remove apples when necessary).

### Implementation ###

Provide an overview of each of the classes in your code, and what their function is in the overall game.

  - AdvancedSnakeGame: This class just displays the home screen for the game. It displays a picture of a snake (when, if clicked, will display the instructions for the game), and has three buttons ("Start," which starts the game, "Load", which loads the previous paused game (if any), and "Leaderboard", which displays the leaderboard).

  - Leaderboard: This class simply reads the entries of the leaderboard.csv file, orders them by highest score, and displays the scores using JTable GUI. It allows for duplicate entries (similar to arcade leaderboards).

  - Apple: This abstract class defines the basic "Apple" that is consumed by the snake during the game. It contains all the methods that all the Apples need (i.e. getter methods, drawing logic, basic information for onCollision) as well as the protected variables that all subtypes of Apple should have access to. It has an in-built random coordinate generator that will be utilized when generating an Apple or once an apple has been consumed (relocate the old apple to a different coordinate).

  - RedApple: This class holds the information and game logic for the red apple. The red apple is your standard apple. It lengthens your snake by 1 and adds 1 to your score. However, every time you eat a red apple, there is a 50% chance that you spawn a brown apple into the game.

  - BrownApple: This class holds the information and game logic for the brown apple. The brown apple is the rotten apple. It is stationary, but if you eat the apple, it will speed up the game for 5 seconds. It lengthens your snake by 1 and decrease score by 1.

  - GreenApple: This class holds the information and game logic for the green apple. The green apple is a bonus apple. It moves around the screen, either horizontally or vertically. Since it is harder to eat, it adds 2 to your score and only lengthens your snake by 1. Additionally, every time you eat a green apple, there is a 25% chance that you remove an existing (if any) brown apples.

  - BlueApple: This class holds the information and game logic for the blue apple. The blue apple is a freeze apple. It is stationary, but if you eat the apple, it will slow the game down for 5 seconds. It lengthens your snake by 1 and adds 1 to your score. It should be used to negate the effects of the brown apple or to make it easier to catch green apples.

  - GameFrame: The GameFrame hosts the entire game graphics and contains the logic for the snake as well. The GameFrame handles all the in-game events such as movement of snake as well as iterates through all the available Apples to handle any potential collisions and generation/removal of new apples. It also checks for pause game events (via the Enter key) and saves/loads the state space. It also manages a timer (and a sub timer for the special apples) for the game that continuously updates the apples and snake positions as well as score, number of apples eaten, etc. Lastly, it handles end game events (such as crashing into walls or running into boundary) and displays the final score, apples eaten, and other relevant information (as well as giving the user options to play again, quit, or record their score).

Were there any significant stumbling blocks while you were implementing your game (related to your design, or otherwise)?
  
  - There were significant stumbling blocks with regard to handling collisions between the snake and the green apple. The issue was that due to the delay rate, the snake could sometimes glide over the moving apples since in the start frame, the snake could be at x coordinate 4 while the apple is at 5, and in the next frame, the snake is at 5 and the apple is at 4. Thus, the intersection could possibly happen between frames. To handle this, I had a specific boolean statement that checked to ensure that if the x coordinate of the snake is within 1 of the x coordinate of apple and every other required condition is met, then we can say assume that the collision happens in the next frame and perform the necessary actions. Moreover, there were issues with the apple sometimes being drawn off the screen due to slight discrepancies in the random number generator and the coordinate plane. To resolve this, I just constrained apple generation to be within 1 unit of the bounds (by decreasing the range of the random number generator and adding a constant).

Evaluate your design. Is there a good separation of functionality? How well is private state encapsulated? What would you refactor, if given the chance?

  - I believe my design was quite efficient but might not be the easiest to follow. I believe this is because instead of using a "Snake" class as would be expected of a game with multiple objects, I decided to build the snake into the GameFrame class. However, this is more efficient as we can draw the snake faster during every delay and we can just use the "drawing coordinates" (snakeX and snakeY) for every part of the snake instead of having to convert them everytime like we do for the Apples.  I believe the private state is well encapsulated as the required classes have access to the required variables and methods as needed for the game (for example, all Apple variables are protected so that subtypes of Apple can access them, and the GameFrame object is passed by reference to every Apple so that they can add/subtract Apples if needed in their onCollision method). One aspect that I would refactor is that I would use a TreeSet of Apples instead. This is because I would be able to leverage the invariant that there can be no duplicate elements in the set in order to simplify the game logic that checks to ensure that every time an Apple is added, it is not drawing over another Apple. Essentially, I would have to implement a compareTo method from the Comparable Interface in order to check that if a snake has same coordinates as another apple. If it does, I would generate new random coordinates until they are different. This also might eliminate the need for a 2D array since we know that if we add an Apple to the set, it will not have the same coordinates as another apple, so I can directly just check to see if the head of snake has the same coordinates as any of the Apples in the "AppleSet" and perform the actions accordingly.

  - Also note the use of protected variables in the Apple class. In this use case, most of my subclasses share the same variables: thus it would make logical sense to use protected variables defined in the abstract class. Examples of variables that are shared are Apples color, label, x coordinates, y coordinates, etc. The alternative would be to make them private and then create setter/getter methods in the abstract class and use super(), which would be a lot more code and less efficient. Protected ensures only subclasses of Apple have access to the variables in Apple but it is not available publicly.

### External Resources ###

Cite any external resources (images, tutorials, etc.) that you may have used while implementing your game.

- I used the image of a snake from this link: https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.123rf.com%2Fphoto_94182637_snake-outline-illustration-black-serpent-isolated-on-a-white-background-vector-tattoo-design.html&psig=AOvVaw1KqcPIkRwguBKgjIThLFMG&ust=1638978759329000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCIjcx-iF0vQCFQAAAAAdAAAAABAJ

- I used Javadocs and Java Tutorials in order to understand how to use certain classes and Java Swing components that
were not covered in class (such as Timer, JTable, BorderLayout, GridLayout, JOptionPane).

- I used this tutorial to understand how to embed HTML into JOptionPane:
https://www.tabnine.com/code/java/methods/javax.swing.JOptionPane/setMessage
