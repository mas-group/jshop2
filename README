How to install and run JSHOP2:

Make sure that Java is installed fully on your computer.

After unzipping the JSHOP2 zipped file in any directory, set the CLASSPATH
environment variable to include (replacing JSHOP2_DIRECTORY with the directory
where JSHOP2 is unzipped):

- in Windows:
  JSHOP2_DIRECTORY\bin\antlr.jar;JSHOP2_DIRECTORY\bin\JSHOP2.jar;.

- in UNIX:
  JSHOP2_DIRECTORY/bin/antlr.jar:JSHOP2_DIRECTORY/bin/JSHOP2.jar:.

This is the environment variable Java uses to looks for Java ARchive (jar)
files used by JSHOP2.

After making sure the CLASSPATH variable is correctly set, any of these
commands can be used at the command line:

- make
  or
  make c : To compile the JSHOP2 source files and make the needed .jar files.
           This has to be done before JSHOP2 can be run.

- make d : To make the HTML JSHOP2 package documentation out of the source
           files.

- make 1 : To run the first example, the blocks world domain.
           WARNING: This is a very big problem, so it will take a long time to
           solve the problem and display the results.

- make 2 : To run the second example, the basic domain.

- make 3 : To run the third example, the old implementation of the blocks
           world.

- make 4 : To run the fourth example, the test domain.

- make 5 : To run the fifth example, the logistics domain.

- make 6 : To run the sixth example, the freecell domain.

- make 7 : To run the seventh example, the propagation domain.

- make 8 : To run the eighth example, the forallexample domain.

- make 9 : To run the ninth example, the rover domain.
           WARNING: This is a very big problem, so it will take a long time to
           solve the problem and display the results.

- make 10: To run the tenth example, a very small blocks world problem where
           all plans, rather than just the first one found, are to be returned.

- make 11: To run the eleventh example, the MadRTS domain.

-----------------------------------------------------------------
The important files and directories in this release of JSHOP2:

./make.bat : The make file for windows.
./Makefile : The make file for Unix.
./README   : This file.
./JSHOP2.pdf : The JSHOP2 user's manual.
./src/JSHOP2 : The source code of the JSHOP2 package.
./bin        : The directory where the compiled jar files are stored.
./doc/index.html : The HTML package documentation of JSHOP2.
./examples/blocks : The blocks world domain featuring list manipulation in
                    JSHOP2, use of axioms, and on-the-fly creation of the
                    logical atoms at run time.
./examples/basic : A very simple domain, ideal to see how it all works and for
                   debugging purposes.
./examples/oldblocks : An older implementation of the blocks world domain,
                       featuring on-the-fly creation of operator delete and add
                       lists at run time.
./examples/test : A synthesized domain featuring various more complex features
                  of JSHOP2 including code calls, disjunctions, if-then-else
                  structure of the method and axiom branches, :sort-by and
                  :first logical preconditions, etc. Have fun deciphering it.
./examples/logistics : The logistics domain featuring protections, unordered
                       task lists, immediate tasks, and on-the-fly creation of
                       the predicates at run time.
./examples/freecell : The freecell domain featuring unordered task lists,
                      immediate tasks, code calls, and use of ForAll in
                      delete/add list of operators.
./examples/propagation : A very simple domain showing how variable bindings
                         propagate only down in the task network.
./examples/forall : A very simple domain that uses forall constructs.
./examples/rover : Our implementation of the Rovers domain, used in the Third
                   International Planning Competition.
./examples/madrts : A very simple version of the MadRTS game.
