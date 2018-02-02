public class Start {

	static class Utils {
		public static String diff(String expected, String different) {
			StringBuffer sb = new StringBuffer();
			int min = Math.min(expected.length(), different.length());
			for (int i = 0; i < min; i++) {
				if (expected.charAt(i) != different.charAt(i)) {
					sb.append(different.charAt(i));
				}
			}

			return sb.toString();
		}
	}

	private static boolean stateOK = true;

	private static void show( String str ) {
		System.out.println( "O> " + str );
	}
	
	private static void test(SQLiteHelperInterface shi, Object klasa, boolean testCreate) {
		String stm;

		if (testCreate)
			stm = shi.createTable(klasa);
		else
			stm = shi.insert(klasa);
		
		String org = stm;

		String expected = CorrectResults.getExpected(klasa);

		expected = process(expected);
		stm = process(stm);

		// System.out.println("O> " + org);
		System.out.println();
		System.out.println("--------------------------------------------------------");
		System.out.println("Test dla: > " + CorrectResults.objectToClassName(klasa));
		System.out.println("--------------------------------------------------------");
		System.out.println("E> " + expected);
		System.out.println("R> " + stm);

		if (!testIfEquals(stm, expected)) {
			expected = CorrectResults.getExpected(klasa, 1);
			if (expected != null) {
				System.out.println("A> " + expected);
				if (!testIfEquals(stm, expected)) {
					stateOK = false;
					show( org );
				}
				
			} else {
				// brak alternatywy
				stateOK = false;
				show( org );
			}
		}

	}

	private static boolean testIfEquals(String stm, String expected) {
		if (expected.equals(stm)) {
			System.out.println(" ---- OK");
			return true;
		} else {
			System.out.println("#### BLAD: Roznica: " + Utils.diff(expected, stm));
			return false;
		}
	}

	private static String process(String str) {
		String stm = str.toUpperCase();

		stm = stm.trim().replaceAll(" +", " ");
		stm = stm.replace('\n', ' ');
		stm = stm.replace(", ", ",");
		stm = stm.replace(" ,", ",");
		stm = stm.replace("( ", "(");
		stm = stm.replace(" (", "(");
		stm = stm.replace(") ", ")");
		stm = stm.replace(" )", ")");
		stm = stm.replace("; ", ";");
		stm = stm.replace(" ;", ";");
		stm = stm.replace("\"", "'");
		stm = stm.replace(".", ",");
		stm = stm.replace("I2,I1", "I1,I2");

		return stm;
	}

	public static void main(String[] args) {
		SQLiteHelperInterface shi = new SQLiteHelper();

		test(shi, new KlasaZKluczem(), true);
		test(shi, new KlasaZKluczemAI(), true);
		test(shi, new KlasaZKluczemDoZignorowania(), true);
		test(shi, new KlasaZKluczemObcym(), true);
		test(shi, new KlasaZKluczemObcymDoZignorowania(), true);
		test(shi, new KlasaZIndeksem(), true);

		test(shi, new KlasaDoInsertAutoincrement(), false);
		test(shi, new KlasaDoZwyklegoInsert(), false);
		test(shi, new KlasaDoZwyklegoInsert2(), false);
		test(shi, new KlasaDoUpdate(), false);

		if (stateOK) {
			System.out.println(" ----------- OK ----------------");
		} else {
			System.out.println(" ########## BLAD ##########");
		}
	}
}
