.PHONY: gui sql-scripts clean

gui:
	cd gui && make

sql-scripts:
	./db-scripts/generate-sql-scripts.py

clean:
	cd gui && make clean
	cd db-scripts && rm -rf gen-sql
