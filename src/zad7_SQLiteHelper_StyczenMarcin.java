/*
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

interface SQLiteHelperInterface {
    */
/**
     * Metoda uzywajac refleksji dokonuje inspekcji przekazanego obiektu i generuje
     * ciag znakow tworzacy w bazie danych typy SQLite tabele, ktora pozwoli
     * zachowac dane z tego obiektu.
     * Metoda wyszukuje wylacznie publiczne pola i bada ich typ. Zwracajac
     * rezultat dokonuje nastepujacego powiazania typow Java z typami dostepnymi w SQLite
     * <br>
     * <ul>
     * <li>int,long,Integer,Long -&gt; INTEGER
     * <li>float,double,Float,Double -&gt; REAL
     * <li>String -&gt; TEXT
     * <li>boolean/Boolean -&gt; INTEGER
     * </ul>
     * <p>
     * Inne typy i niepubliczne pola sa <b>ignorowane</b>.
     * Nazwa tworzonej kolumny to nazwa pola klasy. Nazwa tworzonej tablicy
     * to nazwa klasy, ktorej obiekt przekazano.
     * Zwracany ciag znakow zaczyna sie zawsze od: <tt>CREATE TABLE IF NOT EXISTS</tt>
     *
     * @param o obiekt do analizy
     * @return ciag znakow z przepisem na tabele do przechowania danych z obiektu
     * @see <a href="https://www.sqlite.org/lang_createtable.html">Create table</a>
     * @see <a href="https://www.sqlite.org/datatype3.html">Datatypes In SQLite Version 3</a>
     *//*

    String createTable(Object o);

    */
/**
     * Metoda zwraca ciag znakow reprezentujacych operacje INSERT, ktora ma
     * spowodowac, ze w tabeli o nazwie klasy obiektu pojawia sie wartosci publicznych
     * pol zapisanych o obiekcie. Zaklada sie, ze stosowna tabela w bazie juz istnieje.
     * Zwracany ciag znakow zaczyna sie zawsze od: <tt>INSERT INTO</tt>
     * Typ logiczny zapisywany jest do kolumny typu calkowitoliczbowego, nalezy dokonac
     * wiec nastepujacej konwersji <tt>true</tt> zapisywane jest jako 1,
     * <tt>false</tt> zapisywane jest jako 0.
     *
     * @param o obiekt do analizy
     * @return ciag znakow z przepisem na umieszczenie w tabeli danych z przekazanego obiektu
     * @see <a href="http://www.tutorialspoint.com/sqlite/sqlite_insert_query.htm">Insert</a>
     * @see <a href="https://www.sqlite.org/lang_insert.html">Insert z dokumentacji SQLite</a>
     *//*

    String insert(Object o);
}

class SQLiteHelper implements SQLiteHelperInterface {

    */
/*
    *  CREATE TABLE IF NOT EXISTS A(intfield INTEGER,
           longfield INTEGER,
           floatfield REAL,
           doublefield REAL,
           booleanfield INTEGER);
    * *//*


    public SQLiteHelper() {
    }

    private String convertTypeJavaToSql(Field field) {
        try {
            Class<?> type = field.getType();
            String name = field.getName();
            if (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
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

        sb.append(publicFieldNamesWithType).append(");");
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

    */
/*
    INSERT INTO A(intfield,longfield,floatfield,
                      doublefield,booleanfield)
               VALUES (4,1000,"0,4","0,66",0);
     *//*


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


*/
