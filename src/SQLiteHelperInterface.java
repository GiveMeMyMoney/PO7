
public interface SQLiteHelperInterface {
	/**
	 * Metoda uzywajac refleksji dokonuje inspekcji przekazanego obiektu i generuje
	 * ciag znakow tworzacy w bazie danych typy SQLite tabele, ktora pozwoli
	 * zachowac dane z tego obiektu. Metoda wyszukuje wylacznie publiczne pola i
	 * bada ich typ. Zwracajac rezultat dokonuje nastepujacego powiazania typow Java
	 * z typami dostepnymi w SQLite <br>
	 * <ul>
	 * <li>int,long,Integer,Long -&gt; INTEGER
	 * <li>float,double,Float,Double -&gt; REAL
	 * <li>String -&gt; TEXT
	 * <li>boolean/Boolean -&gt; INTEGER
	 * </ul>
	 * 
	 * Inne typy i niepubliczne pola sa <b>ignorowane</b>. Nazwa tworzonej kolumny
	 * to nazwa pola klasy. Nazwa tworzonej tablicy to nazwa klasy, ktorej obiekt
	 * przekazano. Zwracany ciag znakow zaczyna sie zawsze od:
	 * <tt>CREATE TABLE IF NOT EXISTS</tt>.
	 * 
	 * Jesli pole typu calkowitoliczbowego oznaczone jest za pomoca adnotacji
	 * KeyAnnotation, to:
	 * <ul>
	 * <li>jesli autoIncrement=FALSE, to w linii tworzacej to pole w bazie powinno
	 * pojawic sie <tt>NOT NULL INTEGER PRIMARY KEY</tt></li>
	 * <li>jesli autoIncrement=TRUE, to pole to tworzone jest za pomocą
	 * <tt>INTEGER PRIMARY KEY AUTOINCREMENT</tt></li>
	 * </ul>
	 * 
	 * Uwaga: wspierane jest wylacznie tworzenie kluczy skladajacych sie z jednego
	 * atrybutu, czyli maksymalnie tylko jedno pole calkowitoliczbowe moze zostac
	 * oznaczone za pomoca KeyAnnotation.
	 * 
	 * <br>
	 * W przypadu uzycia adnotacji ForeignKeyAnnotation definicja tabeli
	 * powinna zawierac sekcje opisujac klucz obcy.

	 * <br>
	 * W przypadku uzycia adnotacji IndexAnnotation po poleceniu CREATE TABLE
	 * powinny zostac dodane dodatkowe polecenia tworzace indeksy.
	 * Wszystkie polecenia powinny zostac zakonczone srednikami.
	 * 
	 * 
	 * @param o
	 *            obiekt do analizy
	 * @return ciag znakow z przepisem na tabele do przechowania danych z obiektu
	 * 
	 * @see <a href="https://www.sqlite.org/lang_createtable.html">Create table</a>
	 * @see <a href="https://www.sqlite.org/datatype3.html">Datatypes In SQLite
	 *      Version 3</a>
	 * 
	 */
	String createTable(Object o);

	/**
	 * Metoda zwraca ciag znakow reprezentujacych operacje INSERT lub UPDATE, ktora
	 * ma spowodowac, ze w tabeli o nazwie klasy obiektu pojawia sie wartosci
	 * publicznych pol zapisanych o obiekcie lub istniejaca krotka zostanie
	 * zmodyfikowana. Zaklada sie, ze stosowna tabela w bazie juz istnieje.
	 * 
	 * <br>
	 * Typ logiczny zapisywany jest do kolumny typu calkowitoliczbowego, nalezy
	 * dokonac wiec nastepujacej konwersji <tt>true</tt> zapisywane jest jako 1,
	 * <tt>false</tt> zapisywane jest jako 0.
	 * 
	 * <br>
	 * O tym czy tworzone jest polecenie <tt>INSERT</tt> czy <tt>UPDATE</tt>
	 * decyduje istnienie pola calkowitoliczobowego obdarzonego adnotacja
	 * KeyAnnotation. I tak:
	 * <ul>
	 * <li>Jesli pewne pole calkowitoliczbowe jest obdarzone adnotacją KeyAnnotation
	 * z elementem autoIncrement ustawionym na TRUE oraz wartością tego pola jest
	 * zero, to tworzone jest polecenie <tt>INSERT</tt>, w ktorym pomijane jest to
	 * pole i stosowane jest polecenie w postaci: <br>
	 * <tt>INSERT INTO nazwa_tabeli(lista_atrybutow) VALUES (wartosci_atrybutow);</tt>
	 * </li>
	 * 
	 * <li>Jeśli wartosc pola z adnotacja KeyAnnotation jest wieksza od zera to
	 * zamiast polecenia <tt>INSERT</tt> powinno zostac wygenerowane polecenie
	 * <tt>UPDATE</tt>. Ma ono ustawic wartosci wszystkich atrybutow krotki o kluczu
	 * rownym wartosci pola z adnotacja KeyAnnotation obiektu, na takie, jakie sa
	 * wpisane do tego obiektu. Czyli, jesli klasa zawiera pole o nazwie kluczyk
	 * oznaczone adnotacja KeyAnnotation, to tworzone jest polecenie aktualizacji
	 * tabeli w postaci <br>
	 * <tt>UPDATE nazwa_tabeli SET nazwa_pola1=wartosc_pola1, nazwa_pola2=wartosc_pola2... 
	 * WHERE kluczyk=wartosc_pola_kluczyk;</tt>.
	 * 
	 * <li>Jesli zadne z pol calkowitoliczbowych nie jest obdarzone adnotacja
	 * KeyAnnotation, to generowane jest zwykle polecenie INSERT tworzace w bazie
	 * krotke z wszystkich publicznych pol o typie zgodnym z wymienionymi w opisie
	 * metody createTable.</li>
	 * 
	 * </ul>
	 * 
	 * @param o
	 *            obiekt do analizy
	 * @return ciag znakow z przepisem na umieszczenie w tabeli danych z
	 *         przekazanego obiektu
	 * 
	 * @see <a href=
	 *      "http://www.tutorialspoint.com/sqlite/sqlite_insert_query.htm">Insert</a>
	 * @see <a href="https://www.sqlite.org/lang_insert.html">Insert z dokumentacji
	 *      SQLite</a>
	 */
	String insert(Object o);
}
