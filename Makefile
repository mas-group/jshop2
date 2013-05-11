c: src/JSHOP2/JSHOP2.g src/JSHOP2/*.java
	cd src/JSHOP2; java antlr.Tool JSHOP2.g; javac *.java
	cd src; jar cvf JSHOP2.jar JSHOP2/*.class;
	rm src/JSHOP2/*.class
	mv src/JSHOP2.jar bin.build/

clean:
	rm src/JSHOP2/JSHOP2Lexer.*
	rm src/JSHOP2/JSHOP2Parser.*
	rm src/JSHOP2/JSHOP2TokenTypes.java
	rm src/JSHOP2/JSHOP2TokenTypes.txt


d: src/JSHOP2/*.java
	rm -rf doc
	cd src; javadoc -d ../doc -author -version -private JSHOP2

1: bin.build/JSHOP2.jar
	cd examples/blocks; java JSHOP2.InternalDomain blocks
	cd examples/blocks; java JSHOP2.InternalDomain -r problem
	cd examples/blocks; javac gui.java
	cd examples/blocks; java -Xss2048K -Xmx512M gui
	cd examples/blocks; rm blocks.java; rm blocks.txt; rm problem.java; rm *.class

2: bin.build/JSHOP2.jar
	cd examples/basic; java JSHOP2.InternalDomain basic
	cd examples/basic; java JSHOP2.InternalDomain -r problem
	cd examples/basic; javac gui.java
	cd examples/basic; java gui
	cd examples/basic; rm basic.java; rm basic.txt; rm problem.java; rm *.class

3: bin.build/JSHOP2.jar
	cd examples/oldblocks; java JSHOP2.InternalDomain oldblocks
	cd examples/oldblocks; java JSHOP2.InternalDomain -r problem
	cd examples/oldblocks; javac gui.java
	cd examples/oldblocks; java gui
	cd examples/oldblocks; rm oldblocks.java; rm oldblocks.txt; rm problem.java; rm *.class

4: bin.build/JSHOP2.jar
	cd examples/test; java JSHOP2.InternalDomain test
	cd examples/test; java JSHOP2.InternalDomain -r12 problem
	cd examples/test; javac gui.java
	cd examples/test; java gui
	cd examples/test; rm test.java; rm test.txt; rm problem.java; rm *.class

5: bin.build/JSHOP2.jar
	cd examples/logistics; java JSHOP2.InternalDomain logistics
	cd examples/logistics; java JSHOP2.InternalDomain -r problem
	cd examples/logistics; javac gui.java
	cd examples/logistics; java gui
	cd examples/logistics; rm logistics.java; rm logistics.txt; rm problem.java; rm *.class

6: bin.build/JSHOP2.jar
	cd examples/freecell; java JSHOP2.InternalDomain freecell
	cd examples/freecell; java JSHOP2.InternalDomain -r problem
	cd examples/freecell; javac gui.java
	cd examples/freecell; java gui
	cd examples/freecell; rm freecell.java; rm freecell.txt; rm problem.java; rm *.class

7: bin.build/JSHOP2.jar
	cd examples/propagation; java JSHOP2.InternalDomain propagation
	cd examples/propagation; java JSHOP2.InternalDomain -r problem
	cd examples/propagation; javac gui.java
	cd examples/propagation; java gui
	cd examples/propagation; rm propagation.java; rm propagation.txt; rm problem.java; rm *.class

8: bin.build/JSHOP2.jar
	cd examples/forall; java JSHOP2.InternalDomain forall
	cd examples/forall; java JSHOP2.InternalDomain -ra problem
	cd examples/forall; javac gui.java
	cd examples/forall; java gui
	cd examples/forall; rm forallexample.java; rm forallexample.txt; rm problem.java; rm *.class

9: bin.build/JSHOP2.jar
	cd examples/rover; java JSHOP2.InternalDomain rover
	cd examples/rover; java JSHOP2.InternalDomain -r problem
	cd examples/rover; javac gui.java
	cd examples/rover; java -Xmx256M gui
	cd examples/rover; rm rover.java; rm rover.txt; rm problem.java; rm *.class

10: bin.build/JSHOP2.jar
	cd examples/blocks; java JSHOP2.InternalDomain blocks
	cd examples/blocks; java JSHOP2.InternalDomain -ra smallproblem
	cd examples/blocks; javac smallgui.java
	cd examples/blocks; java smallgui
	cd examples/blocks; rm blocks.java; rm blocks.txt; rm smallproblem.java; rm *.class

11: bin.build/JSHOP2.jar
	cd examples/madrts; java JSHOP2.InternalDomain madrts
	cd examples/madrts; java JSHOP2.InternalDomain -ra problem
	cd examples/madrts; javac gui.java
	cd examples/madrts; java gui 
	cd examples/madrts; rm madrts.java; rm madrts.txt; rm problem.java; rm *.class
