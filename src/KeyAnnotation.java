import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Adnotacja dla pol klasy - pozwala ona oznaczyc pole, ktore pelni w relacji
 * role klucza. Dodatkowo, istnieje mozliwosc wskazania czy klucz ma wlasnosc
 * autoinkrementacji.
 * 
 * @see <a href=
 *      "http://tutorials.jenkov.com/java-reflection/annotations.html">Obsluga
 *      adnotacji</a>
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface KeyAnnotation {
	/**
	 * Informacja czy klucz ma miec ustawiona wlasnosc autoinkrementacji.
	 * 
	 * @return true - autoinkrementacja wlaczona
	 */
	public boolean autoIncrement();
}
