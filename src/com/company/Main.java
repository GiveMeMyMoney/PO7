package com.company;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;


public class Main {

    public static void main(String[] args) {
        SQLiteHelper sqLiteHelper = new SQLiteHelper();
        Cos cos = new Cos();


        //sqLiteHelper.createTable(cos);

        sqLiteHelper.insert(cos);

    }

}

class Cos {
    private Double PRIAVTE = 78.0;
    protected Double PROETECTED = 78.0;
    public Integer pole1 = 1;
    public int pole4 = 4;
    public Boolean pole2 = false;
    public String pole3 = "0.0";
    public float pole5 = 0.55f;
    public double pole6 = 66.66;
    public int pole7;
    public boolean bool = true;
    public List lista = new ArrayList();

}

class SQLiteHelper implements SQLiteHelperInterface {

    /*
    *  CREATE TABLE IF NOT EXISTS A(intfield INTEGER,
           longfield INTEGER,
           floatfield REAL,
           doublefield REAL,
           booleanfield INTEGER);
    * */

    public SQLiteHelper() {
    }

    /**
     * * Jesli pole typu calkowitoliczbowego oznaczone jest za pomoca adnotacji
     * KeyAnnotation, to:
     * <ul>
     * <li>jesli autoIncrement=FALSE, to w linii tworzacej to pole w bazie powinno
     * pojawic sie <tt>INTEGER NOT NULL PRIMARY KEY</tt></li>
     * <li>jesli autoIncrement=TRUE, to pole to tworzone jest za pomocÄ?
     * <tt>INTEGER PRIMARY KEY AUTOINCREMENT</tt></li>
     * </ul>
     * <p>
     * Uwaga: wspierane jest wylacznie tworzenie kluczy skladajacych sie z jednego
     * atrybutu, czyli maksymalnie tylko jedno pole calkowitoliczbowe moze zostac
     * oznaczone za pomoca KeyAnnotation.
     * <p>
     * <br>
     * W przypadu uzycia adnotacji ForeignKeyAnnotation definicja tabeli
     * powinna zawierac sekcje opisujac klucz obcy.
     * <p>
     * <br>
     * W przypadku uzycia adnotacji IndexAnnotation po poleceniu CREATE TABLE
     * powinny zostac dodane dodatkowe polecenia tworzace indeksy.
     * Wszystkie polecenia powinny zostac zakonczone srednikami.
     */
    private String convertTypeJavaToSql(Field field) {
        try {
            Class<?> type = field.getType();
            StringBuilder name = new StringBuilder(field.getName());
            if (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                return name + " INTEGER";
            } else if (Integer.TYPE.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                name.append(" INTEGER");
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(KeyAnnotation.class)) {
                        KeyAnnotation ka = (KeyAnnotation) annotation;
                        if (ka.autoIncrement()) {
                            name.append(" PRIMARY KEY AUTOINCREMENT");
                        } else {
                            name.append(" NOT NULL PRIMARY KEY");
                        }
                    }
                }
                return name + " INTEGER";
            } else if (Float.TYPE.isAssignableFrom(type) || Float.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {

                return name + " REAL";
            } else if (String.class.isAssignableFrom(type)) {
                return name + " TEXT";
            }
        } catch (NullPointerException e) {
            return null;
        }
        return null;
    }

    private String addForeignKeyConstraint(Field field) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(KeyAnnotation.class)) {
                    ForeignKeyAnnotation fka = (ForeignKeyAnnotation) annotation;
                    String foreignKeyName = fka.foreignKeyName();
                    String foreignTableName = fka.foreignTableName();
                    String foreignColumnName = fka.foreignColumnName();
                    //foreignColumnName="idx", foreignKeyName="sth", foreignTableName="S"
                    sb.append("FOREIGN KEY (").append(foreignKeyName).append(")").append(" REFERENCES ").append(foreignTableName)
                            .append("(").append(foreignColumnName).append(")");
                }
            }
            return sb.toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    //TODO KLASAZINDEKSEM(I1,I2) - polaczyc pola oraz rozroznic UNIQUE
    private String addIndexes(Field field, String classWithIndexName) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(KeyAnnotation.class)) {
                    IndexAnnotation ia = (IndexAnnotation) annotation;
                    String indexName = ia.indexName();
                    Boolean isUnique = ia.isUnique();
                    sb.append("CREATE");
                    if (isUnique) {
                        sb.append(" UNIQUE");
                    }
                    sb.append(" INDEX ").append(indexName).append(" ON ").append(classWithIndexName)
                            .append("(").append(field.getName()).append(")");
                }
            }
            return sb.toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private String createTableString(Class clas) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        String tableName = clas.getSimpleName();
        sb.append(tableName).append(" (");

        Field[] fields = clas.getFields();
        List<Field> fieldsList = new LinkedList<>(Arrays.asList(fields));

        String publicFieldNamesWithType = fieldsList.stream()
                .map(this::convertTypeJavaToSql)
                .filter(Objects::nonNull)
                .collect(joining(", "));

        StringBuilder sbForeignKeysAndIndexes = new StringBuilder(publicFieldNamesWithType);
        sbForeignKeysAndIndexes.append(fieldsList.stream()
                .filter(field -> {
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().equals(ForeignKeyAnnotation.class)) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(this::addForeignKeyConstraint)
                .collect(joining(", ")));

        sb.append(publicFieldNamesWithType).append(");");

        sbForeignKeysAndIndexes.append(fieldsList.stream()
                .filter(field -> {
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().equals(IndexAnnotation.class)) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(field -> addIndexes(field, tableName))
                .collect(joining("; ")));

        return sb.toString();
    }


    @Override
    public String createTable(Object o) {
        if (o != null) {
            String createTableSql = createTableString(o.getClass());
            return createTableSql;
        }
        return "";
    }

    /*
    INSERT INTO A(intfield,longfield,floatfield,
                      doublefield,booleanfield)
               VALUES (4,1000,"0,4","0,66",0);
     */

    //TODO refactor
    private String getValueFromField(Object o, Field field) {
        Class<?> type = field.getType();
        if (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type) || Long.class.isAssignableFrom(type) || Float.TYPE.isAssignableFrom(type) || Float.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type) || Double.class.isAssignableFrom(type) || String.class.isAssignableFrom(type)) {
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(o);
                if (value == null) {
                    if (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                        value = "0";
                    } else if (Float.TYPE.isAssignableFrom(type) || Float.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
                        value = "\"0,0\"";
                    } else if (String.class.isAssignableFrom(type)) {
                        value = "\"\"";
                    }
                } else {
                    if (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                        if ((Boolean) value) {
                            value = "1";
                        } else {
                            value = "0";
                        }
                    } else if (Float.TYPE.isAssignableFrom(type) || Float.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
                        String str = String.valueOf(value);
                        str = str.replace(".", ",");
                        StringBuilder sb = new StringBuilder(str);
                        sb.insert(0, "\"").append("\"");
                        value = sb.toString();
                    } else if (String.class.isAssignableFrom(type)) {
                        StringBuilder sb = new StringBuilder(String.valueOf(value));
                        sb.insert(0, "\"").append("\"");
                        value = sb.toString();
                    }
                }
            } catch (IllegalAccessException e) {
                return "\"\"";
            }
            //String none = "\"\"";
            //return value == null ? none : String.valueOf(value);
            return String.valueOf(value);
        }
        return null;
    }

    private String insertIntoTableString(Object o) {
        Class clas = o.getClass();
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        String tableName = clas.getSimpleName();
        sb.append(tableName).append(" VALUES (");

        Field[] fields = clas.getFields();
        List<Field> fieldsList = new LinkedList<>(Arrays.asList(fields));

        String fieldValues = fieldsList.stream()
                .map(field -> this.getValueFromField(o, field))
                .filter(Objects::nonNull)
                .collect(joining(", "));

        sb.append(fieldValues).append(");");

        return sb.toString();
    }

    @Override
    public String insert(Object o) {
        if (o != null) {
            String insertIntoTableString = insertIntoTableString(o);
            return insertIntoTableString;
        }
        return "";
    }
}


interface SQLiteHelperInterface {
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
     * <p>
     * Inne typy i niepubliczne pola sa <b>ignorowane</b>. Nazwa tworzonej kolumny
     * to nazwa pola klasy. Nazwa tworzonej tablicy to nazwa klasy, ktorej obiekt
     * przekazano. Zwracany ciag znakow zaczyna sie zawsze od:
     * <tt>CREATE TABLE IF NOT EXISTS</tt>.
     * <p>
     * Jesli pole typu calkowitoliczbowego oznaczone jest za pomoca adnotacji
     * KeyAnnotation, to:
     * <ul>
     * <li>jesli autoIncrement=FALSE, to w linii tworzacej to pole w bazie powinno
     * pojawic sie <tt>INTEGER NOT NULL PRIMARY KEY</tt></li>
     * <li>jesli autoIncrement=TRUE, to pole to tworzone jest za pomocÄ?
     * <tt>INTEGER PRIMARY KEY AUTOINCREMENT</tt></li>
     * </ul>
     * <p>
     * Uwaga: wspierane jest wylacznie tworzenie kluczy skladajacych sie z jednego
     * atrybutu, czyli maksymalnie tylko jedno pole calkowitoliczbowe moze zostac
     * oznaczone za pomoca KeyAnnotation.
     * <p>
     * <br>
     * W przypadu uzycia adnotacji ForeignKeyAnnotation definicja tabeli
     * powinna zawierac sekcje opisujac klucz obcy.
     * <p>
     * <br>
     * W przypadku uzycia adnotacji IndexAnnotation po poleceniu CREATE TABLE
     * powinny zostac dodane dodatkowe polecenia tworzace indeksy.
     * Wszystkie polecenia powinny zostac zakonczone srednikami.
     *
     * @param o obiekt do analizy
     * @return ciag znakow z przepisem na tabele do przechowania danych z obiektu
     * @see <a href="https://www.sqlite.org/lang_createtable.html">Create table</a>
     * @see <a href="https://www.sqlite.org/datatype3.html">Datatypes In SQLite
     * Version 3</a>
     */
    String createTable(Object o);

    /**
     * Metoda zwraca ciag znakow reprezentujacych operacje INSERT lub UPDATE, ktora
     * ma spowodowac, ze w tabeli o nazwie klasy obiektu pojawia sie wartosci
     * publicznych pol zapisanych o obiekcie lub istniejaca krotka zostanie
     * zmodyfikowana. Zaklada sie, ze stosowna tabela w bazie juz istnieje.
     * <p>
     * <br>
     * Typ logiczny zapisywany jest do kolumny typu calkowitoliczbowego, nalezy
     * dokonac wiec nastepujacej konwersji <tt>true</tt> zapisywane jest jako 1,
     * <tt>false</tt> zapisywane jest jako 0.
     * <p>
     * <br>
     * O tym czy tworzone jest polecenie <tt>INSERT</tt> czy <tt>UPDATE</tt>
     * decyduje istnienie pola calkowitoliczobowego obdarzonego adnotacja
     * KeyAnnotation. I tak:
     * <ul>
     * <li>Jesli pewne pole calkowitoliczbowe jest obdarzone adnotacjÄ? KeyAnnotation
     * z elementem autoIncrement ustawionym na TRUE oraz wartoÅ?ciÄ? tego pola jest
     * zero, to tworzone jest polecenie <tt>INSERT</tt>, w ktorym pomijane jest to
     * pole i stosowane jest polecenie w postaci: <br>
     * <tt>INSERT INTO nazwa_tabeli(lista_atrybutow) VALUES (wartosci_atrybutow);</tt>
     * </li>
     * <p>
     * <li>JeÅ?li wartosc pola z adnotacja KeyAnnotation jest wieksza od zera to
     * zamiast polecenia <tt>INSERT</tt> powinno zostac wygenerowane polecenie
     * <tt>UPDATE</tt>. Ma ono ustawic wartosci wszystkich atrybutow krotki o kluczu
     * rownym wartosci pola z adnotacja KeyAnnotation obiektu, na takie, jakie sa
     * wpisane do tego obiektu. Czyli, jesli klasa zawiera pole o nazwie kluczyk
     * oznaczone adnotacja KeyAnnotation, to tworzone jest polecenie aktualizacji
     * tabeli w postaci <br>
     * <tt>UPDATE nazwa_tabeli SET nazwa_pola1=wartosc_pola1, nazwa_pola2=wartosc_pola2...
     * WHERE kluczyk=wartosc_pola_kluczyk;</tt>.
     * <p>
     * <li>Jesli zadne z pol calkowitoliczbowych nie jest obdarzone adnotacja
     * KeyAnnotation, to generowane jest zwykle polecenie INSERT tworzace w bazie
     * krotke z wszystkich publicznych pol o typie zgodnym z wymienionymi w opisie
     * metody createTable.</li>
     * <p>
     * </ul>
     * <p>
     * Uwaga: podobnie jak w przypadku metody createTable
     * inne typy danych i niepubliczne pola sa <b>ignorowane</b>.
     *
     * @param o obiekt do analizy
     * @return ciag znakow z przepisem na umieszczenie w tabeli danych z
     * przekazanego obiektu
     * @see <a href=
     * "http://www.tutorialspoint.com/sqlite/sqlite_insert_query.htm">Insert</a>
     * @see <a href="https://www.sqlite.org/lang_insert.html">Insert z dokumentacji
     * SQLite</a>
     */
    String insert(Object o);
}

/**
 * Adnotacja uzywana do oznaczenia kolumny (dla uproszczenia jednej) typu
 * integer, dla ktorej ma zostac dodane polecenie utworzenia klucza obcego.
 */
@Retention(RUNTIME)
@Target(FIELD)
@interface ForeignKeyAnnotation {
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


/**
 * Adnotacja uzywana do wskazania kolumn, ktore maja zostac uzyte w tworzonym
 * indeksie. Jesli indexName powtarza sie dla kilku kolumn, oznacza to, ze
 * tworzony jest pod ta nazwa indeks wielokolumnowy. <br>
 * Skladnia polecenia tworzacego indeks w bazie SQLite znajduje sie na stronie
 * <a href="http://www.sqlitetutorial.net/sqlite-index/">sqlite-index</a>. <br>
 * W przypadku gdy pole otrzymuje adnotacje IndexAnnotation z ustawionym
 * elementem isUnique tworzac indeks nalezy dodac slowo kluczowe UNIQUE.
 */
@Retention(RUNTIME)
@Target(FIELD)
@interface IndexAnnotation {
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

/**
 * Adnotacja dla pol klasy - pozwala ona oznaczyc pole, ktore pelni w relacji
 * role klucza. Dodatkowo, istnieje mozliwosc wskazania czy klucz ma wlasnosc
 * autoinkrementacji.
 *
 * @see <a href=
 * "http://tutorials.jenkov.com/java-reflection/annotations.html">Obsluga
 * adnotacji</a>
 */
@Retention(RUNTIME)
@Target(FIELD)
@interface KeyAnnotation {
    /**
     * Informacja czy klucz ma miec ustawiona wlasnosc autoinkrementacji.
     *
     * @return true - autoinkrementacja wlaczona
     */
    public boolean autoIncrement();
}
