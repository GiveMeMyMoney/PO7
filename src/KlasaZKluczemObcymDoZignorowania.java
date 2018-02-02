
public class KlasaZKluczemObcymDoZignorowania {
	@KeyAnnotation(autoIncrement=false)
	public int basicKey;
	
	@ForeignKeyAnnotation(foreignColumnName="idx", foreignKeyName="sth", foreignTableName="S")
	int sth;

}
