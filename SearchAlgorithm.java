/*
 * Andrew Madrid, Alan Caldelas
 * CS 4320
 * HW3
 */
import java.util.ArrayList;
import java.util.Collections;

public class SearchAlgorithm {

  /* Simulated Annealing Algorithm
   * Author: Andrew Madrid
   * Apply the simulated annealing search strategy by generating a solution
   * and view neighbors until either a good solution has been found or the 
   * temperature has cooled.
   * Input:	SchedulingProblem problem	- The arguments of the problem (buildings, rooms, courses)
   * 		long deadline				- The deadline that the algorithm has to find a solution
   * 		int temp					- The initial temperature
   * Output: Schedule solution			- The schedule generated
   */
  public Schedule simulatedAnnealing(SchedulingProblem problem, long deadline, int temp) {

    // Create an empty solution
    Schedule solution = problem.getEmptySchedule();
    double bestScore = 0.0;
    
    // Create two ArrayList to populate and keep track of values
    ArrayList<Integer> currentSlot = new ArrayList<>();
    ArrayList<Integer> currentRoom = new ArrayList<>();
    for (int i = 0; i < problem.NUM_TIME_SLOTS; i++)
    {
        currentSlot.add(i);
    }
    for (int i = 0; i < problem.rooms.size(); i++)
    {
        currentRoom.add(i);
    }
    
    // Shuffles each of the lists to keep the first value random
    Collections.shuffle(currentSlot);
    Collections.shuffle(currentRoom);
    
    // Apply Simulated Annealing
    while (temp > 0)
    {
        Schedule currentSolution = problem.getEmptySchedule();
        ArrayList<Integer> randomRoom = new ArrayList<>(currentRoom);
        ArrayList<Integer> randomSlot = new ArrayList<>(currentSlot);
        
        // Move through the collection
        for (int i = 0; i < problem.NUM_TIME_SLOTS / 3; i++)
        {
            Collections.swap(randomSlot, (int)(Math.random()*problem.NUM_TIME_SLOTS), (int)(Math.random()*problem.NUM_TIME_SLOTS));
        }
        for (int i = 0; i < problem.rooms.size()/3; i++)
        {
            //System.out.println(randomRoom.size());
        	Collections.swap(randomRoom, (int)(Math.random()*problem.rooms.size()), (int)(Math.random()*problem.rooms.size()));
        }
        // Start assigning courses to rooms
        for (int i = 0; i < problem.courses.size(); i++)
        {
            Course c = problem.courses.get(i);
            boolean scheduled = false;
            for (int j = 0; j < c.timeSlotValues.length; j++)
            {
                if (scheduled) break;
                if (c.timeSlotValues[currentSlot.get(j)] > 0)
                {
                    for (int k = 0; k < problem.rooms.size(); k++)
                    {
                    	if (currentSolution.schedule[randomRoom.get(k)][randomSlot.get(j)] < 0)
                        {
                            currentSolution.schedule[randomRoom.get(k)][randomSlot.get(j)] = i;
                            scheduled = true;
                            break;
                        }
                    }
                }
            }
        }
        // Update the best value
        Double sum = problem.evaluateSchedule(currentSolution) - bestScore;
        if(sum > 0)
        {
            bestScore = problem.evaluateSchedule(currentSolution);
            solution = currentSolution;
            currentRoom = randomRoom;
            currentSlot = randomSlot;

        } 
        // Use the temperature to check with randomness if it passes
        else if(Math.exp(sum / temp) > Math.random())
        {
            currentRoom = randomRoom;
            currentSlot = randomSlot;
        }
        temp--;

    }
    return solution;
  }
  
  // Author: Alan Caldelas
  public Schedule BackTracking(SchedulingProblem problem, long deadline)
  {
      Schedule solution = problem.getEmptySchedule();
	//Recursive Backtracking will be done in a different method
	//Takes in solution and problem and size
      return RecBackTracking(solution,problem,0);
  }
  public Schedule RecBackTracking(Schedule sol,SchedulingProblem problem,int s)
  {
	//Base case
	if(problem.courses.size() == s)
      {
          return sol;
      }
	//Create tmp variable 
      Course tmp = problem.courses.get(s);
	//loop till we have gone through the slotvalues
      for (int i = 0; i < tmp.timeSlotValues.length; i++)
      {
          if (tmp.timeSlotValues[i] > -1)
          {
		//Loop through all the rooms
              for (int j = 0; j < problem.rooms.size(); j++)
              {
		    //Check if the schedule is open and place a class
                  if (sol.schedule[j][i] < 0)
                  {
                      sol.schedule[j][i] = s;
			//Start to look at the rest
                      Schedule tmp_solution = RecBackTracking(sol,problem, s = s + 1);
                      if(tmp_solution !=  null)
                      {
                          return sol;
                      }
                      sol.schedule[j][i] = -1;
                  }

              }
          }
      }
      return null;
  }

  //Author: Alan Caldelas
  //This is the code for backtracking using Minimum remaining values
  public Schedule MinBackTracking(SchedulingProblem problem, long deadline)
  {
      Schedule solution = problem.getEmptySchedule();
	//Create two here for monitoring
      int max_constraints[] = new int[problem.courses.size()];
      int constraints[] = new int[problem.courses.size()];
      for (int i = 0; i < problem.courses.size(); i++)
      {
          constraints[i]=i;
      }
    
	//Using bubble sort to sort and store index into constraints array
      int index = max_constraints.length;
      for (int i = 0; i < index-1; i++)
          for (int j = 0; j < index-i-1; j++)
              if (max_constraints[j] > max_constraints[j+1])
              {
		    //Swapping into the constraints
                  int tmp = max_constraints[j];
                  max_constraints[j] = max_constraints[j+1];
                  max_constraints[j+1] = tmp;
                  int tmp2 = constraints[j];
                  constraints[j] = constraints[j+1];
                  constraints[j+1] = tmp2;
              }
      return MinRecBackTracking(solution,problem,0,constraints);
  }
  public Schedule MinRecBackTracking(Schedule solution,SchedulingProblem problem,int s,int max_array[])
  {
	//Base case
	//All have been finished
      if (problem.courses.size() == s)
      {
          return solution;
      }
      Course tmp = problem.courses.get(max_array[s]);
      //Iteration on all time slotvals
      for (int i = 0; i < tmp.timeSlotValues.length; i++)
      {
	    //Checking if slot is there
          if (tmp.timeSlotValues[i] > 0)
          {
              for (int j = 0; j < problem.rooms.size(); j++)
              {
                  if (solution.schedule[j][i] < 0)
                  {
                      solution.schedule[j][i] = s;
                      Schedule tmp_sol = MinRecBackTracking(solution,problem, s = s + 1,max_array);
                      if(tmp_sol !=  null)
                      {
                          return solution;
                      }
                      solution.schedule[j][i] = -1;
                  }
              }
          }
      }
      return null;
  }
  
  // Author: Alan Caldelas
  //Using the degree heuristic
  public Schedule MaxBackTracking(SchedulingProblem problem, long deadline)
  {
      Schedule solution = problem.getEmptySchedule();
      
	//Create two array to monitor the constraints
      int max_constraints[] = new int[problem.courses.size()];
      int constraints[] = new int[problem.courses.size()];
      for (int i = 0; i < problem.courses.size(); i++)
      {
	    //fillin 
	    constraints[i]=i;
	    
      }
	//Bubble sort to get the classes with the constraints and store them in constraints
      int index = max_constraints.length;
      for (int i = 0; i < index-1; i++)
          for (int j = 0; j < index-i-1; j++)
              if (max_constraints[j] < max_constraints[j+1])
              {
		    //swapping into the constraints
                  int tmp = max_constraints[j];
                  constraints[j] = max_constraints[j+1];
                  constraints[j+1] = tmp;
                  int tmp2 = constraints[j];
                  constraints[j] = constraints[j+1];
                  constraints[j+1] = tmp2;
              }
      return MaxRecBackTracking(solution,problem,0,constraints);
  }
  public Schedule MaxRecBackTracking(Schedule solution,SchedulingProblem problem,int s,int max_array[])
  {
	//Base case check if the classes have finished
      if (problem.courses.size() == s)
      {
          return solution;
      }
      Course tmp = problem.courses.get(max_array[s]);
      //Iteration on every time slot that passes
      for (int i = 0; i < tmp.timeSlotValues.length; i++)
      {
	    //Checking if the slot is here
          if (tmp.timeSlotValues[i] > -1)
          {
              for (int j = 0; j < problem.rooms.size(); j++)
              {
		    //Check if it's open then place it
                  if (solution.schedule[j][i] < 0)
                  {
                      solution.schedule[j][i] = s;
                      //Recursive call
                      Schedule tmp_sol = MaxRecBackTracking(solution,problem, s = s + 1,max_array);
                      if(tmp_sol !=  null)
                      {
                          return solution;
                      }
                      solution.schedule[j][i] = -1;
                  }
              }
          }
      }
      return null;
  }
  
  // This is a very naive baseline scheduling strategy
  // It should be easily beaten by any reasonable strategy
  public Schedule naiveBaseline(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    for (int i = 0; i < problem.courses.size(); i++) {
      Course c = problem.courses.get(i);
      boolean scheduled = false;
      for (int j = 0; j < c.timeSlotValues.length; j++) {
        if (scheduled) break;
        if (c.timeSlotValues[j] > 0) {
          for (int k = 0; k < problem.rooms.size(); k++) {
            if (solution.schedule[k][j] < 0) {
              solution.schedule[k][j] = i;
              scheduled = true;
              break;
            }
          }
        }
      }
    }

    return solution;
  }

}
