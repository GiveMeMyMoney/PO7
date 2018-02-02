import java.util.Map;
import java.util.TreeMap;

public class CorrectResults {
	private static Map<String, String> results = new TreeMap<>();

	static {
		results.put("KlasaZKluczem",
				"CREATE TABLE IF NOT EXISTS KLASAZKLUCZEM(BASICKEY INTEGER NOT NULL PRIMARY KEY,STH INTEGER);");
		results.put("KlasaZKluczemAI",
				"CREATE TABLE IF NOT EXISTS KLASAZKLUCZEMAI(AUTOINCREMENKEY INTEGER PRIMARY KEY AUTOINCREMENT,STH INTEGER);");
		results.put("KlasaZKluczemDoZignorowania",
				"CREATE TABLE IF NOT EXISTS KLASAZKLUCZEMDOZIGNOROWANIA(STH INTEGER);");
		results.put("KlasaZKluczemObcym",
				"CREATE TABLE IF NOT EXISTS KLASAZKLUCZEMOBCYM(BASICKEY INTEGER NOT NULL PRIMARY KEY,STH INTEGER,CONSTRAINT STH FOREIGN KEY(STH)REFERENCES S(IDX));");
		results.put("KlasaZKluczemObcym-1",
				"CREATE TABLE IF NOT EXISTS KLASAZKLUCZEMOBCYM(BASICKEY INTEGER NOT NULL PRIMARY KEY,STH INTEGER,FOREIGN KEY(STH)REFERENCES S(IDX));");
		results.put("KlasaZKluczemObcymDoZignorowania",
				"CREATE TABLE IF NOT EXISTS KLASAZKLUCZEMOBCYMDOZIGNOROWANIA(BASICKEY INTEGER NOT NULL PRIMARY KEY);");
		results.put("KlasaZIndeksem",
				"CREATE TABLE IF NOT EXISTS KLASAZINDEKSEM(I1 INTEGER,I2 INTEGER,I3 INTEGER);CREATE INDEX I1I2 ON KLASAZINDEKSEM(I1,I2);CREATE UNIQUE INDEX I3 ON KLASAZINDEKSEM(I3);");

		results.put("KlasaDoInsertAutoincrement",
				"INSERT INTO KlasaDoInsertAutoincrement(valueI,valueF) VALUES(10,'20.0');");
		results.put("KlasaDoZwyklegoInsert",
				"INSERT INTO KlasaDoZwyklegoInsert(kluczyk,valueI,valueF) VALUES(10,10,'20.0');");
		results.put("KlasaDoZwyklegoInsert-1",
				"INSERT INTO KlasaDoZwyklegoInsert VALUES(10,10,'20.0');");
		results.put("KlasaDoZwyklegoInsert2", "INSERT INTO KlasaDoZwyklegoInsert2(valueI,valueF) VALUES(10,'20.0');");
		results.put("KlasaDoUpdate", "UPDATE KlasaDoUpdate SET valueI=10,valueF='20.0' WHERE kluczyk=10;");
	}

	public static String objectToClassName(Object o) {
		return o.getClass().getCanonicalName();
	}

	public static String getExpected(Object o, int alternatywa) {
		return results.get(objectToClassName(o) + "-" + alternatywa);
	}

	public static String getExpected(Object o) {
		return results.get(objectToClassName(o));
	}

}
