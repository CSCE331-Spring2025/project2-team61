.PHONY: run docs clean

PSQL_JAR=./jar/postgresql-42.2.8.jar

SRC_DIR = .

SRC_FILES = $(wildcard $(SRC_DIR)/*.java)
CLASS_FILES = $(patsubst $(SRC_DIR)/%.java, $(SRC_DIR)/%.class, $(SRC_FILES))

run: $(CLASS_FILES)
	java -cp ".:$(PSQL_JAR)" Main

$(SRC_DIR)/%.class: $(SRC_DIR)/%.java
	javac $<

docs: $(SRC_FILES)
	javadoc -d ../docs *.java	

clean:
	rm -f $(SRC_DIR)/*.class
