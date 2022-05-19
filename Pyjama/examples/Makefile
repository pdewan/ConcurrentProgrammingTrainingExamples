all: HelloWorld.class ArraySum.class Monte.class

HelloWorld.class: HelloWorld.java
	java -jar $(pyjama) HelloWorld.java

ArraySum.class: ArraySum.java
	java -jar $(pyjama) ArraySum.java

Monte.class: Monte.java
	java -jar $(pyjama) Monte.java


.PHONY: clean run

clean:
	rm *.class

run:
	java -cp $(pyjama):./ $(PROG) $(ARGS)
