GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	NeuralNetwork.java\
	Network.java\
	Neurolution.java\
	Edge.java\
	Problem.java\
	Clause.java
 
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *~
