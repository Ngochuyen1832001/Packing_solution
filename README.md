# Packing_solution
Package Selection Guide (using Dynamic Programming)

1. Problem motivation:
Suppose you are a shipper working at an express company X. A day, you are given a list of packages you have to deliver; however you can discard packages that you cannot load due to overload (those one your colleagues who have larger cars will deliver). It is impossible to carry all the packages in 1 turn only, so you have to split them into turns to deal with them. But choosing what to deliver in order to maximize the total value in every turn is not easy, and you have to find a method to come up with this challenge.

2. Algorithm overview:
The most effective approach is using 0/1 knapsack algorithm to find the maximum value for every turn.
2.1. 0/1 Knapsack Algorithm:
0/1 Knapsack algorithm is a dynamic programming algorithm which is used for maximizing the value of item chosen based on the capacity limit. In the algorithm, “0/1” annotates every item can only be chosen once (1), or not (0). So only one item per type can be chosen, not more.
The original 0/1 Knapsack algorithm only maximize the value; however, in this case, the algorithm itself can be extended to find that which item can be chosen for the maximum value. As a result, the 0/1 Knapsack algorithm are splitted into 2 main phases: forward phase and backward phase, to get the selection array corresponding to items.
a. Forward phase:
In the forward phase, a memory table is created for maximizing value. Rows represent items, and columns represent capacity cases. The capacity cases are increased from 0 to the maximum capacity (c) (and increase via the step size).
Note that the first row of the table shows the empty item (useful for considering the first one), the first column shows the capacity case equals 0; and in this case, the capacity step size equals to 1 kg, so the number of capacity cases equals to capacity c (the step size may be varied when applied to the project data). Therefore, the size of the table is (number of items + 1) x (number of capacity cases + 1).
The objective of the table is the value of each element is the maximum value of considered items in corresponding capacity case.
When considering every item, if the capacity case is not enough for storing the item, the value of the corresponding element is updated via the element above it. Otherwise, the value is the maximum of, the sum of the value of considering item plus the max value of other considered items (the value of element in above row and capacity case is the current case minus the weight of the considering item), and the element above considering element.
Keep this step until the last element of the table (the element in the bottom right of the table), the maximum value found. 
 

The forward phase finishes, and the algorithm switch to the backward.
b. Backward phase:
The target of this phase is finding which items are selected based on the maximum value (the last value of the table). In this case, a selection array (that have same size as the weight and value) is initialized.
Starting at the position of the maximum value, if the value above is the same as the current, that means the corresponding item is not selected (selection value remains 0), and the new position is upper one row and capacity case remains the same.
But if the value above is different, that means the corresponding item is selected, and the selection value of the item is set to 1. Then the new position is upper one row, and new capacity case is the current case minus the weight of just-selected item.
Repeat these steps until the position up to the first row (row of the empty item), the backward phase completes, and the final selection array is returned.
 
2.2. Working with project’s data:
There are some changes in the data. First, the weight values are floating-point numbers, and as capacity varies, the capacity input value can also be floating-point. Moreover, weight values can be a small number. 
Therefore, there are minor changes in the algorithm. One of them is capacity step size cannot be 1 kg as in the previous case; instead, the step size must depend on capacity input.
There are 3 cases of the capacity step size:
•	0.01 kg if capacity is smaller than 3 kg.
•	0.1 kg if capacity is in range 3 - 30 kg.
•	1 kg if capacity is larger than 30 kg.
Therefore, capacity cases mostly are real numbers so they cannot be accessed directly. Instead, 2 domains are defined: Capacity domain (in real numbers), and step domain (in integers).
Step numbers can be converted to corresponding capacity case values and vice versa. Steps to capacity cases conversion can be done by multiplying step numbers with capacity step size (as defined above), and capacity case to steps conversion can be done by dividing capacity cases by capacity step size (the output is the floor value of the quotient).
However, the main drawback is capacity cases might not be totally fit with items, thus affects to space optimization.

3. Method steps:
Data input: package data (from a csv file), capacity value (from keyboard).
As overloading packages can be discarded, the first step is filtering and sorting the package data. After filtering and sorting, the data is only loadable packages.
Next, in every turn:
•	Use 0/1 Knapsack algorithm (as discussed above) to get the selection array, which corresponding to packages. (output: selection array)
•	Then, use this selection array to display which packages should be picked up for that turn.
•	Discard packages that are selected to prepare data for the next turn. (output: new package data)
Repeat those 3 steps until there are no packages left (means all packages are put into turns for delivery).

