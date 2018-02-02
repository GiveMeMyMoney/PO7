
public class KlasaZKluczemObcym {
	@KeyAnnotation(autoIncrement=false)
	public int basicKey;
	
	@ForeignKeyAnnotation(foreignColumnName="idx", foreignKeyName="sth", foreignTableName="S")
	public int sth;

}
