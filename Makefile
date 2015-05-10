GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Clause.java\
	Edge.java\
	GA.java\
	LayeredNetwork.java\
	Network.java\
	NeuralNetwork.java\
	Neurolution.java\
	Perceptron.java\
	Problem.java\
	
 
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *~
