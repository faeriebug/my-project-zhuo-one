package DataPrepare;

/**
 * class that stores the parse result;
 * @author WuyaMony
 *
 */
public class ParseResult {
	public String topics, lewissplit, newid, title, body;

	public ParseResult(String topics, String lewissplit, String newid,
			String title, String body) {
		this.topics = topics;
		this.lewissplit = lewissplit;
		this.newid = newid;
		this.title = title;
		this.body = body;
	}
}
