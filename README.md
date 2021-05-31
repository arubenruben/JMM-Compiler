# JMM Compiler

#### GROUP: 2a

* NAME1:César Nogueira, NR1: up201706828, GRADE1: 18.8, CONTRIBUTION1: 15%
* NAME2:Luís Marques , NR2: up201704093, GRADE2: 18.8, CONTRIBUTION2: 25%
* NAME3:José Guerra, NR3: up201706421, GRADE3: 18.8, CONTRIBUTION3: 30%
* NAME4:Rúben Almeida, NR4: up201704618, GRADE4: 18.8,  CONTRIBUTION4: 30%

GLOBAL Grade of the project: 18.8

### SUMMARY:

JMM Compiler is a compiler of the Java-- programming language. JMM Compiler establishes a complete pipeline receiving as input your .jmm high-level documents and outputs the execution of the corresponding instructions.

Like the most used compilers, JMM Compiler needs to deal with error possibility in all stages of its execution. This means that more than a simple compilation pipeline, JMM Compiler includes error handling and error displaying techniques to boost the user experience of the user and help in the task of finding errors in the code given as input.

### Compilation and Execution

In order to compile JMM Compiler you should run gradle build over your terminal

The recommended version of Java is Java 15. In order to test the program you can run gradle test over your terminal to execute the tests.

The compiler was tested in Windows and Debian based Linux distros.

### SYNTATIC ANALYSIS:

#### PARSER LOOKAHEAD:

Our project final version only includes one local lookahead of 2 to distinguish between variable declarations or the beginning of the expressions in the code. All other lookaheads were eliminated by converting the grammar provided for a more convenient form.

#### SYNTATIC ERRORS:

When handling errors in the syntactic phase, the jmm compiler fails at pretty much any error except those that appear in the while expression. In this situation, the parser can recover and continue parsing the code. Basically, the compiler forces the analysis to ignore the terminal symbols (tokens) until the closing ")" is found. This is done by counting the amount of "(" and ")" parenthesis when processing the tokens. Recovering from errors allows the compiler to present more meaningful errors from a single compilation.

In the presence of errors in the input program, the compiler does not abort execution immediately after the first error. Instead, it reports a list of errors so that the developer proceeds with their correction. For each error found, a report is generated, and after ten errors, the program aborts the program's execution.

### SEMANTIC ANALYSIS:

The semantic analysis was implemented according to the teacher's Tiago checklist from dia 6 of April in the Channel Project - General Topics. We used visitor pattern implementation designed by teacher João Bispo to perform this task.

The teacher divided the semantic analysis stage into three stages, and so did we in our project.

Our first visitor deals with the first topic in the checklist, **filling the symbol table** with the information regarding class name, class inheritance, class fields, methods specification and their return types. Also, as an enhancement, the first visitor checks for method overloading.

Our second visitor performs the tasks specified in the second chapter of the checklist, **Type Verification**. In this task, we evaluate that: 
* Operations are correctly performed with the correct types. 
* Mathematical operations only deal with integer values. 
* Array accesses are made using an integer index. 
* Boolean conditions are made using only boolean operators. 
* if and while conditions result in a boolean result. 

Our third visitor performs the third task of the already mentioned checklist, Method Verification. This step ensures that all method calls are made to existing class methods or that the class imports or extends other classes containing those methods. Furthermore, this visitor ensures that methods are called with the correct number of parameters and that their type is also correct.



### CODE GENERATION: 
The code generation step is divided into two steps, the OLLIR code, and the Jasmin translation step. They compress two different philosophies, so we separate them into two different sections.

#### OLLIR code generation:

The OLLIR code required a bottom-up traversal of the AST to generate the correspondent code. Initially, our goal was to use the Sethi Ullman algorithm to minimize the number of registers used. However, we soon understood that step was not very effective since it would not avoid a posterior Liveness Analysis to reuse registries. The Sethi Ullman algorithm would only help in arithmetic expressions, and as such, it was not implemented. 

Although Sethi Ullman was not 100% implemented, the philosophy to generate the code was almost the same, a first traversal to mark the nodes according to the number of instructions they require and then perform a post-order traversal according to the requirements of those nodes. The capabilities of Dismember the code are all compressed in the Dismember Class.

#### Jasmin code generation: 

To generate the Jasmin code, we use the class OllirClass, which contains all the information we need, such as the class name, class fields, and class methods. 
First, we generate the Jasmin code for the class name and the class fields. Then we start generating the Jasmin code for each of the class methods. For each method, when we are processing its instructions one by one, we are making sure to count the size of the stack to calculate the stack's max size at the end. The maximum number of virtual registers for the method is found by checking the size of the variable table that is present in the OllirClass object for that method. 


### Optimizations:

In this project, we implemented the optimizations related to the “-o” tag. When the program is executed with this flag :
* Loop template is changed to the optimized version that was suggested 
* Constant propagation is made for integer and booleans

We also implemented other two optimizations using as reference the following link https://compileroptimizations.com/index.html. These optimizations were: 
* Loop unrolling
* Expression Simplification

These optimizations can be observed by running their correspondent tests in the class of tests related to optimization.

There were also some optimizations done when generating the jasmin code. 

Whenever the compiler finds an instructions the folling format `<variable> = <variable> + constant`, then instead of doing the normal assign operation template which would corresponde to something like this: 

1. iload `<variable> register`
2. iconst `constant value`
3. istore `<variable> register`

Instead it uses the instruction iinc to increment to variable, like this:

1. iinc `<variable> register` `constnat value`

Another optimization done is by using specific instruction to load constants from specific ranges, such as:

1. iconst -> 0 to 5
2. bipush -> -128 to 127
3. sipush -> -32768 to 32767
4. ldc -> for everthing else 


### TASK DISTRIBUTION:

Checkpoint 1:

Grammar:

* Initial Grammar translation: PREENCHER
* Deal with comments in JavaCC: Rúben and José
* Remove Left Recursions: Rúben
* Remove demanding Lookaheads: Rúben
* Remove ambiguity from the grammar related with precedence in expressions: Rúben and José

Error Recovery: José
AST code to file: José

Testing:
Tool to print errors from the Syntactic Analyses: José
Setup the gradle testing properly: José

#### Checkpoint 2:

##### Semantic Analysis:

* Definition of the data structures: José and Luís
* Fill the symbol table(First Visitor): Rúben
* Semantic Analysis of Arithmetic Operations(Second Visitor): Rúben
* Semantic Analysis of Boolean Conditions: Luís
* Semantic Analysis of Method Invocation(Third Visitor): Rúben
* Method Overload adaptation: Luís.
* OLLIR Code Generation: Rúben
* Jasmin Code Generation: José
* Test scripts: Rúben and José

#### Checkpoint 3:

* OLLIR Code Generation: Rúben
* Jasmin Code Generation: José
* Test scripting: Rúben José and César

#### Final Delivery:

* -o and other optimization: Rúben and José


### PROS:

* We can deal with method overload.
* We have done a considerable amount of tests. Besides those already supplied, we also made tests regarding specific and overall compiler features to ensure that this one is working properly.
* The way we show the user the errors is made in a friendly and useful way.
* The compiler creates a file that contains the ast from the Lexical analysis.
* The compiler creates files with the ollir code and jasmin code for a specific .jmm file.
* The compiler is fast to execute
* The -o optimizations, loop unrolling and expression simplification optimizations, were implemented.


### CONS:

* The code could be more well designed. This project was done incrementally, but at the beginning, we were not aware of the overview of tasks we were required to accomplish. This interfered a lot in the project's development and required us to step back to things considered already done that required small arrangements.
* JMM Compiler uses third party code design by teacher João Bispo, His code has its own requirement and specifications. There are certain keywords that could break his code and therefore our project. Those reserved keywords should had been taken into account since the beginning of project in order to force the user to avoid its use. That was not possible to follow.    
* Our code has difficulties in dealing with imported classes and with inherited methods
* JMM Compiler is only able to deal automatically with a single compiled class. This fact makes the solution without any interest outside academia. If there were a feature planned to deal with multiple file compilation, JMM Compiler would compete with any other JMM Compiler in the market.
* Since the specification does not introduce the possibility of having strings, the compiler is very limited in its tasks. It makes it harder to do real-world examples for testing purposes.



