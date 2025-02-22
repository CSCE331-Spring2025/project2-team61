.PHONY: run clean

PSQL_JAR=./jar/postgresql-42.2.8.jar

SRC_DIR = .

SRC_FILES = $(wildcard $(SRC_DIR)/*.java)
CLASS_FILES = $(patsubst $(SRC_DIR)/%.java, $(SRC_DIR)/%.class, SRC_FILES)

run: $(CLASS_FILES)
	java -cp ".:$(PSQL_JAR)" LoginScreen

$(CLASS_FILES): $(SRC_FILES)
	javac *.java

clean:
	rm -f *.class
