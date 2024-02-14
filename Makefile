# Makefile for compiling Java player

# Define the Java compiler
JAVAC = javac

# Define the Java source files
SOURCES = player.java playerhelper.java state.java

# Define the Java classpath (if needed)
CLASSPATH =

# Define the main class (entry point) for your Java program
MAIN_CLASS = player

# Define the Java flags and options
JFLAGS =

# Define the targets and dependencies
all: $(SOURCES)
	$(JAVAC) $(JFLAGS) $(SOURCES)

run: all
	java $(CLASSPATH) $(MAIN_CLASS)

clean:
	rm -f *.class

.PHONY: all run clean
