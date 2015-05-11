GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Neurolution.java\
	GA.java\
	LayeredNetwork.java\
	Network.java\
	NeuralNetwork.java\
	Perceptron.java\
	Edge.java\
	Problem.java\
	Clause.java
 
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *~
