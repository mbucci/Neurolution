GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Neurolution.java\
	GA.java\
	TwoLayerPerceptron.java\
	LayeredNetwork.java\
	Perceptron.java\
	NetworkLayer.java\
	Edge.java\
	Problem.java\
	Clause.java
 
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *~
