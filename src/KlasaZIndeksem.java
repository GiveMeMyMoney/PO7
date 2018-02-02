
public class KlasaZIndeksem {
	@IndexAnnotation(indexName="i1i2",isUnique=false)
	public int i1;
	@IndexAnnotation(indexName="i1i2",isUnique=false)
	public int i2;

	@IndexAnnotation(indexName="i3",isUnique=true)
	public int i3;
}
