# Search_for_Room_Scheduling

This program is used to perform a scheduling strategy when given a number of buildings, rooms, and courses.  This program uses the following search strategies:
1. Simulated Annealing
2. Backtracking

## Running the Program
In order to run the program, enter the following command in the terminal:

```
java -jar SearchRoomScheduling.jar buildings rooms courses timeLimit algorithm seed 
```
- buildings is an integer value for the number of buildings to have
- rooms is an integer value for the number of rooms to have
- courses is an integer value for the number of courses to have
- timeLimit is an integer value for how long the algorithm has to complete the search
- algorithms is a command to indicate which search to run
  - 0 runs the native baseline
  - 1 runs simulated annealing
  - 2 runs backtracking
  - 3 runs minimum backtracking
  - 4 runs maximum backtracking
- seed is long value 
  
## Note
The temperature value for simulated annealing is modified in the Main.java file