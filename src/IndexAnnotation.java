import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Adnotacja uzywana do wskazania kolumn, ktore maja zostac uzyte w tworzonym
 * indeksie. Jesli indexName powtarza sie dla kilku kolumn, oznacza to, ze
 * tworzony jest pod ta nazwa indeks wielokolumnowy. <br>
 * Skladnia polecenia tworzacego indeks w bazie SQLite znajduje sie na stronie
 * <a href="http://www.sqlitetutorial.net/sqlite-index/">sqlite-index</a>. <br>
 * W przypadku gdy pole otrzymuje adnotacje IndexAnnotation z ustawionym
 * elementem isUnique tworzac indeks nalezy dodac slowo kluczowe UNIQUE.
 * 
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface IndexAnnotation {
	/**
	 * Nazwa indeksu do utworzenia
	 * 
	 * @return nazwa indeksu
	 */
	String indexName();

	/**
	 * Informacja czy indeks tworzony jest jako UNIQUE
	 * 
	 * @return czy indeks zawiera slowo UNIQUE
	 */
	boolean isUnique();
}
