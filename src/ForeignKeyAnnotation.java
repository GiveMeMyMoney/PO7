import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Adnotacja uzywana do oznaczenia kolumny (dla uproszczenia jednej) typu
 * integer, dla ktorej ma zostac dodane polecenie utworzenia klucza obcego.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ForeignKeyAnnotation {
	/**
	 * Nazwa oraniczenia
	 * 
	 * @return nazwa dla klucz obcego
	 */
	String foreignKeyName();

	/**
	 * Nazwa kolumny w tabeli foreignTableName, do ktorej odnosi sie tworzony klucz
	 * obcy
	 * 
	 * @return nazwa kolumny
	 */
	String foreignColumnName();

	/**
	 * Nazwa tabeli, w ktorej znajduje sie kolumna foreignColumnName.
	 * 
	 * @return nazwa tabeli, ktora wskazuje klucz obcy
	 */
	String foreignTableName();
}
